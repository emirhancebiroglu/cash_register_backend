package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.*;
import bit.salesservice.dto.kafka.CancelledSaleReportDTO;
import bit.salesservice.dto.kafka.ReturnedProductInfoDTO;
import bit.salesservice.dto.kafka.SaleReportDTO;
import bit.salesservice.entity.*;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.exceptions.notinstocks.NotInStocksException;
import bit.salesservice.exceptions.productnotfound.ProductNotFoundException;
import bit.salesservice.exceptions.uncompletedcheckoutexception.UncompletedCheckoutException;
import bit.salesservice.repository.CampaignRepository;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.repository.ShoppingBagRepository;
import bit.salesservice.service.CheckoutService;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import bit.salesservice.validators.BagValidator;
import bit.salesservice.validators.CheckoutValidator;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final SaleReportProducer saleReportProducer;
    private final ProductInfoHttpRequest httpRequest;
    private final ShoppingBagRepository shoppingBagRepository;
    private final BagValidator bagValidator;
    private final CampaignRepository campaignRepository;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private final CheckoutValidator checkoutValidator;
    private final ProductInfoHttpRequest request;
    private static final Logger logger = LogManager.getLogger(CheckoutServiceImpl.class);
    private static final String CHECKOUT_NOT_FOUND = "Checkout not found";
    private static final String PRODUCT_NOT_FOUND = "Product not found";
    private static final String NOT_IN_STOCKS = "There is not enough product in stocks.";
    private static final String UPDATED_PRODUCT = "Updated product: {}";
    private static final String RETRIEVED_CHECKOUT = "Retrieved checkout: {}";

    @Transactional
    @Override
    public void createCheckout(List<AddAndListProductReq> reqs) {
        logger.trace("Creating checkout and adding products...");

        // Create a new checkout entity
        Checkout checkout = new Checkout();
        checkout.setCreatedDate(LocalDateTime.now());
        checkout.setUpdatedDate(LocalDateTime.now());
        checkout.setTotalPrice(0D);

        // Save the new checkout entity
        checkoutRepository.save(checkout);

        addProductsToBag(reqs, checkout.getId());

        logger.trace("Created checkout and added products");
    }

    @Override
    public void completeCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId) {
        logger.trace("Performing checkout process...");

        // Retrieve the checkout entity from the repository
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> {
                    logger.error(CHECKOUT_NOT_FOUND);
                    return  new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
                });

        // Variable to hold the amount of money taken
        Double moneyTaken;

        // Validate the checkout details
        checkoutValidator.validateCheckout(checkout, completeCheckoutReq);

        // Determine the payment method and calculate the money taken
        if (completeCheckoutReq.getMoneyTakenFromCash() != null && completeCheckoutReq.getMoneyTakenFromCard() == null) {
            checkout.setPaymentMethod(PaymentMethod.CASH);
            moneyTaken = completeCheckoutReq.getMoneyTakenFromCash();
        } else if (completeCheckoutReq.getMoneyTakenFromCash() == null) {
            checkout.setPaymentMethod(PaymentMethod.CREDIT_CARD);
            moneyTaken = completeCheckoutReq.getMoneyTakenFromCard();
        } else {
            checkout.setPaymentMethod(PaymentMethod.PARTIAL);
            moneyTaken = completeCheckoutReq.getMoneyTakenFromCash() + completeCheckoutReq.getMoneyTakenFromCard();
        }

        // Set the money taken, change, and completion details
        checkout.setMoneyTaken(moneyTaken);
        checkout.setChange(moneyTaken - checkout.getTotalPrice());
        checkout.setCompleted(true);
        checkout.setCompletedDate(LocalDateTime.now());

        // Log the validated checkout details for debugging purposes
        logger.debug("Validated checkout: {}", checkout);

        checkoutRepository.save(checkout);

        // Map products in the checkout to product DTOs
        List<ProductDTO> productDTOs = checkout.getProducts().stream()
                .map(this::mapToProductDTO)
                .toList();

        // Create a SaleReportDTO from checkout details and product DTOs
        SaleReportDTO saleReportDTO = new SaleReportDTO(
                checkout.getId(),
                productDTOs,
                checkout.getTotalPrice(),
                checkout.getPaymentMethod(),
                checkout.getMoneyTaken(),
                checkout.getChange(),
                checkout.getCompletedDate(),
                checkout.getCancelledDate(),
                checkout.getReturnedMoney()
        );

        // Send the sale report DTO to the reporting service
        saleReportProducer.sendSaleReport("sale-report", saleReportDTO);

        logger.trace("Checkout completed successfully");
    }

    @Override
    public void cancelCheckout(Long checkoutId) {
        logger.trace("Cancelling checkout process...");

        // Retrieve the checkout entity from the repository
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> {
                    logger.error(CHECKOUT_NOT_FOUND);
                    return new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
                });

        // Log the retrieved checkout details for debugging purposes
        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Mark the checkout as cancelled
        checkout.setCancelled(true);
        checkout.setCancelledDate(LocalDateTime.now());
        checkout.setReturnedMoney(checkout.getTotalPrice());

        // Log the cancellation details for debugging purposes
        logger.debug("Checkout cancelled. Cancelled date: {}, Total price returned: {}", checkout.getCancelledDate(), checkout.getReturnedMoney());

        // Mark all products in the checkout as returned
        for (Product product : checkout.getProducts()) {
            product.setReturned(true);
        }

        // Update the stock quantities for the returned products
        Map<String, Integer> productsCodeWithQuantity = new HashMap<>();

        // Iterate through the products in the checkout
        for (Product product : checkout.getProducts()) {
            // Add the product code and quantity to the map
            productsCodeWithQuantity.put(product.getCode(), product.getQuantity());
        }

        request.updateStocks(jwtToken, productsCodeWithQuantity, false);

        // Log the stock quantity update request details for debugging purposes
        logger.debug("Stock quantities updated for returned products: {}", productsCodeWithQuantity);

        checkoutRepository.save(checkout);

        // Create a CancelledSaleReportDTO with the provided information
        CancelledSaleReportDTO cancelledSaleReportDTO = new CancelledSaleReportDTO(checkout.getId(), checkout.isCancelled(), checkout.getCancelledDate(), checkout.getReturnedMoney());

        // Send the cancelled sale report DTO to the reporting service
        saleReportProducer.sendCancelledSaleReport("cancelled-sale-report", cancelledSaleReportDTO);

        logger.trace("Checkout cancelled successfully");
    }

    @Transactional
    @Override
    public void addProductsToBag(List<AddAndListProductReq> reqs, Long checkoutId) {
        logger.trace("Adding products to Bag...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException(CHECKOUT_NOT_FOUND));

        // Map to store products and their quantities for stock update
        Map<String, Integer> productsIdWithQuantity = new HashMap<>();

        // Iterate over each product request and add the products to the bag
        for (AddAndListProductReq req : reqs) {
            // Retrieve product information
            ProductInfo productInfo = httpRequest.getProductInfo(req.getCode(), jwtToken);

            logger.debug("Retrieved product information: {}", productInfo);

            // Check if the product is available
            checkIfProductAvailable(req, productInfo);

            // Validate adding the product to the bag
            bagValidator.validationForAddingToBag(req, productInfo, checkout);

            // Check if the product is already in the bag
            Product existingProduct = shoppingBagRepository.findByCodeAndCheckout(req.getCode(), checkout);

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
                existingProduct.setQuantity(req.getQuantity());
                existingProduct.setPrice(productInfo.getPrice());
                existingProduct.setCheckout(checkout);
            }

            logger.debug(UPDATED_PRODUCT, existingProduct);

            // Update the total price of the checkout
            updateCheckoutTotalForAddingProduct(existingProduct, productInfo.getPrice(), req.getQuantity(), checkout);

            // Update stock quantities for purchased products
            productsIdWithQuantity.put(existingProduct.getCode(), existingProduct.getQuantity());

            shoppingBagRepository.save(existingProduct);

            logger.trace("Product added successfully: {}", existingProduct.getCode());
        }

        // Update stock quantities for all processed products
        request.updateStocks(jwtToken, productsIdWithQuantity, true);
    }

    @Transactional
    @Override
    public void removeProductsFromBag(List<RemoveOrReturnProductFromBagReq> reqs, Long checkoutId) {
        logger.trace("Removing products from shopping bag...");

        // Retrieve the checkout once
        Checkout checkout = getCheckout(checkoutId);

        Map<String, Integer> productCodeWithQuantity = new HashMap<>();

        logger.debug(RETRIEVED_CHECKOUT, checkout);

        for (RemoveOrReturnProductFromBagReq req : reqs) {
            // Retrieve the product from the shopping bag
            Product product = getProduct(req, checkout);

            logger.debug("Retrieved product: {}", product);

            // Validate removing or returning the product from the bag
            int productQuantity = product.getQuantity();
            bagValidator.validationForRemoveProductFromBag(req, productQuantity, checkout);

            // Update product quantity or mark it as removed
            if (productQuantity > req.getQuantity()) {
                product.setQuantity(productQuantity - req.getQuantity());
            } else {
                product.setQuantity(0);
                product.setRemoved(true);
            }

            logger.debug(UPDATED_PRODUCT, product);

            // Update the total price of the checkout
            updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), req.getQuantity(), checkout);

            productCodeWithQuantity.put(product.getCode(), product.getQuantity());

            shoppingBagRepository.save(product);

            logger.trace("Product removed successfully: {}", product.getCode());
        }

        httpRequest.updateStocks(jwtToken, productCodeWithQuantity, false);

        logger.trace("All products processed successfully");
    }

    @Override
    public void returnProductFromBag(RemoveOrReturnProductFromBagReq request, Long checkoutId) {
        logger.trace("Returning product from shopping bag...");

        // Retrieve the checkout associated with the product
        Checkout checkout = getCheckout(checkoutId);

        // Retrieve the product from the shopping bag
        Product product = getProduct(request, checkout);

        logger.debug("Retrieved product: {}", product);

        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Check if the checkout is completed
        if (checkout.isCompleted()) {
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

            logger.debug(UPDATED_PRODUCT, product);

            // Update returned quantity and checkout total price
            product.setReturnedQuantity(product.getReturnedQuantity() + request.getQuantity());
            updateCheckoutTotalForRemovingOrReturningProduct(product, product.getPrice(), request.getQuantity(), checkout);

            increaseStockAmount(request, product);
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

    @Override
    @Transactional
    public void removeAll(Long checkoutId) {
        logger.trace("Removing all products from shopping bag...");

        // Retrieve the checkout
        Checkout checkout = getCheckout(checkoutId);

        // Retrieve all products in the shopping bag associated with the checkout
        List<Product> products = shoppingBagRepository.findByCheckoutId(checkoutId);

        logger.debug("Retrieved products: {}", products);

        // Mark all products as removed and update their quantities
        for (Product product : products){
            if (!product.isRemoved()){
                product.setRemoved(true);
                product.setQuantity(0);
            }
        }

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

        logger.debug(RETRIEVED_CHECKOUT, checkout);

        // Retrieve products in the shopping bag associated with the checkout
        List<AddAndListProductReq> products = shoppingBagRepository.findProductReqByCheckoutAndRemoved(checkout);

        logger.trace("Products fetched successfully.");
        return products;
    }

    /**
     * Maps a Product entity to a ProductDTO object.
     *
     * @param product The Product entity to be mapped.
     * @return The mapped ProductDTO object.
     */
    private ProductDTO mapToProductDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getAppliedCampaign(),
                product.getQuantity(),
                product.getReturnedQuantity(),
                product.isReturned(),
                product.getPrice()
        );
    }

    /**
     * Checks if the product is available in the stock.
     *
     * @param req The request containing the product code and quantity.
     * @param productInfo The product information retrieved from the product service.
     * @throws NotInStocksException If the product quantity is greater than the available stock.
     * @throws ProductNotFoundException If the product does not exist or is not available in stock.
     */
    private void checkIfProductAvailable(AddAndListProductReq req, ProductInfo productInfo){
        boolean exists = productInfo.isExists();
        int stockAmount = productInfo.getStockAmount();

        // Check if the product exists and is available in stock
        if (!exists || stockAmount == 0){
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND);
        }

        if (stockAmount < req.getQuantity()) {
            logger.error(NOT_IN_STOCKS);
            throw new NotInStocksException("There is not enough product in stocks. Please provide a valid quantity");
        }
    }

    /**
     * Updates the total price of the checkout after adding a product.
     * Applies the applicable campaigns and updates the price of the product.
     *
     * @param product The product that is being added to the checkout.
     * @param price The price of the product.
     * @param quantity The quantity of the product being added.
     * @param checkout The checkout to which the product is being added.
     */
    private void updateCheckoutTotalForAddingProduct(Product product, double price, Integer quantity, Checkout checkout){
        checkout.setTotalPrice(applyCampaignsAndUpdatePrice(product, price * quantity, quantity));
        checkout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(checkout);
    }

    /**
     * Applies the applicable campaigns and updates the price of the product.
     *
     * @param existingProduct The product that is being added to the checkout.
     * @param total The total price of the product before applying the campaign.
     * @param quantity The quantity of the product being added.
     * @return The updated total price after applying discounts.
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
     * Calculates the discount amount while applying the campaign.
     *
     * @param campaign The campaign to be applied.
     * @param existingProduct The product to which the campaign is being applied.
     * @param quantity The quantity of the product being added to the checkout.
     * @return The discount amount to be applied to the product.
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
     * Retrieves the checkout associated with the given checkoutId.
     * If the checkout is not found, it throws a CheckoutNotFoundException.
     *
     * @param checkoutId The id of the checkout to be retrieved.
     * @return The retrieved checkout.
     * @throws CheckoutNotFoundException If the checkout is not found.
     */
    private Checkout getCheckout(Long checkoutId) {
        return checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> {
                    logger.error(CHECKOUT_NOT_FOUND);
                    return new CheckoutNotFoundException(CHECKOUT_NOT_FOUND);
                });
    }

    /**
     * Retrieves the product associated with the given request and checkout.
     * If the product is not found, it throws a ProductNotFoundException.
     *
     * @param request The request containing the product code.
     * @param checkout The checkout to which the product is associated.
     * @return The retrieved product.
     * @throws ProductNotFoundException If the product is not found.
     */
    private Product getProduct(RemoveOrReturnProductFromBagReq request, Checkout checkout) {
        return Optional.ofNullable(shoppingBagRepository.findByCodeAndCheckout(request.getCode(), checkout))
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND + ": " + request.getCode()));
    }

    /**
     * Updates the total price of the checkout after removing or returning a product.
     * Applies the applicable campaigns and updates the price of the product.
     * Adjusts the returned money if any quantity of the product was returned.
     *
     * @param product The product that is being removed or returned from the checkout.
     * @param price The price of the product.
     * @param quantity The quantity of the product being removed or returned.
     * @param checkout The checkout to which the product is being removed or returned.
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
     * Unapplies the applicable campaigns and updates the price of the product.
     *
     * @param product The product that is being removed or returned from the checkout.
     * @param total The total price of the product before applying the campaign.
     * @param quantity The quantity of the product being removed or returned.
     * @return The updated total price after unapplying discounts.
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
     * Calculates the discount amount while unapplying the campaign.
     *
     * @param campaign The campaign to be unapplied.
     * @param existingProduct The product to which the campaign is being unapplied.
     * @param quantity The quantity of the product being removed or returned.
     * @return The updated total price after unapplying discounts.
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
     * Calculates the discount amount while unapplying the campaign.
     *
     * @param campaign The campaign to be unapplied.
     * @param quantity The quantity of the product being removed or returned.
     * @param numberOfProductsWithUnappliedCampaign The quantity of products that do not meet the required quantity for the campaign.
     * @param numberOfTimesCampaignApplied The number of times the campaign has been applied.
     * @param price The price of the product.
     * @return The discount amount to be subtracted from the total price.
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
     * Sends the returned product information to the reporting service.
     *
     * @param product The product that was returned.
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
     * Increases the stock amount of the product based on the quantity returned.
     *
     * @param request The request containing the product code and quantity to be returned.
     * @param product The product that was returned.
     */
    private void increaseStockAmount(RemoveOrReturnProductFromBagReq request, Product product) {
        // Create a map to store the product code with the quantity to be increased
        Map<String, Integer> productCodeWithQuantity = new HashMap<>();
        productCodeWithQuantity.put(product.getCode(), request.getQuantity());

        // Call the updateStocks method of the httpRequest object to increase the stock amount
        // The second parameter is set to false to indicate that the stocks are being increased
        httpRequest.updateStocks(jwtToken, productCodeWithQuantity, false);
    }
}