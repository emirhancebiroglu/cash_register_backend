package bit.salesservice.utils;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;

/**
 * Utility class for making HTTP requests related to product information.
 */
@Component
@RequiredArgsConstructor
public class ProductInfoHttpRequest {
    private final WebClientConfig webClientConfig;
    private static final Logger logger = LogManager.getLogger(ProductInfoHttpRequest.class);

    /**
     * Retrieves product information from the backend API using the provided product code and authentication token.
     *
     * @param code      The product code for which to retrieve information.
     * @param authToken The authentication token used for authorization.
     * @return The product information retrieved from the backend API.
     */
    public ProductInfo getProductInfo(String code, String authToken){
        logger.info("Retrieving product information from the backend");

        ProductInfo productInfo = webClientConfig.webClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/check-product")
                        .queryParam("code", code)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(ProductInfo.class)
                .doOnError(error -> logger.error("Failed to retrieve product info : {}", error.getMessage()))
                .block();

        logger.info("Product information retrieved successfully");

        return productInfo;
    }

    /**
     * Updates the stock levels of products in the backend API using the provided authentication token and product quantity map.
     *
     * @param authToken               The authentication token used for authorization.
     * @param productsIdWithQuantity  A map containing product IDs and their corresponding quantities.
     * @param shouldDecrease          A boolean indicating whether to decrease the stock level (true) or increase it (false).
     */
    public void updateStocks(String authToken, Map<String, Integer> productsIdWithQuantity, boolean shouldDecrease){
        logger.info("Updating stock levels of products in the backend");

        webClientConfig.webClient().post()
                .uri("/api/products/update-stocks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .body(BodyInserters.fromValue(new UpdateStockRequest(productsIdWithQuantity, shouldDecrease)))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Failed to update stocks: {}", error.getMessage()))
                .subscribe();

        logger.info("Stock levels updated successfully");
    }
}
