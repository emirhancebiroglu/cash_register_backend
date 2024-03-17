package com.bit.gatewayservice.filter;

import com.bit.gatewayservice.exceptions.invalidrole.InvalidRoleException;
import com.bit.gatewayservice.exceptions.invalidtoken.InvalidTokenException;
import com.bit.gatewayservice.exceptions.missingauthorizationheader.MissingAuthorizationHeaderException;
import com.bit.gatewayservice.util.JwtUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Component
@Data
public class AuthenticationFilter
    extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

  private final RouteValidator validator;
  private JwtUtil jwtUtil;
  private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

  public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
    super(Config.class);
    this.validator = validator;
    this.jwtUtil = jwtUtil;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return ((exchange, chain) -> {
      var request = exchange.getRequest();
      HttpHeaders headers = exchange.getRequest().getHeaders();

      if (validator.isSecured.test(exchange.getRequest())) {
        if (!exchange.getRequest().getHeaders().containsKey(
                HttpHeaders.AUTHORIZATION)) {
          throw new MissingAuthorizationHeaderException("Missing authorization header");
        }

        String authHeader = Objects.requireNonNull(headers.get(HttpHeaders.AUTHORIZATION)).get(0);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
          authHeader = authHeader.substring(7);
        }
        try {
          jwtUtil.validateToken(authHeader);

        } catch (Exception e) {
          logger.error("Error validating");
          throw new InvalidTokenException("un-authorized access to application");
        }

        if (!jwtUtil.hasAnyRole(request, config.getRoles())){
          throw new InvalidRoleException("You dont have necessary role");
        }
      }
      return chain.filter(exchange);
    });
  }
  @Data
  public static class Config {
    private List<String> roles;
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return List.of("roles");
  }
}
