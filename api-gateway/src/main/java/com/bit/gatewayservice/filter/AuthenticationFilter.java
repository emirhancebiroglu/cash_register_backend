package com.bit.gatewayservice.filter;

import com.bit.gatewayservice.config.WebClientConfig;
import com.bit.gatewayservice.exceptions.invalidrole.InvalidRoleException;
import com.bit.gatewayservice.exceptions.invalidtoken.InvalidTokenException;
import com.bit.gatewayservice.exceptions.missingauthorizationheader.MissingAuthorizationHeaderException;
import com.bit.gatewayservice.util.JwtUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final RouteValidator validator;
  private final JwtUtil jwtUtil;
  private final WebClientConfig webClientConfig;
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

  public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil, WebClientConfig webClientConfig) {
    super(Config.class);
    this.validator = validator;
    this.jwtUtil = jwtUtil;
    this.webClientConfig = webClientConfig;
  }

  /**
   * Apply the Authentication Filter.
   *
   * @param config The configuration for the filter.
   * @return The GatewayFilter.
   */
  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      var request = exchange.getRequest();

      if (validator.isSecured.test(request)) {
        HttpHeaders headers = request.getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        validateAuthorizationHeader(authHeader);

        String jwtToken = authHeader.substring(7);
        validateToken(jwtToken);

        return handleValidToken(jwtToken, exchange, config, chain);
      }

      return chain.filter(exchange);
    };
  }

  @Data
  public static class Config {
    private List<String> roles;
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return List.of("roles");
  }

  /**
   * Validate if the JWT token is valid.
   *
   * @param jwt The JWT token.
   * @return A Mono indicating if the token is valid.
   */
  private Mono<Boolean> isTokenValid(String jwt) {
    return webClientConfig.webClient().get()
            .uri(uriBuilder -> uriBuilder
                    .path("/api/auth/validate-token")
                    .queryParam("jwt", jwt)
                    .build())
            .retrieve()
            .bodyToMono(Boolean.class);
  }

  /**
   * Validate the authorization header.
   *
   * @param authHeader The authorization header.
   */
  private void validateAuthorizationHeader(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new MissingAuthorizationHeaderException("Missing or invalid authorization header");
    }
  }

  /**
   * Validate the JWT token.
   *
   * @param jwtToken The JWT token.
   */
  private void validateToken(String jwtToken) {
    try {
      jwtUtil.validateToken(jwtToken);
    } catch (Exception e) {
      logger.error("Error validating token: {}", e.getMessage());
      throw new InvalidTokenException("Unauthorized access to application");
    }
  }

  /**
   * Handle a valid token.
   *
   * @param jwtToken The JWT token.
   * @param exchange The server web exchange.
   * @param config   The filter configuration.
   * @param chain    The gateway filter chain.
   * @return A Mono indicating the completion of handling the valid token.
   */
  private Mono<Void> handleValidToken(String jwtToken, ServerWebExchange exchange, Config config, GatewayFilterChain chain) {
    return isTokenValid(jwtToken)
            .flatMap(valid -> {
              if (Boolean.TRUE.equals(valid)) {
                if (!jwtUtil.hasAnyRole(exchange.getRequest(), config.getRoles())) {
                  return Mono.error(new InvalidRoleException("Insufficient role permissions"));
                }
                return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken).build()).build());
              } else {
                return Mono.error(new InvalidTokenException("Invalid or expired token"));
              }
            });
  }
}
