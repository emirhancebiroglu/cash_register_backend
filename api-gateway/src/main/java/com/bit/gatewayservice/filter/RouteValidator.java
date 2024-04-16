package com.bit.gatewayservice.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    /**
     * List of open API endpoints.
     */
    public static final List<String> openApiEndpoints = List.of(
            "/api/auth/login",
            "/api/auth/validate-token",
            "/api/auth/forgot-user-code",
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/eureka"
    );

    /**
     * Predicate to check if a request is secured.
     */
    Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

}
