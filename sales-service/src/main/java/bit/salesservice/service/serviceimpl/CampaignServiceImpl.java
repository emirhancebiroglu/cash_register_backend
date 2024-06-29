package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.dto.kafka.CampaignDTO;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.exceptions.campaignnotfound.CampaignNotFoundException;
import bit.salesservice.exceptions.fixedamountdiscounttypewithprovidedquantity.FixedAmountDiscountTypeWithProvidedQuantityException;
import bit.salesservice.exceptions.invaliddiscountamount.InvalidDiscountAmountException;
import bit.salesservice.exceptions.invaliddiscounttype.InvalidDiscountTypeException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.service.CampaignService;
import bit.salesservice.utils.CampaignProducer;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.validators.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final ProductInfoHttpRequest info;
    private final CampaignValidator campaignValidator;
    private final CampaignProducer campaignProducer;
    private static final Logger logger = LogManager.getLogger(CampaignServiceImpl.class);
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);

    private static final String NOT_FOUND = "Campaign not found";
    private static final String STATUS_ACTIVE = "active";

    @Override
    public void addCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        logger.trace("Adding campaign...");

        // Check if the required products for the campaign are available
        checkIfProductsAvailable(addAndUpdateCampaignReq)
                .doOnError(error -> {
                    logger.error("Error occurred while checking products availability: {}", error.getMessage());
                    throw new ProductNotFoundException(error.getMessage());
                })
                .block();

        // Validate the campaign DTO
        campaignValidator.validateCampaignDTO(addAndUpdateCampaignReq, campaignRepository);

        // Map DTO to Campaign entity
        Campaign campaign = mapToCampaign(addAndUpdateCampaignReq);

        // Save the campaign
        campaignRepository.save(campaign);

        // Send campaign information to reporting service
        sendCampaignInfoToReportingService(campaign);

        logger.trace("Campaign added successfully");
    }

    @Override
    public void updateCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq, Long campaignId) {
        logger.trace("Updating campaign...");

        // Retrieve the existing campaign from the repository
        Campaign existingCampaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new CampaignNotFoundException(NOT_FOUND);
                });

        logger.debug("Retrieved existing campaign: {}", existingCampaign);

        // Ensure the campaign is active and there is no conflict before proceeding with the update
        campaignValidator.validateActivation(existingCampaign);
        campaignValidator.validateCampaignName(addAndUpdateCampaignReq, campaignRepository);

        // Check and update product codes if provided
        if (!addAndUpdateCampaignReq.getCodes().isEmpty()){
            checkIfProductsAvailable(addAndUpdateCampaignReq)
                    .doOnError(error -> {
                        logger.error("Error occurred while checking products availability: {}", error.getMessage());
                        throw new ProductNotFoundException(error.getMessage());
                    })
                    .block();

            campaignValidator.validateProductCodes(addAndUpdateCampaignReq.getCodes(), campaignRepository);
            existingCampaign.setCodes(addAndUpdateCampaignReq.getCodes());

            // Log the updated product codes for debugging purposes
            logger.debug("Updated product codes: {}", addAndUpdateCampaignReq.getCodes());
        }

        // Update campaign name if provided and different from the current name
        if (!Objects.equals(existingCampaign.getName(), addAndUpdateCampaignReq.getName()) && !addAndUpdateCampaignReq.getName().isEmpty()){
            existingCampaign.setName(addAndUpdateCampaignReq.getName());
            logger.debug("Updated campaign name: {}", addAndUpdateCampaignReq.getName());
        }

        // Update campaign duration and end date if provided and different from the current duration
        if (addAndUpdateCampaignReq.getDurationDays() != null && (!Objects.equals(existingCampaign.getEndDate(), existingCampaign.getStartDate().plusDays(addAndUpdateCampaignReq.getDurationDays())))){
            campaignValidator.validateDurationDays(addAndUpdateCampaignReq);
            existingCampaign.setDurationDays(addAndUpdateCampaignReq.getDurationDays());
            existingCampaign.setEndDate(existingCampaign.getStartDate().plusDays(addAndUpdateCampaignReq.getDurationDays()));
            logger.debug("Updated campaign end date: {}", existingCampaign.getEndDate());
        }

        // Update discount type if provided and different from the current type
        if (!addAndUpdateCampaignReq.getDiscountType().isEmpty()){
            DiscountType discountType = getDiscountType(addAndUpdateCampaignReq);

            if (!Objects.equals(existingCampaign.getDiscountType(), discountType)){
                existingCampaign.setDiscountType(discountType);
                logger.debug("Updated discount type: {}", discountType);
            }
        }

        // Update discount amount if provided and different from the current amount
        if (addAndUpdateCampaignReq.getDiscountAmount() != null && !Objects.equals(existingCampaign.getDiscountAmount(), addAndUpdateCampaignReq.getDiscountAmount())){
            campaignValidator.validateDiscountAmount(addAndUpdateCampaignReq);
            existingCampaign.setDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());

            logger.debug("Updated discount amount: {}", addAndUpdateCampaignReq.getDiscountAmount());
        }

        // Update needed quantity if provided and different from the current quantity
        if (addAndUpdateCampaignReq.getNeededQuantity() != null && !Objects.equals(existingCampaign.getNeededQuantity(), addAndUpdateCampaignReq.getNeededQuantity())){
            campaignValidator.validateNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());
            existingCampaign.setNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());
            logger.debug("Updated needed quantity: {}", addAndUpdateCampaignReq.getNeededQuantity());
        }

        // Update the updated date and save the changes to the repository
        existingCampaign.setUpdatedDate(LocalDateTime.now());
        campaignRepository.save(existingCampaign);

        // Send updated campaign information to the reporting service
        sendCampaignInfoToReportingService(existingCampaign);

        logger.trace("Campaign updated successfully");
    }

    @Override
    public void inactivateCampaign(Long campaignId) {
        logger.trace("Inactivating campaign...");

        // Retrieve the campaign from the repository
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new CampaignNotFoundException(NOT_FOUND);
                });

        logger.debug("Retrieved campaign: {}", campaign);

        // Check if the campaign is already inactive
        campaignValidator.validateActivation(campaign);

        // Set the campaign as inactive
        campaign.setInactive(true);

        // Save the changes to the repository
        campaignRepository.save(campaign);

        logger.trace("Campaign inactivated successfully");
    }

    @Override
    public void reactivateCampaign(Long campaignId, Integer durationDays) {
        logger.trace("Reactivating campaign...");

        // Retrieve the campaign from the repository
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new CampaignNotFoundException(NOT_FOUND);
                });

        logger.debug("Retrieved campaign: {}", campaign);

        // Check if the campaign is already inactive
        campaignValidator.validateInactivation(campaign);

        // Set the campaign as inactive
        campaign.setInactive(false);
        campaign.setDurationDays(durationDays);
        campaign.setEndDate(LocalDateTime.now().plusDays(durationDays));
        campaign.setStartDate(LocalDateTime.now());
        campaign.setUpdatedDate(LocalDateTime.now());

        // Save the changes to the repository
        campaignRepository.save(campaign);

        logger.trace("Campaign reactivated successfully");
    }

    @Override
    public List<ListCampaignsReq> getCampaigns(int pageNo, int pageSize, String discountType, String status, String searchingTerm, String sortBy, String sortOrder) {
        logger.trace("Getting campaigns...");

        // Apply pagination and sorting parameters
        Pageable pageable = applySort(pageNo, pageSize, sortBy, sortOrder);

        // Variables to store query results
        Page<Campaign> campaignPage;

        DiscountType parsedDiscountType = campaignValidator.validateDiscountTypeAndStatus(discountType, status);

        logger.debug("Validated discount type: {}, status: {}", parsedDiscountType, status);

        // Determine the appropriate query based on input parameters
        if (parsedDiscountType != null && status != null && searchingTerm != null) {
            campaignPage = campaignRepository.findAllByDiscountTypeAndIsInactiveAndNameContaining(parsedDiscountType, !status.equals(STATUS_ACTIVE), searchingTerm, pageable);
        } else if (parsedDiscountType != null && status != null) {
            campaignPage = campaignRepository.findAllByDiscountTypeAndIsInactive(parsedDiscountType, !status.equals(STATUS_ACTIVE), pageable);
        } else if (parsedDiscountType != null && searchingTerm != null) {
            campaignPage = campaignRepository.findAllByDiscountTypeAndNameContaining(parsedDiscountType, searchingTerm, pageable);
        } else if (status != null && searchingTerm != null) {
            campaignPage = campaignRepository.findAllByisInactiveAndNameContaining(status.equals(STATUS_ACTIVE), searchingTerm, pageable);
        } else if (parsedDiscountType != null) {
            campaignPage = campaignRepository.findAllByDiscountType(parsedDiscountType, pageable);
        } else if (status != null){
            campaignPage = (status.equals(STATUS_ACTIVE)) ? campaignRepository.findAllByisInactive(false, pageable) : campaignRepository.findAllByisInactive(true, pageable);
        } else if (searchingTerm != null) {
            campaignPage = campaignRepository.findByNameContaining(pageable, searchingTerm);
        } else{
            campaignPage = campaignRepository.findAll(pageable);
        }

        // Map the retrieved campaigns to DTOs\
        List<ListCampaignsReq> listCampaignsReqList = campaignPage.getContent().stream()
                .map(this::mapToCampaignReq)
                .toList();

        logger.trace("Campaigns retrieved successfully");

        return listCampaignsReqList;
    }

    /**
     * This method applies sorting parameters to the campaigns retrieved from the database.
     *
     * @param pageNo The page number for pagination.
     * @param pageSize The number of campaigns per page.
     * @param sortBy The field to sort the campaigns by. It can be either "name" or "end_date".
     * @param sortOrder The order to sort the campaigns by. It can be either "ASC" or "DESC".
     * @return A Pageable object with the applied sorting parameters.
     */
    @NotNull
    private static Pageable applySort(int pageNo, int pageSize, String sortBy, String sortOrder) {
        // Default sort by name if sortBy is not specified or invalid
        Sort sort = Sort.by(Sort.Direction.ASC, "name");

        if (sortBy != null) {
            sort = switch (sortBy.toLowerCase()) {
                case "name" ->
                        Sort.by(sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "name");
                case "end_date" ->
                        Sort.by(sortOrder.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, "endDate");
                default -> sort;
            };
        }

        // Create pageable object with sorting parameters
        return PageRequest.of(pageNo, pageSize, sort);
    }

    /**
     * This method maps a Campaign entity to a ListCampaignsReq DTO.
     *
     * @param campaign The Campaign entity to be mapped.
     * @return A ListCampaignsReq DTO containing the details of the given Campaign entity.
     */
    private ListCampaignsReq mapToCampaignReq(Campaign campaign) {
        ListCampaignsReq listCampaignsReq = new ListCampaignsReq();

        listCampaignsReq.setName(campaign.getName());
        listCampaignsReq.setCodes(campaign.getCodes());
        listCampaignsReq.setDurationDays(campaign.getDurationDays());
        listCampaignsReq.setDiscountType(campaign.getDiscountType().toString());
        listCampaignsReq.setDiscountAmount(campaign.getDiscountAmount());
        listCampaignsReq.setInactive(campaign.isInactive());
        listCampaignsReq.setStartDate(campaign.getStartDate());
        campaign.setEndDate(campaign.getEndDate());

        return listCampaignsReq;
    }

    /**
     * This method converts a string representation of a discount type to its corresponding enum value.
     *
     * @param addAndUpdateCampaignReq The request object containing the discount type as a string.
     * @return The corresponding enum value of the discount type.
     * @throws InvalidDiscountTypeException If the provided discount type is not a valid enum value.
     */
    private static DiscountType getDiscountType(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        DiscountType discountType;

        try {
            discountType = DiscountType.valueOf(addAndUpdateCampaignReq.getDiscountType());
        }
        catch (IllegalArgumentException e){
            logger.error("Invalid discount type");
            throw new InvalidDiscountTypeException("Invalid discount type");
        }
        return discountType;
    }

    /**
     * This method checks the availability of each product code asynchronously.
     * It fetches product information for each code and throws a ProductNotFoundException
     * if a product does not exist.
     *
     * @param addAndUpdateCampaignReq The request object containing the product codes.
     * @return A Mono<Void> indicating completion of the asynchronous checks.
     */
    private Mono<Void> checkIfProductsAvailable(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        // Check the availability of each product code asynchronously
        return Flux.fromIterable(addAndUpdateCampaignReq.getCodes())
                .flatMap(code -> {
                    final String productCode = code;

                    // Fetch product information for the given code
                    return Mono.just(info.getProductInfo(productCode, jwtToken))
                            .flatMap(productInfo -> {
                                // If product does not exist, throw ProductNotFoundException
                                if (!productInfo.isExists()) {
                                    logger.error("Product not found : {}", productCode);
                                    return Mono.error(new ProductNotFoundException("Product not found: " + productCode));
                                }

                                if (addAndUpdateCampaignReq.getDiscountAmount() >= productInfo.getPrice() && addAndUpdateCampaignReq.getDiscountType().equals("FIXED_AMOUNT")){
                                    logger.error("The discount amount cannot be equal to or greater than the price of the product");
                                    return Mono.error(new InvalidDiscountAmountException("The discount amount cannot be equal to or greater than the price of the product"));
                                }
                                return Mono.empty();
                            });
                })
                // Combine all results into a single Mono<Void> indicating completion
                .then();
    }

    /**
     * This method maps a request object to a Campaign entity.
     *
     * @param addAndUpdateCampaignReq The request object containing campaign details.
     * @return A Campaign entity with the details from the request object.
     * @throws FixedAmountDiscountTypeWithProvidedQuantityException If the discount type is fixed amount and the needed quantity is more than 1.
     */
    private Campaign mapToCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        // Extract discount type from the request
        DiscountType discountType = getDiscountType(addAndUpdateCampaignReq);

        // Extract duration days from the request
        Integer durationDays = addAndUpdateCampaignReq.getDurationDays();

        // Create a new Campaign entity
        Campaign campaign = new Campaign();

        // Set campaign details from the request
        campaign.setCodes(addAndUpdateCampaignReq.getCodes());
        campaign.setName(addAndUpdateCampaignReq.getName());
        campaign.setStartDate(LocalDateTime.now());
        campaign.setUpdatedDate(LocalDateTime.now());
        campaign.setDurationDays(durationDays);
        campaign.setEndDate(LocalDateTime.now().plusDays(durationDays));
        campaign.setDiscountType(discountType);
        campaign.setDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());

        // Set needed quantity for the campaign, with validation for fixed amount discount type
        if (addAndUpdateCampaignReq.getNeededQuantity() != null){
            if (discountType == DiscountType.FIXED_AMOUNT && addAndUpdateCampaignReq.getNeededQuantity() > 1){
                logger.error("Fixed amount discount type should not require more than 1 quantity for the campaign");
                throw new FixedAmountDiscountTypeWithProvidedQuantityException("Fixed amount discount type should not require more than 1 quantity for the campaign");
            }
            campaign.setNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());
        }
        else{
            campaign.setNeededQuantity(1);
        }
        return campaign;
    }

    /**
     * This method sends campaign information to the reporting service.
     *
     * @param campaign The campaign entity from which the information is extracted.
     */
    private void sendCampaignInfoToReportingService(Campaign campaign){
        // Create a CampaignDTO object from the Campaign entity
        CampaignDTO campaignDTO = new CampaignDTO(
                campaign.getName(),
                campaign.getDiscountType(),
                campaign.getDiscountAmount(),
                campaign.getNeededQuantity()
        );

        // Send the campaign information to the reporting service
        campaignProducer.sendCampaign("campaign", campaignDTO);
    }
}