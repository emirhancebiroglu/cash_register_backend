package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.kafka.ReturnedProductInfoDTO;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.invalidquantity.InvalidQuantityException;
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.repository.ShoppingBagRepository;
import bit.salesservice.service.ShoppingBagService;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingBagServiceImpl implements ShoppingBagService {
    private final ShoppingBagRepository shoppingBagRepository;
    private final CheckoutRepository checkoutRepository;
    private final CampaignRepository campaignRepository;
    private final SaleReportProducer saleReportProducer;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private static final Logger logger = LoggerFactory.getLogger(ShoppingBagServiceImpl.class);
    private final ProductInfoHttpRequest info;
    private static final String NOT_FOUND = "Product not found";

    @Override
    public void addProductToBag(AddAndListProductReq req) {
        logger.info("Adding product to shopping bag...");

        ProductInfo productInfo = info.getProductInfo(req.getCode(), jwtToken);

        checkIfProductAvailable(req, productInfo);

        if (req.getQuantity() <= 0){
            throw new InvalidQuantityException("Please provide a valid quantity.");
        }

        if (productInfo.getStockAmount() < req.getQuantity()){
            throw new NotInStocksException("There is not enough product in stocks.");
        }

        Checkout currentCheckout = getCurrentCheckout();
        Product existingProduct = shoppingBagRepository.findByCodeAndCheckout(req.getCode(), currentCheckout);

        if (existingProduct != null && existingProduct.isRemoved()) {
            existingProduct.setRemoved(false);
            existingProduct.setQuantity(req.getQuantity());
        } else if (existingProduct != null) {
            int newQuantity = existingProduct.getQuantity() + req.getQuantity();
            if (productInfo.getStockAmount() < newQuantity) {
                throw new NotInStocksException("There is not enough product in stocks.");
            }
            existingProduct.setQuantity(newQuantity);
        } else {
            existingProduct = new Product();
            existingProduct.setCode(req.getCode());
            existingProduct.setName(productInfo.getName());
            if (currentCheckout.getProducts() == null){
                currentCheckout.setTotalPrice(0D);
            }
            existingProduct.setQuantity(req.getQuantity());
            existingProduct.setPrice(productInfo.getPrice());
            existingProduct.setCheckout(currentCheckout);
        }

        updateCheckoutTotalForAddingProduct(existingProduct, productInfo.getPrice(), req.getQuantity());
        shoppingBagRepository.save(existingProduct);

        logger.info("Product added successfully");
    }

    @Override
    public void removeProductFromBag(Long id, Integer quantity) {
        logger.info("Removing product from shopping bag...");

        Checkout currentCheckout = getCurrentCheckout();

        Product product = shoppingBagRepository.findByIdAndCheckoutId(id, currentCheckout.getId())
                .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND));

        int productQuantity = product.getQuantity();

        if (productQuantity < quantity){
            throw new InvalidQuantityException("Quantity is out of range");
        }

        if (productQuantity > quantity && quantity != 0){
            product.setQuantity(productQuantity - quantity);
        }
        else{
            product.setQuantity(0);
            product.setRemoved(true);
        }

        updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), quantity);
        shoppingBagRepository.save(product);

        logger.info("Product removed successfully");
    }

    @Override
    @Transactional
    public void removeAll() {
        logger.info("Removing all products from shopping bag...");

        Checkout currentCheckout = getCurrentCheckout();

        List<Product> products = shoppingBagRepository.findByCheckoutId(currentCheckout.getId());

        for (Product product : products){
            if (!product.isRemoved()){
                product.setRemoved(true);
                product.setQuantity(0);
            }
        }

        shoppingBagRepository.saveAll(products);

        currentCheckout.setTotalPrice(0D);
        checkoutRepository.save(currentCheckout);

        logger.info("All products removed successfully");
    }

    @Override
    public List<AddAndListProductReq> getProductsInBagForCurrentCheckout() {
        logger.info("Getting products in shopping bag for current checkout...");

        Checkout currentCheckout = getCurrentCheckout();

        List<AddAndListProductReq> products;

        if (!currentCheckout.isCompleted()){
            products = shoppingBagRepository.findProductReqByCheckoutAndRemoved(currentCheckout);
            logger.info("Products fetched successfully.");
            return products;
        }

        logger.info("Checkout is completed. No products to retrieve.");
        return Collections.emptyList();
    }

    @Override
    public void returnProductFromBag(Long id, Integer quantityToReturn, Long checkoutId) {
        logger.info("Returning product from shopping bag...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new ProductNotFoundException("Checkout not found"));

        if (checkout.isCompleted()) {
            Product product = shoppingBagRepository.findByIdAndCheckoutId(id, checkout.getId())
                    .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND));

            if (product.isRemoved() || product.isReturned()){
                throw new ProductNotFoundException("This product is removed or returned");
            }

            int quantity = product.getQuantity();

            if (quantity < quantityToReturn) {
                throw new InvalidQuantityException("Quantity is out of range");
            }

            if (quantity > quantityToReturn) {
                product.setQuantity(quantity - quantityToReturn);
            } else {
                product.setQuantity(0);
                product.setReturned(true);
            }

            product.setReturnedQuantity(product.getReturnedQuantity() + quantityToReturn);

            updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), quantity);
            shoppingBagRepository.save(product);
        }
        else{
            throw new UncompletedCheckoutException("Checkout is not completed.");
        }

        logger.info("Product returned successfully");
    }

    private Checkout getCurrentCheckout() {
        Checkout currentCheckout = checkoutRepository.findFirstByOrderByIdDesc();
        if (currentCheckout == null) {
            currentCheckout = new Checkout();
            currentCheckout.setCreatedDate(LocalDateTime.now());
            currentCheckout.setUpdatedDate(LocalDateTime.now());

            checkoutRepository.save(currentCheckout);
        }
        return currentCheckout;
    }

    private void updateCheckoutTotalForAddingProduct(Product product, double price, Integer quantity){
        Checkout checkout = product.getCheckout();
        checkout.setTotalPrice(applyCampaignsAndUpdatePrice(product, price * quantity, quantity));
        checkout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }

    private void updateCheckoutTotalForRemovingOrReturningProduct(Product product, double price, Integer quantity){
        double newTotalPrice = unApplyCampaignsAndUpdatePrice(product, price * quantity, quantity);

        Checkout checkout = product.getCheckout();
        if (product.getReturnedQuantity() > 0){
            checkout.setReturnedMoney(checkout.getReturnedMoney() + (checkout.getTotalPrice() - newTotalPrice));
            sendReturnedProductsInfoToReportingService(product, checkout.getReturnedMoney());
        }
        checkout.setTotalPrice(newTotalPrice);
        checkout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }

    private static int getStockAmount(AddAndListProductReq req, ProductInfo productInfo) {
        boolean exists = productInfo.isExists();
        int stockAmount = productInfo.getStockAmount();

        if (!exists){
            throw new ProductNotFoundException("Product not exists or in stock");
        }

        if (stockAmount < req.getQuantity()) {
            throw new NotInStocksException("There is not enough product in stocks. Please provide a valid quantity");
        }
        return stockAmount;
    }

    private Double applyCampaignsAndUpdatePrice(Product existingProduct, double total, Integer quantity) {
        Campaign campaign = campaignRepository.findFirstByCodesContaining(existingProduct.getCode());

        if (campaign == null || campaign.isInactive()) {
            return existingProduct.getCheckout().getTotalPrice() + total;
        }

        double discountAmount = calculateDiscountAmountWhileApplyingCampaign(campaign, existingProduct, quantity);

        return existingProduct.getCheckout().getTotalPrice() + total - discountAmount;
    }

    private Double unApplyCampaignsAndUpdatePrice(Product product, double total, Integer quantity) {
        Campaign campaign = campaignRepository.findFirstByCodesContaining(product.getCode());

        if (campaign == null || campaign.isInactive()) {
            return product.getCheckout().getTotalPrice() - total;
        }

        double discountAmount = calculateDiscountAmountWhileUnapplyingCampaign(campaign, product, quantity);

        return product.getCheckout().getTotalPrice() - discountAmount;
    }

    private double calculateDiscountAmountWhileApplyingCampaign(Campaign campaign, Product existingProduct, Integer quantity) {
        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() ==  1){
                if (existingProduct.getAppliedCampaign() == null) {
                    existingProduct.setAppliedCampaign(campaign.getName());
                }
                return (existingProduct.getPrice() * campaign.getDiscountAmount()) / 100;
            }

            int numberOfProductsWithUnappliedCampaign = (existingProduct.getQuantity() - quantity) % campaign.getNeededQuantity();

            int counter = 0;

            for (int i = 1; i <= quantity + numberOfProductsWithUnappliedCampaign; i++) {
                if (i % campaign.getNeededQuantity() == 0){
                    counter++;
                }
            }

            if (counter > 0 && existingProduct.getAppliedCampaign() == null) {
                existingProduct.setAppliedCampaign(campaign.getName());
            }

            return campaign.getNeededQuantity() * counter * ((existingProduct.getPrice() * campaign.getDiscountAmount()) / 100);
        } else if (campaign.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            if (existingProduct.getAppliedCampaign() == null) {
                existingProduct.setAppliedCampaign(campaign.getName());
            }
            return campaign.getDiscountAmount() * quantity;
        }
        return 0;
    }

    private double calculateDiscountAmountWhileUnapplyingCampaign(Campaign campaign, Product existingProduct, Integer quantity) {
        double price = existingProduct.getPrice();

        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() ==  1){
                return (price - ((price * campaign.getDiscountAmount()) / 100)) * quantity;
            }

            int numberOfTimesCampaignApplied = (existingProduct.getQuantity() + quantity) / campaign.getNeededQuantity();
            int numberOfProductsWithUnappliedCampaign = (existingProduct.getQuantity() + quantity) % campaign.getNeededQuantity();

            double priceToDecrease = 0;
            int counter = 1;
            int quantityToDecrease = quantity - numberOfProductsWithUnappliedCampaign;
            int quantityToDecreaseTemp = quantityToDecrease;

            if (numberOfTimesCampaignApplied > 0){
                for (int i = 0; i < numberOfTimesCampaignApplied; i++){
                    if (quantityToDecreaseTemp == 0){
                        break;
                    }

                    for (int j = 1; j <= quantityToDecrease; j++) {
                        if (j  == 1){
                            priceToDecrease += 0;

                        }
                        else{
                            priceToDecrease += price;
                            counter++;

                        }
                        quantityToDecreaseTemp--;
                        if (quantityToDecreaseTemp == 0){
                            break;
                        }

                        if (counter == campaign.getNeededQuantity()){
                            quantityToDecrease -= counter;
                            counter = 1;
                            break;
                        }
                    }
                }

                return numberOfProductsWithUnappliedCampaign * existingProduct.getPrice() + priceToDecrease;
            }
            else{
                return quantity * price;
            }

        } else if (campaign.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return (price - campaign.getDiscountAmount()) * quantity;
        }

        return price * quantity;
    }

    private void checkIfProductAvailable(AddAndListProductReq req, ProductInfo productInfo){
        int stockAmount = getStockAmount(req, productInfo);
        boolean exists = productInfo.isExists();

        if (!exists || stockAmount == 0){
            throw new ProductNotFoundException("Product not exists or in stock");
        }
    }

    private void sendReturnedProductsInfoToReportingService(Product product, Double returnedMoney) {
        ReturnedProductInfoDTO returnedProductInfoDTO = new ReturnedProductInfoDTO(
                product,
                returnedMoney
        );

        saleReportProducer.sendReturnedProductInfoToReportingSerivce("returned-product-info", returnedProductInfoDTO);
    }
}