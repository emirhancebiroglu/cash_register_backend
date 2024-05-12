package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.AddAndListProductReq;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.RemoveOrReturnProductFromBagReq;
import bit.salesservice.dto.kafka.ReturnedProductInfoDTO;
import bit.salesservice.entity.Campaign;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.DiscountType;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing shopping bag operations.
 */
@Service
@RequiredArgsConstructor
public class ShoppingBagServiceImpl implements ShoppingBagService {
    private final ShoppingBagRepository shoppingBagRepository;
    private final CheckoutRepository checkoutRepository;
    private final CampaignRepository campaignRepository;
    private final SaleReportProducer saleReportProducer;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private static final Logger logger = LogManager.getLogger(ShoppingBagServiceImpl.class);
    private final ProductInfoHttpRequest httpRequest;
    private static final String NOT_FOUND = "Product not found";
    private static final String INVALID_QUANTITY = "Please provide a valid quantity";
    private static final String NOT_IN_STOCKS = "There is not enough product in stocks.";
    private static final String OUT_OF_RANGE = "Quantity is out of range";


    @Override
    public void addProductToBag(AddAndListProductReq req, Long checkoutId) {
        logger.info("Adding product to shopping bag...");

        ProductInfo productInfo = httpRequest.getProductInfo(req.getCode(), jwtToken);

        checkIfProductAvailable(req, productInfo);

        if (req.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (productInfo.getStockAmount() < req.getQuantity()){
            logger.error(NOT_IN_STOCKS);
            throw new NotInStocksException(NOT_IN_STOCKS);
        }

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException("Checkout not found"));

        Product existingProduct = shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout);

        if (existingProduct != null && existingProduct.isRemoved()) {
            existingProduct.setRemoved(false);
            existingProduct.setQuantity(req.getQuantity());
        } else if (existingProduct != null) {
            int newQuantity = existingProduct.getQuantity() + req.getQuantity();
            if (productInfo.getStockAmount() < newQuantity) {
                logger.error(NOT_IN_STOCKS);
                throw new NotInStocksException(NOT_IN_STOCKS);
            }
            existingProduct.setQuantity(newQuantity);
        } else {
            existingProduct = new Product();
            existingProduct.setCode(req.getCode());
            existingProduct.setName(productInfo.getName());
            if (checkout.getProducts() == null){
                checkout.setTotalPrice(0D);
            }
            existingProduct.setQuantity(req.getQuantity());
            existingProduct.setPrice(productInfo.getPrice());
            existingProduct.setCheckout(checkout);
        }

        updateCheckoutTotalForAddingProduct(existingProduct, productInfo.getPrice(), req.getQuantity());
        shoppingBagRepository.save(existingProduct);

        logger.info("Product added successfully");
    }

    @Override
    public void removeProductFromBag(RemoveOrReturnProductFromBagReq request) {
        logger.info("Removing product from shopping bag...");

        Product product = shoppingBagRepository.findByCode(request.getCode())
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new ProductNotFoundException(NOT_FOUND);
                });

        if (request.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        int productQuantity = product.getQuantity();

        if (productQuantity < request.getQuantity()){
            logger.error(OUT_OF_RANGE);
            throw new InvalidQuantityException(OUT_OF_RANGE);
        }

        if (productQuantity > request.getQuantity()){
            product.setQuantity(productQuantity - request.getQuantity());
        }
        else{
            product.setQuantity(0);
            product.setRemoved(true);
        }

        updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), request.getQuantity());
        shoppingBagRepository.save(product);

        logger.info("Product removed successfully");
    }

    @Override
    @Transactional
    public void removeAll(Long checkoutId) {
        logger.info("Removing all products from shopping bag...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException("Checkout not found"));

        List<Product> products = shoppingBagRepository.findByCheckoutId(checkoutId);

        for (Product product : products){
            if (!product.isRemoved()){
                product.setRemoved(true);
                product.setQuantity(0);
            }
        }

        shoppingBagRepository.saveAll(products);

        checkout.setTotalPrice(0D);
        checkoutRepository.save(checkout);

        logger.info("All products removed successfully");
    }

    @Override
    public List<AddAndListProductReq> getProductsInBag(Long checkoutId) {
        logger.info("Getting products in shopping bag for current checkout...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException("Checkout not found"));

        List<AddAndListProductReq> products;

        products = shoppingBagRepository.findProductReqByCheckoutAndRemoved(checkout);
        logger.info("Products fetched successfully.");
        return products;
    }

    @Override
    public void returnProductFromBag(RemoveOrReturnProductFromBagReq request) {
        logger.info("Returning product from shopping bag...");

        Product product = shoppingBagRepository.findByCode(request.getCode())
                .orElseThrow(() -> {
                    logger.error(NOT_FOUND);
                    return new ProductNotFoundException(NOT_FOUND);
                });

        Checkout checkout = checkoutRepository.findById(request.getCheckoutId())
                .orElseThrow(() -> new CheckoutNotFoundException("Checkout not found"));

        if (request.getQuantity() <= 0){
            logger.error(INVALID_QUANTITY);
            throw new InvalidQuantityException(INVALID_QUANTITY);
        }

        if (checkout.isCompleted()) {
            if (product.isRemoved() || product.isReturned()){
                logger.error("This product is removed or returned");
                throw new ProductNotFoundException("This product is removed or returned");
            }

            int productQuantity = product.getQuantity();

            if (productQuantity < request.getQuantity()) {
                logger.error(OUT_OF_RANGE);
                throw new InvalidQuantityException(OUT_OF_RANGE);
            }

            if (productQuantity > request.getQuantity()) {
                product.setQuantity(productQuantity - request.getQuantity());
            } else {
                product.setQuantity(0);
                product.setReturned(true);
            }

            product.setReturnedQuantity(product.getReturnedQuantity() + request.getQuantity());
            updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), request.getQuantity());

            Map<String, Integer> productsIdWithQuantity = getProductsCodeWithQuantity(request.getCode(), request.getQuantity());
            httpRequest.updateStocks(jwtToken, productsIdWithQuantity, false);

            shoppingBagRepository.save(product);
            sendReturnedProductsInfoToReportingService(product);
        }
        else{
            logger.error("Checkout is not completed.");
            throw new UncompletedCheckoutException("Checkout is not completed.");
        }

        logger.info("Product returned successfully");
    }

    /**
     * Updates the total price of the checkout when adding a product.
     *
     * @param product  the product being added
     * @param price    the price of the product
     * @param quantity the quantity of the product being added
     */
    private void updateCheckoutTotalForAddingProduct(Product product, double price, Integer quantity){
        Checkout checkout = product.getCheckout();
        checkout.setTotalPrice(applyCampaignsAndUpdatePrice(product, price * quantity, quantity));
        checkout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }

    /**
     * Updates the total price of the checkout when removing or returning a product.
     *
     * @param product  the product being removed or returned
     * @param price    the price of the product
     * @param quantity the quantity of the product being removed or returned
     */
    private void updateCheckoutTotalForRemovingOrReturningProduct(Product product, double price, Integer quantity){
        double newTotalPrice = unApplyCampaignsAndUpdatePrice(product, price * quantity, quantity);

        Checkout checkout = product.getCheckout();
        if (product.getReturnedQuantity() > 0){
            checkout.setReturnedMoney(checkout.getReturnedMoney() + (checkout.getTotalPrice() - newTotalPrice));
        }
        checkout.setTotalPrice(newTotalPrice);
        checkout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }

    /**
     * Retrieves the stock amount for a given product.
     *
     * @param req          the request containing product details
     * @param productInfo  information about the product
     * @return the stock amount of the product
     */
    private static int getStockAmount(AddAndListProductReq req, ProductInfo productInfo) {
        boolean exists = productInfo.isExists();
        int stockAmount = productInfo.getStockAmount();

        if (!exists){
            logger.error(NOT_FOUND);
            throw new ProductNotFoundException(NOT_FOUND);
        }

        if (stockAmount < req.getQuantity()) {
            logger.error(NOT_IN_STOCKS);
            throw new NotInStocksException("There is not enough product in stocks. Please provide a valid quantity");
        }
        return stockAmount;
    }

    /**
     * Applies campaigns and updates the total price when adding a product to the checkout.
     *
     * @param existingProduct the existing product in the checkout
     * @param total           the total price before applying discounts
     * @param quantity        the quantity of the product being added
     * @return the updated total price after applying discounts
     */
    private Double applyCampaignsAndUpdatePrice(Product existingProduct, double total, Integer quantity) {
        Campaign campaign = campaignRepository.findFirstByCodesContaining(existingProduct.getCode());

        if (campaign == null || campaign.isInactive()) {
            return existingProduct.getCheckout().getTotalPrice() + total;
        }

        double discountAmount = calculateDiscountAmountWhileApplyingCampaign(campaign, existingProduct, quantity);

        return existingProduct.getCheckout().getTotalPrice() + total - discountAmount;
    }

    /**
     * Unapplies campaigns and updates the total price when removing or returning a product from the checkout.
     *
     * @param product  the product being removed or returned
     * @param total    the total price before unapplying discounts
     * @param quantity the quantity of the product being removed or returned
     * @return the updated total price after unapplying discounts
     */
    private Double unApplyCampaignsAndUpdatePrice(Product product, double total, Integer quantity) {
        Campaign campaign = campaignRepository.findFirstByCodesContaining(product.getCode());

        if (campaign == null) {
            return product.getCheckout().getTotalPrice() - total;
        }

        double discountAmount = calculateDiscountAmountWhileUnapplyingCampaign(campaign, product, quantity);

        return product.getCheckout().getTotalPrice() - discountAmount;
    }

    /**
     * Calculates the discount amount while applying a campaign.
     *
     * @param campaign       the campaign being applied
     * @param existingProduct the existing product in the checkout
     * @param quantity        the quantity of the product being added
     * @return the discount amount
     */
    private double calculateDiscountAmountWhileApplyingCampaign(Campaign campaign, Product existingProduct, Integer quantity) {
        if (existingProduct.getQuantity() >= campaign.getNeededQuantity() && existingProduct.getAppliedCampaign() == null){
            existingProduct.setAppliedCampaign(campaign.getName());
        }

        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() ==  1){
                return (existingProduct.getPrice() * campaign.getDiscountAmount()) / 100;
            }

            int numberOfProductsWithUnappliedCampaign = (existingProduct.getQuantity() - quantity) % campaign.getNeededQuantity();

            int counter = 0;

            for (int i = 1; i <= quantity + numberOfProductsWithUnappliedCampaign; i++) {
                if (i % campaign.getNeededQuantity() == 0){
                    counter++;
                }
            }

            return campaign.getNeededQuantity() * counter * ((existingProduct.getPrice() * campaign.getDiscountAmount()) / 100);
        } else if (campaign.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return campaign.getDiscountAmount() * quantity;
        }
        return 0;
    }

    /**
     * Calculates the discount amount while unapplying a campaign.
     *
     * @param campaign       the campaign being unapplied
     * @param existingProduct the existing product in the checkout
     * @param quantity        the quantity of the product being removed or returned
     * @return the discount amount
     */
    private double calculateDiscountAmountWhileUnapplyingCampaign(Campaign campaign, Product existingProduct, Integer quantity) {
        double price = existingProduct.getPrice();

        if (existingProduct.getQuantity() < campaign.getNeededQuantity()){
            existingProduct.setAppliedCampaign(null);
        }

        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() ==  1){
                double discount = price * campaign.getDiscountAmount() / 100;
                return (price - discount) * quantity;
            }

            int totalQuantity = existingProduct.getQuantity() + quantity;
            int numberOfTimesCampaignApplied = totalQuantity / campaign.getNeededQuantity();

            if (numberOfTimesCampaignApplied > 0){
                int numberOfProductsWithUnappliedCampaign = totalQuantity % campaign.getNeededQuantity();
                double priceToDecrease = calculate(campaign, quantity, numberOfProductsWithUnappliedCampaign, numberOfTimesCampaignApplied, price);

                return numberOfProductsWithUnappliedCampaign * existingProduct.getPrice() + priceToDecrease;
            }
        } else if (campaign.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            return (price - campaign.getDiscountAmount()) * quantity;
        }

        return price * quantity;
    }

    /**
     * Helper method for calculating discount amount.
     *
     * @param campaign                                 the campaign being applied or unapplied
     * @param quantity                                 the quantity of the product
     * @param numberOfProductsWithUnappliedCampaign    the number of products with unapplied campaigns
     * @param numberOfTimesCampaignApplied             the number of times the campaign is applied
     * @param price                                    the price of the product
     * @return the discount amount
     */
    private static double calculate(Campaign campaign, Integer quantity, int numberOfProductsWithUnappliedCampaign, int numberOfTimesCampaignApplied, double price) {
        double priceToDecrease = 0;
        int counter = 1;
        int quantityToDecrease = quantity - numberOfProductsWithUnappliedCampaign;
        int tmp = quantityToDecrease;

        for (int i = 0; i < numberOfTimesCampaignApplied && tmp > 0; i++){
            for (int j = 1; j <= quantityToDecrease; j++, tmp--) {
                if (j  != 1){
                    priceToDecrease += price;
                    counter++;
                }

                if (counter == campaign.getNeededQuantity()){
                    quantityToDecrease -= counter;
                    tmp--;
                    counter = 1;
                    break;
                }
            }
        }
        return priceToDecrease;
    }

    /**
     * Checks if the product is available in stock.
     *
     * @param req         the request containing product details
     * @param productInfo information about the product
     */
    private void checkIfProductAvailable(AddAndListProductReq req, ProductInfo productInfo){
        int stockAmount = getStockAmount(req, productInfo);
        boolean exists = productInfo.isExists();

        if (!exists || stockAmount == 0){
            throw new ProductNotFoundException(NOT_FOUND);
        }
    }

    /**
     * Sends information about returned products to the reporting service.
     *
     * @param product the product being returned
     */
    private void sendReturnedProductsInfoToReportingService(Product product) {
        ReturnedProductInfoDTO returnedProductInfoDTO = new ReturnedProductInfoDTO(
                product.getId(),
                product.getReturnedQuantity(),
                product.getCheckout().getReturnedMoney(),
                product.getQuantity(),
                product.isReturned()
        );

        saleReportProducer.sendReturnedProductInfoToReportingService("returned-product-info", returnedProductInfoDTO);
    }

    /**
     * Retrieves a map of product IDs with their corresponding quantities.
     *
     * @param quantity the quantity of the product
     * @return a map of product IDs with quantities
     */
    private Map<String, Integer> getProductsCodeWithQuantity(String code, Integer quantity) {
        Map<String, Integer> productCodeWithQuantity = new HashMap<>();

        Product product = shoppingBagRepository.findByCode(code)
                .orElseThrow(() -> new ProductNotFoundException(NOT_FOUND));

        productCodeWithQuantity.put(product.getCode(), quantity);
        return productCodeWithQuantity;
    }
}