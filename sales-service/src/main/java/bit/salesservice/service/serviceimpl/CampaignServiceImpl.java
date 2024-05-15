package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.dto.kafka.CampaignDTO;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.exceptions.campaignnotfound.CampaignNotFoundException;
import bit.salesservice.exceptions.fixedamountdiscounttypewithprovidedquantity.FixedAmountDiscountTypeWithProvidedQuantityException;
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

/**
 * Service implementation for managing campaign operations.
 */
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

        // Log the validation success as debug information
        logger.debug("Campaign DTO validation successful");

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

        // Log the retrieved campaign details for debugging purposes
        logger.debug("Retrieved existing campaign: {}", existingCampaign);

        // Ensure the campaign is active before proceeding with the update
        campaignValidator.validateActivation(existingCampaign);

        // Log the validation success as debug information
        logger.debug("Campaign activation validation successful");

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

            // Log the updated campaign name for debugging purposes
            logger.debug("Updated campaign name: {}", addAndUpdateCampaignReq.getName());
        }

        // Update campaign duration and end date if provided and different from the current duration
        if (addAndUpdateCampaignReq.getDurationDays() != null && (!Objects.equals(existingCampaign.getEndDate(), existingCampaign.getStartDate().plusDays(addAndUpdateCampaignReq.getDurationDays())))){
            logger.debug("Updating campaign duration and end date...");

            existingCampaign.setDurationDays(addAndUpdateCampaignReq.getDurationDays());
            existingCampaign.setEndDate(existingCampaign.getStartDate().plusDays(addAndUpdateCampaignReq.getDurationDays()));

            // Log the updated campaign duration and end date for debugging purposes
            logger.debug("Updated campaign duration: {} days", addAndUpdateCampaignReq.getDurationDays());
            logger.debug("Updated campaign end date: {}", existingCampaign.getEndDate());
        }

        // Update discount type if provided and different from the current type
        if (!addAndUpdateCampaignReq.getDiscountType().isEmpty()){
            logger.debug("Updating discount type...");
            DiscountType discountType = getDiscountType(addAndUpdateCampaignReq);

            if (!Objects.equals(existingCampaign.getDiscountType(), discountType)){
                existingCampaign.setDiscountType(discountType);

                // Log the updated discount type for debugging purposes
                logger.debug("Updated discount type: {}", discountType);
            }
        }

        // Update discount amount if provided and different from the current amount
        if (addAndUpdateCampaignReq.getDiscountAmount() != null && !Objects.equals(existingCampaign.getDiscountAmount(), addAndUpdateCampaignReq.getDiscountAmount())){
            logger.debug("Updating discount amount...");
            campaignValidator.validateDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());
            existingCampaign.setDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());

            // Log the updated discount amount for debugging purposes
            logger.debug("Updated discount amount: {}", addAndUpdateCampaignReq.getDiscountAmount());
        }

        // Update needed quantity if provided and different from the current quantity
        if (addAndUpdateCampaignReq.getNeededQuantity() != null && !Objects.equals(existingCampaign.getNeededQuantity(), addAndUpdateCampaignReq.getNeededQuantity())){
            logger.debug("Updating needed quantity...");
            existingCampaign.setNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());

            // Log the updated needed quantity for debugging purposes
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

        // Log the retrieved campaign details for debugging purposes
        logger.debug("Retrieved campaign: {}", campaign);

        // Check if the campaign is already inactive
        campaignValidator.validateActivation(campaign);

        // Log the validation success as debug information
        logger.debug("Campaign activation validation successful");

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

        // Log the retrieved campaign details for debugging purposes
        logger.debug("Retrieved campaign: {}", campaign);

        // Check if the campaign is already inactive
        campaignValidator.validateInactivation(campaign);

        // Log the validation success as debug information
        logger.debug("Campaign inactivation validation successful");

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

        // Log the validated discount type and status for debugging purposes
        logger.debug("Validated discount type: {}, status: {}", parsedDiscountType, status);

        // Determine the appropriate query based on input parameters
        if (parsedDiscountType != null && status != null && searchingTerm != null) {
            logger.debug("Querying campaigns by discount type, status, and searching term...");
            campaignPage = campaignRepository.findAllByDiscountTypeAndIsInactiveAndNameContaining(parsedDiscountType, !status.equals(STATUS_ACTIVE), searchingTerm, pageable);
        } else if (parsedDiscountType != null && status != null) {
            logger.debug("Querying campaigns by discount type and status...");
            campaignPage = campaignRepository.findAllByDiscountTypeAndIsInactive(parsedDiscountType, !status.equals(STATUS_ACTIVE), pageable);
        } else if (parsedDiscountType != null && searchingTerm != null) {
            logger.debug("Querying campaigns by discount type and searching term...");
            campaignPage = campaignRepository.findAllByDiscountTypeAndNameContaining(parsedDiscountType, searchingTerm, pageable);
        } else if (status != null && searchingTerm != null) {
            logger.debug("Querying campaigns by status and searching term...");
            campaignPage = campaignRepository.findAllByisInactiveAndNameContaining(status.equals(STATUS_ACTIVE), searchingTerm, pageable);
        } else if (parsedDiscountType != null) {
            logger.debug("Querying campaigns by discount type...");
            campaignPage = campaignRepository.findAllByDiscountType(parsedDiscountType, pageable);
        } else if (status != null){
            logger.debug("Querying campaigns by status...");
            campaignPage = (status.equals(STATUS_ACTIVE)) ? campaignRepository.findAllByisInactive(false, pageable) : campaignRepository.findAllByisInactive(true, pageable);
        } else if (searchingTerm != null) {
            logger.debug("Querying campaigns by searching term...");
            campaignPage = campaignRepository.findByNameContaining(pageable, searchingTerm);
        } else{
            logger.debug("Querying all campaigns...");
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
     * Applies sorting parameters to create a pageable object for query pagination.
     *
     * @param pageNo     the page number
     * @param pageSize   the page size
     * @param sortBy     the field to sort by
     * @param sortOrder  the sort order (ASC or DESC)
     * @return a pageable object for query pagination
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
     * Maps a Campaign entity to a ListCampaignsReq DTO.
     *
     * @param campaign the Campaign entity to be mapped
     * @return a ListCampaignsReq DTO mapped from the Campaign entity
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
     * Retrieves the discount type from the provided request object.
     *
     * @param addAndUpdateCampaignReq the request object containing campaign details
     * @return the discount type parsed from the request
     * @throws InvalidDiscountTypeException if an invalid discount type is provided
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
     * Checks the availability of products associated with a campaign request.
     *
     * @param addAndUpdateCampaignReq the request containing campaign details
     * @return a Mono<Void> indicating completion of the product availability check
     * @throws ProductNotFoundException if a product is not found while checking availability
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
                                return Mono.empty();
                            });
                })
                // Combine all results into a single Mono<Void> indicating completion
                .then();
    }

    /**
     * Maps a request object to a Campaign entity.
     *
     * @param addAndUpdateCampaignReq the request object containing campaign details
     * @return a Campaign entity mapped from the request object
     * @throws FixedAmountDiscountTypeWithProvidedQuantityException if a fixed amount discount type is provided with a quantity greater than 1
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
     * Sends campaign information to the reporting service.
     *
     * @param campaign the Campaign entity containing campaign information
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