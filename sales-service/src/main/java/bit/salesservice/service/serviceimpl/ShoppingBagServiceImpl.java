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
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.repository.ShoppingBagRepository;
import bit.salesservice.service.ShoppingBagService;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import bit.salesservice.validators.BagValidator;
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
    private final BagValidator bagValidator;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private static final Logger logger = LogManager.getLogger(ShoppingBagServiceImpl.class);
    private final ProductInfoHttpRequest httpRequest;
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String CHECKOUT_NOT_FOUND = "Checkout not found";
    private static final String NOT_IN_STOCKS = "There is not enough product in stocks.";
    private static final String RETRIEVED_CHECKOUT = "Retrieved checkout: {}";
    private static final String UPDATED_PRODUCT = "Updated product: {}";


    @Override
    public void addProductToBag(AddAndListProductReq req, Long checkoutId) {
        logger.trace("Adding product to shopping bag...");

        // Retrieve product information
        ProductInfo productInfo = httpRequest.getProductInfo(req.getCode(), jwtToken);

        // Log the retrieved product information for debugging purposes
        logger.debug("Retrieved product information: {}", productInfo);

        // Check if the product is available
        checkIfProductAvailable(req, productInfo);

        // Log the availability of the product for debugging purposes
        logger.debug("Product is available");

        // Retrieve the checkout
        Checkout checkout = getCheckout(checkoutId);

        // Log the retrieved checkout for debugging purposes
        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Validate adding the product to the bag
        bagValidator.validationForAddingToBag(req, productInfo, checkout);

        // Log the successful validation for adding the product to the bag
        logger.debug("Product validation for adding to bag successful");

        // Check if the product is already in the bag
        Product existingProduct = shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout);

        // Log the existence of the product in the bag, if any, for debugging purposes
        logger.debug("Existing product in bag: {}", existingProduct);

        // Update existing product or add new product to the bag
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

        // Log the updated product for debugging purposes
        logger.debug(UPDATED_PRODUCT, existingProduct);

        // Update the total price of the checkout
        updateCheckoutTotalForAddingProduct(existingProduct, productInfo.getPrice(), req.getQuantity(), checkout);

        // Save the product to the shopping bag
        shoppingBagRepository.save(existingProduct);

        logger.trace("Product added successfully");
    }

    @Override
    public void removeProductFromBag(RemoveOrReturnProductFromBagReq request) {
        logger.trace("Removing product from shopping bag...");

        // Retrieve the product from the shopping bag
        Product product = getProduct(request);

        // Log the retrieved product for debugging purposes
        logger.debug("Retrieved product: {}", product);

        // Retrieve the checkout
        Checkout checkout = getCheckout(request.getCheckoutId());

        // Log the retrieved checkout for debugging purposes
        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Validate removing or returning the product from the bag
        int productQuantity = product.getQuantity();
        bagValidator.validationForRemoveProductFromBag(request, productQuantity);

        // Log the validation result for debugging purposes
        logger.debug("Product removal validation successful");

        // Update product quantity or mark it as removed
        if (productQuantity > request.getQuantity()){
            product.setQuantity(productQuantity - request.getQuantity());
        }
        else{
            product.setQuantity(0);
            product.setRemoved(true);
        }

        // Log the updated product for debugging purposes
        logger.debug(UPDATED_PRODUCT, product);

        // Update the total price of the checkout
        updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), request.getQuantity(), checkout);

        // Save the changes to the product in the shopping bag
        shoppingBagRepository.save(product);

        logger.trace("Product removed successfully");
    }

    @Override
    @Transactional
    public void removeAll(Long checkoutId) {
        logger.trace("Removing all products from shopping bag...");

        // Retrieve the checkout
        Checkout checkout = getCheckout(checkoutId);

        // Retrieve all products in the shopping bag associated with the checkout
        List<Product> products = shoppingBagRepository.findByCheckoutId(checkoutId);

        // Log the retrieved products for debugging purposes
        logger.debug("Retrieved products: {}", products);

        // Mark all products as removed and update their quantities
        for (Product product : products){
            if (!product.isRemoved()){
                product.setRemoved(true);
                product.setQuantity(0);
            }
        }

        // Log the updated products for debugging purposes
        logger.debug("Updated products: {}", products);

        // Save the changes to the products in the shopping bag
        shoppingBagRepository.saveAll(products);

        // Reset the total price of the checkout to zero
        checkout.setTotalPrice(0D);
        checkoutRepository.save(checkout);

        logger.trace("All products removed successfully");
    }

    @Override
    public List<AddAndListProductReq> getProductsInBag(Long checkoutId) {
        logger.trace("Getting products in shopping bag for current checkout...");

        // Retrieve the checkout
        Checkout checkout = getCheckout(checkoutId);

        // Log the retrieved checkout for debugging purposes
        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Retrieve products in the shopping bag associated with the checkout
        List<AddAndListProductReq> products = shoppingBagRepository.findProductReqByCheckoutAndRemoved(checkout);

        // Log the fetched products for debugging purposes
        logger.debug("Fetched products: {}", products);

        logger.trace("Products fetched successfully.");
        return products;
    }

    @Override
    public void returnProductFromBag(RemoveOrReturnProductFromBagReq request) {
        logger.trace("Returning product from shopping bag...");

        // Retrieve the product from the shopping bag
        Product product = getProduct(request);

        // Log the retrieved product for debugging purposes
        logger.debug("Retrieved product: {}", product);

        // Retrieve the checkout associated with the product
        Checkout checkout = getCheckout(request.getCheckoutId());

        // Log the retrieved checkout for debugging purposes
        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Check if the checkout is completed
        if (checkout.isCompleted()) {
            // Log the completion status for debugging purposes
            logger.debug("Checkout is completed");

            // Validate returning the product from the bag
            int productQuantity = product.getQuantity();
            bagValidator.validationForReturnProductFromBag(request, product, productQuantity);

            // Update product quantity or mark it as returned
            if (productQuantity > request.getQuantity()) {
                product.setQuantity(productQuantity - request.getQuantity());
            } else {
                product.setQuantity(0);
                product.setReturned(true);
            }

            // Log the updated product information for debugging purposes
            logger.debug(UPDATED_PRODUCT, product);

            // Update returned quantity and checkout total price
            product.setReturnedQuantity(product.getReturnedQuantity() + request.getQuantity());
            updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), request.getQuantity(), checkout);

            // Update stocks and save changes to the product in the shopping bag
            Map<String, Integer> productsIdWithQuantity = getProductsCodeWithQuantity(request.getCode(), request.getQuantity());
            httpRequest.updateStocks(jwtToken, productsIdWithQuantity, false);
            shoppingBagRepository.save(product);

            // Send returned products information to the reporting service
            sendReturnedProductsInfoToReportingService(product);
        }
        else{
            logger.error("Checkout is not completed.");
            throw new UncompletedCheckoutException("Checkout is not completed.");
        }

        logger.trace("Product returned successfully");
    }

    /**
     * Retrieves the checkout object from the repository by its id.
     *
     * @param checkoutId the id of the checkout to be retrieved
     * @return the checkout object
     * @throws CheckoutNotFoundException if the checkout with the given id is not found
     */
    private Checkout getCheckout(Long checkoutId) {
        return checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> {
                    logger.error(CHECKOUT_NOT_FOUND);
                    return new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
                });
    }

    /**
     * Updates the total price of the checkout when adding a product.
     *
     * @param product  the product being added
     * @param price    the price of the product
     * @param quantity the quantity of the product being added
     */
    private void updateCheckoutTotalForAddingProduct(Product product, double price, Integer quantity, Checkout checkout){
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
    private void updateCheckoutTotalForRemovingOrReturningProduct(Product product, double price, Integer quantity, Checkout checkout){
        // Calculate the new total price after removing or returning the product
        double newTotalPrice = unApplyCampaignsAndUpdatePrice(product, price * quantity, quantity);

        // Adjust returned money if any quantity of the product was returned
        if (product.getReturnedQuantity() > 0){
            checkout.setReturnedMoney(checkout.getReturnedMoney() + (checkout.getTotalPrice() - newTotalPrice));
        }

        // Update the total price and other checkout details
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
            logger.error(PRODUCT_NOT_FOUND);
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND);
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
        // Find the campaign associated with the product
        Campaign campaign = campaignRepository.findFirstByCodesContaining(existingProduct.getCode());

        // If no active campaign is found, return the current total price plus the total price of the product
        if (campaign == null || campaign.isInactive()) {
            return existingProduct.getCheckout().getTotalPrice() + total;
        }

        // Calculate the discount amount while applying the campaign
        double discountAmount = calculateDiscountAmountWhileApplyingCampaign(campaign, existingProduct, quantity);

        // Return the updated total price after applying discounts
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
        // Find the campaign associated with the product
        Campaign campaign = campaignRepository.findFirstByCodesContaining(product.getCode());

        // If no campaign is found, return the current total price minus the total price of the product
        if (campaign == null) {
            return product.getCheckout().getTotalPrice() - total;
        }

        // Calculate the discount amount while unapplying the campaign
        double discountAmount = calculateDiscountAmountWhileUnapplyingCampaign(campaign, product, quantity);

        // Return the updated total price after unapplying discounts
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
        // Set the applied campaign if the quantity meets the required quantity and no campaign is already applied
        if (existingProduct.getQuantity() >= campaign.getNeededQuantity() && existingProduct.getAppliedCampaign() == null){
            existingProduct.setAppliedCampaign(campaign.getName());
        }

        // Calculate discount based on the type of campaign
        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() ==  1){
                return (existingProduct.getPrice() * campaign.getDiscountAmount()) / 100;
            }

            // Calculate discount amount for multiple products
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

        // Remove applied campaign if the product quantity falls below the required quantity
        if (existingProduct.getQuantity() < campaign.getNeededQuantity()){
            existingProduct.setAppliedCampaign(null);
        }

        // Calculate discount based on the type of campaign
        if (campaign.getDiscountType() == DiscountType.PERCENTAGE) {
            if (campaign.getNeededQuantity() ==  1){
                double discount = price * campaign.getDiscountAmount() / 100;
                return (price - discount) * quantity;
            }

            // Calculate discount amount for multiple products
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

        // Iterate through the products to calculate the discount amount
        for (int i = 0; i < numberOfTimesCampaignApplied && tmp > 0; i++){
            for (int j = 1; j <= quantityToDecrease; j++, tmp--) {
                // Increase priceToDecrease if the counter is not 1 (for multiple products)
                if (j  != 1){
                    priceToDecrease += price;
                    counter++;
                }

                // Check if the required quantity for the campaign is reached
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

        // Check if the product exists and is available in stock
        if (!exists || stockAmount == 0){
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND);
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

        // sends the DTO to the reporting service
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
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));

        productCodeWithQuantity.put(product.getCode(), quantity);
        return productCodeWithQuantity;
    }

    /**
     * Retrieves a product from the shopping bag by its code.
     *
     * @param request the request containing the product code
     * @return the product found in the shopping bag
     * @throws ProductNotFoundException if the product with the given code is not found
     */
    private Product getProduct(RemoveOrReturnProductFromBagReq request) {
        return shoppingBagRepository.findByCode(request.getCode())
                .orElseThrow(() -> {
                    logger.error(PRODUCT_NOT_FOUND);
                    return new ProductNotFoundException(PRODUCT_NOT_FOUND);
                });
    }
}