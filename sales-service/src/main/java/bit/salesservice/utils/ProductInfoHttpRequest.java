package bit.salesservice.utils;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.ProductInfo;
import bit.salesservice.dto.UpdateStockRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductInfoHttpRequest {
    private final WebClientConfig webClientConfig;
    private static final Logger logger = LoggerFactory.getLogger(ProductInfoHttpRequest.class);

    public ProductInfo getProductInfo(String code, String authToken){
        return webClientConfig.webClient().get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/products/check-product")
                        .queryParam("code", code)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .retrieve()
                .bodyToMono(ProductInfo.class)
                .block();
    }

    public void updateStocks(String authToken, Map<String, Integer> productsIdWithQuantity, boolean shouldDecrease){
        webClientConfig.webClient().post()
                .uri("/api/products/update-stocks")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authToken)
                .body(BodyInserters.fromValue(new UpdateStockRequest(productsIdWithQuantity, shouldDecrease)))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(error -> logger.error("Failed to update stocks: {}", error.getMessage()))
                .subscribe();
    }
}
