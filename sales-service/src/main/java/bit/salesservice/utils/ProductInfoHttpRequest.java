package bit.salesservice.utils;

import bit.salesservice.config.WebClientConfig;
import bit.salesservice.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductInfoHttpRequest {
    private final WebClientConfig webClientConfig;

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
}
