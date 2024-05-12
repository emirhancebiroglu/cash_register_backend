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

    @Override
    public void cancelCheckout(Long checkoutId) {
        logger.info("Cancelling checkout process...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> {
                    logger.error("Checkout not found");
                    return new CheckoutNotFoundException("Checkout not found");
                });

        checkout.setCancelled(true);
        checkout.setCancelledDate(LocalDateTime.now());
        checkout.setReturnedMoney(checkout.getTotalPrice());

        for (Product product : checkout.getProducts()) {
            product.setReturned(true);
        }

        Map<String, Integer> productsIdWithQuantity = getProductsCodeWithQuantity(checkout);
        request.updateStocks(jwtToken, productsIdWithQuantity, false);

        checkoutRepository.save(checkout);

        sendCancelledSaleReportInfoToReportingService(checkout.getId(), checkout.isCancelled(), checkout.getCancelledDate(), checkout.getReturnedMoney());

        logger.info("Checkout cancelled successfully");
    }

    @Override
    public void completeCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId) {
        logger.info("Performing checkout process...");

        Checkout checkout = validateAndSetCheckout(completeCheckoutReq, checkoutId);
        checkoutRepository.save(checkout);

        Map<String, Integer> productsIdWithQuantity = getProductsCodeWithQuantity(checkout);
        request.updateStocks(jwtToken, productsIdWithQuantity, true);

        sendSaleReportToReportingService(checkout);

        logger.info("Checkout completed successfully");
    }

    @Override
    public void openSale() {
        Checkout checkout = new Checkout();
        checkout.setCreatedDate(LocalDateTime.now());
        checkout.setUpdatedDate(LocalDateTime.now());
        checkout.setTotalPrice(0D);
        checkoutRepository.save(checkout);
    }

    /**
     * Validates and sets the checkout details based on the completion request.
     *
     * @param completeCheckoutReq the request containing checkout completion details
     * @return the validated and updated checkout entity
     */
    private Checkout validateAndSetCheckout(CompleteCheckoutReq completeCheckoutReq, Long checkoutId) {
        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException("Checkout not found"));

        checkoutValidator.validateCheckout(checkout, completeCheckoutReq);

        checkout.setPaymentMethod(PaymentMethod.valueOf(completeCheckoutReq.getPaymentMethod()));
        checkout.setCompleted(true);
        checkout.setCompletedDate(LocalDateTime.now());

        if (checkout.getPaymentMethod() == PaymentMethod.CREDIT_CARD){
            checkout.setMoneyTaken(checkout.getTotalPrice());
            checkout.setChange(0D);
        }
        else{
            checkout.setMoneyTaken(completeCheckoutReq.getMoneyTaken());
            checkout.setChange(completeCheckoutReq.getMoneyTaken() - checkout.getTotalPrice());
        }
        return checkout;
    }

    /**
     * Retrieves a map of product IDs with their corresponding quantities from the checkout.
     *
     * @param checkout the checkout from which to retrieve products
     * @return a map of product IDs with quantities
     */
    private static Map<String, Integer> getProductsCodeWithQuantity(Checkout checkout) {
        Map<String, Integer> productsCodeWithQuantity = new HashMap<>();
        for (Product product : checkout.getProducts()) {
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
        List<ProductDTO> productDTOs = checkout.getProducts().stream()
                .map(this::mapToProductDTO)
                .toList();
        
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
        CancelledSaleReportDTO cancelledSaleReportDTO = new CancelledSaleReportDTO(id, cancelled, cancelledDate, returnedMoney);
        saleReportProducer.sendCancelledSaleReport("cancelled-sale-report", cancelledSaleReportDTO);
    }
}