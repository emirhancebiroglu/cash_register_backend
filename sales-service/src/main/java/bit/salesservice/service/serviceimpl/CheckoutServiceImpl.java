package bit.salesservice.service.serviceimpl;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.UpdateStockRequest;
import bit.salesservice.entity.Checkout;
import bit.salesservice.entity.PaymentMethod;
import bit.salesservice.entity.Product;
import bit.salesservice.exceptions.checkoutnotfound.CheckoutNotFoundException;
import bit.salesservice.repository.CheckoutRepository;
import bit.salesservice.service.CheckoutService;
import bit.salesservice.validators.CheckoutValidator;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
    private final CheckoutRepository checkoutRepository;
    private final WebClientConfig webClientConfig;
    private final String jwtToken = HttpHeaders.AUTHORIZATION.substring(7);
    private final CheckoutValidator checkoutValidator;
    private static final Logger logger = LoggerFactory.getLogger(CheckoutServiceImpl.class);

    @Override
    public void cancelCheckout(Long checkoutId) {
        logger.info("Cancelling checkout process...");

        Checkout checkout = checkoutRepository.findById(checkoutId)
                .orElseThrow(() -> new CheckoutNotFoundException("Checkout not found"));

        checkout.setCancelled(true);
        checkout.setCompleted(false);
        checkout.setCancelledDate(LocalDateTime.now());

        for (Product product : checkout.getProducts()) {
            product.setReturned(true);
        }

        Map<String, Integer> productsIdWithQuantity = getProductsIdWithQuantity(checkout);
        updateStocks(jwtToken, productsIdWithQuantity, false);

        checkoutRepository.save(checkout);

        logger.info("Checkout cancelled successfully");
    }

    @Override
    public void completeCheckout(String paymentMethodStr) {
        logger.info("Performing checkout process...");

        Checkout checkout = validateAndSetCheckout(paymentMethodStr);
        checkoutRepository.save(checkout);

        Checkout nextCheckout = new Checkout();
        nextCheckout.setCreatedDate(LocalDateTime.now());
        nextCheckout.setUpdatedDate(LocalDateTime.now());
        checkoutRepository.save(nextCheckout);

        Map<String, Integer> productsIdWithQuantity = getProductsIdWithQuantity(checkout);
        updateStocks(jwtToken, productsIdWithQuantity, true);

        logger.info("Checkout completed successfully");
    }

    private Checkout validateAndSetCheckout(String paymentMethodStr) {
        Checkout checkout = checkoutRepository.findFirstByOrderByIdDesc();

        checkoutValidator.validateCheckout(checkout, paymentMethodStr);

        checkout.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));
        checkout.setCompleted(true);
        checkout.setCompletedDate(LocalDateTime.now());
        return checkout;
    }

    private void updateStocks(String authToken, Map<String, Integer> productsIdWithQuantity, boolean shouldDecrease){
        webClientConfig.webClient().post()
                .uri("/api/products/update-stocks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .body(BodyInserters.fromValue(new UpdateStockRequest(productsIdWithQuantity, shouldDecrease)))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Failed to update stocks: {}", error.getMessage()))
                .subscribe();
    }

    private static Map<String, Integer> getProductsIdWithQuantity(Checkout checkout) {
        Map<String, Integer> productsIdWithQuantity = new HashMap<>();
        for (Product product : checkout.getProducts()) {
            productsIdWithQuantity.put(product.getCode(), product.getQuantity());
        }
        return productsIdWithQuantity;
    }
}