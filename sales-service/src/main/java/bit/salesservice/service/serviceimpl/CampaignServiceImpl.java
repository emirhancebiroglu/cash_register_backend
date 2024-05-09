package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.AddAndUpdateCampaignReq;
import bit.salesservice.dto.ListCampaignsReq;
import bit.salesservice.dto.kafka.CampaignDTO;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.exceptions.activecampaign.ActiveCampaignException;
import bit.salesservice.exceptions.campaignalreadyexists.CampaignAlreadyExistsException;
import bit.salesservice.exceptions.campaignnotfound.CampaignNotFoundException;
import bit.salesservice.exceptions.fixedamountdiscounttypewithprovidedquantity.FixedAmountDiscountTypeWithProvidedQuantityException;
import bit.salesservice.exceptions.inactivecampaign.InactiveCampaignException;
import bit.salesservice.exceptions.invaliddiscounttype.InvalidDiscountTypeException;
import bit.salesservice.exceptions.invalidstatustype.InvalidStatusTypeException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.service.CampaignService;
import bit.salesservice.utils.CampaignProducer;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.validators.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.logging.log4j.Logger;
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
    private static final String END_DATE = "endDate";

    @Override
    public void addCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        logger.info("Adding campaign...");

        if (campaignRepository.findByName(addAndUpdateCampaignReq.getName()) != null){
            logger.error("Campaign with name {} already exists.", addAndUpdateCampaignReq.getName());
            throw new CampaignAlreadyExistsException("Campaign wit the name " + addAndUpdateCampaignReq.getName() + " already exists");
        }

        checkIfProductsAvailable(addAndUpdateCampaignReq)
                .doOnError(error -> {
                    logger.error("Error occurred while checking products availability: {}", error.getMessage());
                    throw new ProductNotFoundException(error.getMessage());
                })
                .block();

        campaignValidator.validateCampaignDTO(addAndUpdateCampaignReq, campaignRepository);

        Campaign campaign = mapToCampaign(addAndUpdateCampaignReq);

        campaignRepository.save(campaign);

        sendCampaignInfoToReportingService(campaign);

        logger.info("Campaign added successfully");
    }

    @Override
    public void updateCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq, Long campaignId) {
        logger.info("Updating campaign...");

        Campaign existingCampaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new CampaignNotFoundException(NOT_FOUND);
                });

        if (existingCampaign.isInactive()){
            logger.error("Campaign is inactivated or has reached its end date, please reactivate the campaign to update it.");
            throw new InactiveCampaignException("Campaign is inactivated or has reached its end date, please reactivate the campaign to update it.");
        }

        if (!addAndUpdateCampaignReq.getCodes().isEmpty()){
            checkIfProductsAvailable(addAndUpdateCampaignReq)
                    .doOnError(error -> {
                        logger.error("Error occurred while checking products availability: {}", error.getMessage());
                        throw new ProductNotFoundException(error.getMessage());
                    })
                    .block();

            campaignValidator.validateProductCodes(addAndUpdateCampaignReq.getCodes(), campaignRepository);
            existingCampaign.setCodes(addAndUpdateCampaignReq.getCodes());
        }

        if (!Objects.equals(existingCampaign.getName(), addAndUpdateCampaignReq.getName()) && !addAndUpdateCampaignReq.getName().isEmpty()){
            existingCampaign.setName(addAndUpdateCampaignReq.getName());
        }

        if (addAndUpdateCampaignReq.getDurationDays() != null && (!Objects.equals(existingCampaign.getEndDate(), existingCampaign.getStartDate().plusDays(addAndUpdateCampaignReq.getDurationDays())))){
                existingCampaign.setDurationDays(addAndUpdateCampaignReq.getDurationDays());
                existingCampaign.setEndDate(existingCampaign.getStartDate().plusDays(addAndUpdateCampaignReq.getDurationDays()));
        }

        if (!addAndUpdateCampaignReq.getDiscountType().isEmpty()){
            DiscountType discountType = getDiscountType(addAndUpdateCampaignReq);

            if (!Objects.equals(existingCampaign.getDiscountType(), discountType)){
                existingCampaign.setDiscountType(discountType);
            }
        }

        if (addAndUpdateCampaignReq.getDiscountAmount() != null && !Objects.equals(existingCampaign.getDiscountAmount(), addAndUpdateCampaignReq.getDiscountAmount())){
            campaignValidator.validateDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());
            existingCampaign.setDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());
        }

        if (addAndUpdateCampaignReq.getNeededQuantity() != null && !Objects.equals(existingCampaign.getNeededQuantity(), addAndUpdateCampaignReq.getNeededQuantity())){
            existingCampaign.setNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());
        }

        existingCampaign.setUpdatedDate(LocalDateTime.now());
        campaignRepository.save(existingCampaign);

        sendCampaignInfoToReportingService(existingCampaign);

        logger.info("Campaign updated successfully");
    }

    @Override
    public void inactivateCampaign(Long campaignId) {
        logger.info("Inactivating campaign...");

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new CampaignNotFoundException(NOT_FOUND);
                });

        if (campaign.isInactive()){
            logger.error("Campaign is already inactivated");
            throw new InactiveCampaignException("Campaign is already inactivated");
        }

        campaign.setInactive(true);

        campaignRepository.save(campaign);

        logger.info("Campaign inactivated successfully");
    }

    @Override
    public void reactivateCampaign(Long campaignId, Integer durationDays) {
        logger.info("Reactivating campaign...");

        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new CampaignNotFoundException(NOT_FOUND);
                });

        if (!campaign.isInactive()){
            logger.error("Campaign is already active");
            throw new ActiveCampaignException("Campaign is already active");
        }

        campaign.setInactive(false);
        campaign.setDurationDays(durationDays);
        campaign.setEndDate(LocalDateTime.now().plusDays(durationDays));
        campaign.setStartDate(LocalDateTime.now());
        campaign.setUpdatedDate(LocalDateTime.now());

        campaignRepository.save(campaign);

        logger.info("Campaign reactivated successfully");
    }

    @Override
    public List<ListCampaignsReq> getCampaigns(int pageNo, int pageSize, String discountType, String status, String searchingTerm, String sortBy, String sortOrder) {
        logger.info("Getting campaigns...");

        Pageable pageable = applySort(pageNo, pageSize, sortBy, sortOrder);

        Page<Campaign> campaignPage;

        DiscountType parsedDiscountType = validateDiscountTypeAndStatus(discountType, status);

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
            if (status.equals(STATUS_ACTIVE)){
                campaignPage = campaignRepository.findAllByisInactive(false, pageable);
            }
            else{
                campaignPage = campaignRepository.findAllByisInactive(true, pageable);
            }
        } else if (searchingTerm != null) {
            campaignPage = campaignRepository.findByNameContaining(pageable, searchingTerm);
        } else{
            campaignPage = campaignRepository.findAll(pageable);
        }

        List<ListCampaignsReq> listCampaignsReqList = campaignPage.getContent().stream()
                .map(this::mapToCampaignReq)
                .toList();

        logger.info("Campaigns retrieved successfully");

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
        Pageable pageable;

        if (sortBy.equalsIgnoreCase("name")) {
            pageable = PageRequest.of(pageNo, pageSize, sortOrder.equalsIgnoreCase("ASC") ? Sort.by(Sort.Direction.ASC, "name") : Sort.by(Sort.Direction.DESC, "name"));
        } else if (sortBy.equalsIgnoreCase(END_DATE)) {
            pageable = PageRequest.of(pageNo, pageSize, sortOrder.equalsIgnoreCase("ASC") ? Sort.by(Sort.Direction.ASC, END_DATE) : Sort.by(Sort.Direction.DESC, END_DATE));
        } else {
            pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.ASC, "name"));
        }

        return pageable;
    }

    /**
     * Validates the discount type and status parameters.
     *
     * @param discountType the discount type
     * @param status       the status
     * @return the parsed discount type if valid, null otherwise
     * @throws InvalidDiscountTypeException if an invalid discount type is provided
     * @throws InvalidStatusTypeException   if an invalid status type is provided
     */
    @Nullable
    private static DiscountType validateDiscountTypeAndStatus(String discountType, String status) {
        DiscountType parsedDiscountType = null;

        if (discountType != null){
            try {
                parsedDiscountType = DiscountType.valueOf(discountType.toUpperCase());
            } catch (IllegalArgumentException ex) {
                logger.error("Invalid discount type : {}", discountType);
                throw new InvalidDiscountTypeException("Invalid discount type: " + discountType);
            }
        }
        if (status != null && (!status.equalsIgnoreCase(STATUS_ACTIVE) && !status.equalsIgnoreCase("inactive"))) {
            logger.error("Invalid status type : {}", status);
            throw new InvalidStatusTypeException("Invalid status type");
        }
        return parsedDiscountType;
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
        return Flux.fromIterable(addAndUpdateCampaignReq.getCodes())
                .flatMap(code -> {
                    final String productCode = code;
                    return Mono.just(info.getProductInfo(productCode, jwtToken))
                            .flatMap(productInfo -> {
                                if (!productInfo.isExists()) {
                                    logger.error("Product not found : {}", productCode);
                                    return Mono.error(new ProductNotFoundException("Product not found: " + productCode));
                                }
                                return Mono.empty();
                            });
                })
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
        DiscountType discountType = getDiscountType(addAndUpdateCampaignReq);
        Integer durationDays = addAndUpdateCampaignReq.getDurationDays();

        Campaign campaign = new Campaign();

        campaign.setCodes(addAndUpdateCampaignReq.getCodes());
        campaign.setName(addAndUpdateCampaignReq.getName());
        campaign.setStartDate(LocalDateTime.now());
        campaign.setUpdatedDate(LocalDateTime.now());
        campaign.setDurationDays(durationDays);
        campaign.setEndDate(LocalDateTime.now().plusDays(durationDays));
        campaign.setDiscountType(discountType);
        campaign.setDiscountAmount(addAndUpdateCampaignReq.getDiscountAmount());

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
        CampaignDTO campaignDTO = new CampaignDTO(
                campaign.getName(),
                campaign.getDiscountType(),
                campaign.getDiscountAmount(),
                campaign.getNeededQuantity()
        );

        campaignProducer.sendCampaign("campaign", campaignDTO);
    }
}