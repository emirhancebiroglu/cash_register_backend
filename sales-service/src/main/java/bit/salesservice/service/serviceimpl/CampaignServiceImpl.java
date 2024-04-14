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
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.service.CampaignService;
import bit.salesservice.utils.CampaignProducer;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.validators.CampaignValidator;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final ProductInfoHttpRequest info;
    private final CampaignValidator campaignValidator;
    private final CampaignProducer campaignProducer;
    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);

    private static final String NOT_FOUND = "Campaign not found";

    @Override
    public void addCampaign(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        logger.info("Adding campaign...");

        if (campaignRepository.findByName(addAndUpdateCampaignReq.getName()) != null){
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
                .orElseThrow(() -> new CampaignNotFoundException(NOT_FOUND));

        if (existingCampaign.isInactive()){
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
                .orElseThrow(() -> new CampaignNotFoundException(NOT_FOUND));

        if (campaign.isInactive()){
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
                .orElseThrow(() -> new CampaignNotFoundException(NOT_FOUND));

        if (!campaign.isInactive()){
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
    public List<ListCampaignsReq> getAllCampaigns() {
        logger.info("Getting all campaigns...");

        List<Campaign> campaignList = campaignRepository.findAll();
        List<ListCampaignsReq> listCampaignsReqList = new ArrayList<>();

        for (Campaign campaign : campaignList) {
            ListCampaignsReq listCampaignsReq = mapToCampaignReq(campaign);
            listCampaignsReqList.add(listCampaignsReq);
        }

        logger.info("All campaigns retrieved successfully");

        return listCampaignsReqList;
    }

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

    private static DiscountType getDiscountType(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        DiscountType discountType;

        try {
            discountType = DiscountType.valueOf(addAndUpdateCampaignReq.getDiscountType());
        }
        catch (IllegalArgumentException e){
            throw new InvalidDiscountTypeException("Invalid discount type");
        }
        return discountType;
    }

    private Mono<Void> checkIfProductsAvailable(AddAndUpdateCampaignReq addAndUpdateCampaignReq) {
        return Flux.fromIterable(addAndUpdateCampaignReq.getCodes())
                .flatMap(code -> {
                    final String productCode = code;
                    return Mono.just(info.getProductInfo(productCode, jwtToken))
                            .flatMap(productInfo -> {
                                if (!productInfo.isExists()) {
                                    return Mono.error(new ProductNotFoundException("Product not found: " + productCode));
                                }
                                return Mono.empty();
                            });
                })
                .then();
    }

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
                throw new FixedAmountDiscountTypeWithProvidedQuantityException("Fixed amount discount type should not require more than 1 quantity for the campaign");
            }
            campaign.setNeededQuantity(addAndUpdateCampaignReq.getNeededQuantity());
        }
        else{
            campaign.setNeededQuantity(1);
        }
        return campaign;
    }

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