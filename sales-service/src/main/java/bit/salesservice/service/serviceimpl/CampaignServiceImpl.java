package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.CampaignDTO;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
    private final CampaignRepository campaignRepository;
    private final ProductInfoHttpRequest info;
    private final CampaignValidator campaignValidator;
    private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);

    private static final String NOT_FOUND = "Campaign not found";

    @Override
    public void addCampaign(CampaignDTO campaignDTO) {
        logger.info("Adding campaign...");

        if (campaignRepository.findByName(campaignDTO.getName()) != null){
            throw new CampaignAlreadyExistsException("Campaign wit the name " + campaignDTO.getName() + " already exists");
        }

        checkIfProductsAvailable(campaignDTO)
                .doOnError(error -> {
                    logger.error("Error occurred while checking products availability: {}", error.getMessage());
                    throw new ProductNotFoundException(error.getMessage());
                })
                .block();

        campaignValidator.validateCampaignDTO(campaignDTO, campaignRepository);

        Campaign campaign = mapToCampaign(campaignDTO);

        campaignRepository.save(campaign);

        logger.info("Campaign added successfully");
    }

    @Override
    public void updateCampaign(CampaignDTO campaignDTO, Long campaignId) {
        logger.info("Updating campaign...");

        Campaign existingCampaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new CampaignNotFoundException(NOT_FOUND));

        if (existingCampaign.isInactive()){
            throw new InactiveCampaignException("Campaign is inactivated or has reached its end date, please reactivate the campaign to update it.");
        }

        if (!campaignDTO.getCodes().isEmpty()){
            checkIfProductsAvailable(campaignDTO)
                    .doOnError(error -> {
                        logger.error("Error occurred while checking products availability: {}", error.getMessage());
                        throw new ProductNotFoundException(error.getMessage());
                    })
                    .block();

            campaignValidator.validateProductCodes(campaignDTO.getCodes(), campaignRepository);
            existingCampaign.setCodes(campaignDTO.getCodes());
        }

        if (!Objects.equals(existingCampaign.getName(), campaignDTO.getName()) && !campaignDTO.getName().isEmpty()){
            existingCampaign.setName(campaignDTO.getName());
        }

        if (campaignDTO.getDurationDays() != null && (!Objects.equals(existingCampaign.getEndDate(), existingCampaign.getStartDate().plusDays(campaignDTO.getDurationDays())))){
                existingCampaign.setDurationDays(campaignDTO.getDurationDays());
                existingCampaign.setEndDate(existingCampaign.getStartDate().plusDays(campaignDTO.getDurationDays()));
        }

        if (!campaignDTO.getDiscountType().isEmpty()){
            DiscountType discountType = getDiscountType(campaignDTO);

            if (!Objects.equals(existingCampaign.getDiscountType(), discountType)){
                existingCampaign.setDiscountType(discountType);
            }
        }

        if (campaignDTO.getDiscountAmount() != null && !Objects.equals(existingCampaign.getDiscountAmount(), campaignDTO.getDiscountAmount())){
            campaignValidator.validateDiscountAmount(campaignDTO.getDiscountAmount());
            existingCampaign.setDiscountAmount(campaignDTO.getDiscountAmount());
        }

        if (campaignDTO.getNeededQuantity() != null && !Objects.equals(existingCampaign.getNeededQuantity(), campaignDTO.getNeededQuantity())){
            existingCampaign.setNeededQuantity(campaignDTO.getNeededQuantity());
        }

        existingCampaign.setUpdatedDate(LocalDateTime.now());
        campaignRepository.save(existingCampaign);

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

    private static DiscountType getDiscountType(CampaignDTO campaignDTO) {
        DiscountType discountType;

        try {
            discountType = DiscountType.valueOf(campaignDTO.getDiscountType());
        }
        catch (IllegalArgumentException e){
            throw new InvalidDiscountTypeException("Invalid discount type");
        }
        return discountType;
    }

    private Mono<Void> checkIfProductsAvailable(CampaignDTO campaignDTO) {
        return Flux.fromIterable(campaignDTO.getCodes())
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

    private Campaign mapToCampaign(CampaignDTO campaignDTO) {
        DiscountType discountType = getDiscountType(campaignDTO);
        Integer durationDays = campaignDTO.getDurationDays();

        Campaign campaign = new Campaign();

        campaign.setCodes(campaignDTO.getCodes());
        campaign.setName(campaignDTO.getName());
        campaign.setStartDate(LocalDateTime.now());
        campaign.setUpdatedDate(LocalDateTime.now());
        campaign.setDurationDays(durationDays);
        campaign.setEndDate(LocalDateTime.now().plusDays(durationDays));
        campaign.setDiscountType(discountType);
        campaign.setDiscountAmount(campaignDTO.getDiscountAmount());

        if (campaignDTO.getNeededQuantity() != null){
            if (discountType == DiscountType.FIXED_AMOUNT && campaignDTO.getNeededQuantity() > 1){
                throw new FixedAmountDiscountTypeWithProvidedQuantityException("Fixed amount discount type should not require more than 1 quantity for the campaign");
            }
            campaign.setNeededQuantity(campaignDTO.getNeededQuantity());
        }
        else{
            campaign.setNeededQuantity(1);
        }
        return campaign;
    }
}
