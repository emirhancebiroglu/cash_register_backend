package bit.salesservice.service.serviceimpl;

import bit.salesservice.dto.CompleteCheckoutReq;
import bit.salesservice.dto.ProductDTO;
import bit.salesservice.dto.kafka.CancelledSaleReportDTO;
import bit.salesservice.dto.kafka.SaleReportDTO;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.PaymentMethod;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.service.CheckoutService;
import bit.salesservice.utils.ProductInfoHttpRequest;
import bit.salesservice.utils.SaleReportProducer;
import bit.salesservice.validators.CheckoutValidator;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for managing checkout operations.
 */
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final SaleReportProducer saleReportProducer;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private final CheckoutValidator checkoutValidator;
    private final ProductInfoHttpRequest request;
    private static final Logger logger = LogManager.getLogger(CheckoutServiceImpl.class);
    private static final String CHECKOUT_NOT_FOUND = "Checkout not found";


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
        logger.debug("Retrieved checkout: {}", checkout);

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

        // Log the products marked as returned for debugging purposes
        logger.debug("All products in the checkout marked as returned");

        // Update the stock quantities for the returned products
        Map<String, Integer> productsIdWithQuantity = getProductsCodeWithQuantity(checkout);
        request.updateStocks(jwtToken, productsIdWithQuantity, false);

        // Log the stock quantity update request details for debugging purposes
        logger.debug("Stock quantities updated for returned products: {}", productsIdWithQuantity);

        // Save the changes to the checkout entity
        checkoutRepository.save(checkout);

        // Send cancellation report to the reporting service
        sendCancelledSaleReportInfoToReportingService(checkout.getId(), checkout.isCancelled(), checkout.getCancelledDate(), checkout.getReturnedMoney());

        logger.trace("Checkout cancelled successfully");
    }

    @Override
    public void completeCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId) {
        logger.trace("Performing checkout process...");

        // Validate and update the checkout with the provided details
        Checkout checkout = validateAndSetCheckout(completeCheckoutReq, checkoutId);

        // Log the validated checkout details for debugging purposes
        logger.debug("Validated checkout: {}", checkout);

        checkoutRepository.save(checkout);

        // Log the checkout update for debugging purposes
        logger.debug("Checkout details updated successfully");

        // Update stock quantities for purchased products
        Map<String, Integer> productsIdWithQuantity = getProductsCodeWithQuantity(checkout);

        // Log the products with their quantities for debugging purposes
        logger.debug("Products and their quantities to be updated in stock: {}", productsIdWithQuantity);

        request.updateStocks(jwtToken, productsIdWithQuantity, true);

        // Log the successful update of stock quantities
        logger.debug("Stock quantities updated successfully");

        // Send sale report to the reporting service
        sendSaleReportToReportingService(checkout);

        logger.trace("Checkout completed successfully");
    }

    @Override
    public void openSale() {
        logger.trace("Opening new sale...");

        // Create a new checkout entity
        Checkout checkout = new Checkout();
        checkout.setCreatedDate(LocalDateTime.now());
        checkout.setUpdatedDate(LocalDateTime.now());
        checkout.setTotalPrice(0D);

        // Log the creation of the new checkout entity for debugging purposes
        logger.debug("New checkout entity created: {}", checkout);

        // Save the new checkout entity
        checkoutRepository.save(checkout);

        logger.trace("Sale opened successfully");
    }

    /**
     * Validates and sets the checkout details based on the completion request.
     *
     * @param completeCheckoutReq the request containing checkout completion details
     * @return the validated and updated checkout entity
     */
    private Checkout validateAndSetCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId) {
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

        return checkout;
    }

    /**
     * Retrieves a map of product IDs with their corresponding quantities from the checkout.
     *
     * @param checkout the checkout from which to retrieve products
     * @return a map of product IDs with quantities
     */
    private static Map<String, Integer> getProductsCodeWithQuantity(Checkout checkout) {
        // Initialize a map to store product codes with quantities
        Map<String, Integer> productsCodeWithQuantity = new HashMap<>();

        // Iterate through the products in the checkout
        for (Product product : checkout.getProducts()) {
            // Add the product code and quantity to the map
            productsCodeWithQuantity.put(product.getCode(), product.getQuantity());
        }
        return productsCodeWithQuantity;
    }

    /**
     * Sends the sale report to the reporting service.
     *
     * @param checkout the checkout containing sale details
     */
    private void sendSaleReportToReportingService(Checkout checkout){
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
    }

    /**
     * Maps a product entity to a DTO for sale reporting.
     *
     * @param product the product to be mapped
     * @return the mapped product DTO
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
     * Sends information about the cancelled sale report to the reporting service.
     *
     * @param id            the ID of the cancelled sale
     * @param cancelled     true if the sale is cancelled, false otherwise
     * @param cancelledDate the date when the sale was cancelled
     * @param returnedMoney the amount of money returned for the cancelled sale
     */
    private void sendCancelledSaleReportInfoToReportingService(Long id, boolean cancelled, LocalDateTime cancelledDate, Double returnedMoney) {
        // Create a CancelledSaleReportDTO with the provided information
        CancelledSaleReportDTO cancelledSaleReportDTO = new CancelledSaleReportDTO(id, cancelled, cancelledDate, returnedMoney);

        // Send the cancelled sale report DTO to the reporting service
        saleReportProducer.sendCancelledSaleReport("cancelled-sale-report", cancelledSaleReportDTO);
    }
}