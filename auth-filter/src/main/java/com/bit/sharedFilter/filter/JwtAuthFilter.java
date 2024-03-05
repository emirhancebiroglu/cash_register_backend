package com.bit.sharedFilter.filter;

import com.bit.sharedFilter.client.JwtAuthServiceClient;
import com.bit.sharedFilter.dto.TokenValidationReq;
import com.bit.sharedFilter.dto.UserDetailsDTO;
import com.bit.sharedFilter.utils.UserDetailsAdapter;
import com.bit.sharedFilter.utils.UserDetailsConsumer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtAuthServiceClient jwtAuthServiceClient;
    private final UserDetailsConsumer userDetailsConsumer;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userCode;

        if(StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer ")){
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        TokenValidationReq tokenValidationReqToExtractUsername = new TokenValidationReq(jwt);

        userCode = jwtAuthServiceClient.extractUsername(tokenValidationReqToExtractUsername);

        if(StringUtils.isNotEmpty(userCode) && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetailsDTO userDetailsDTO = userDetailsConsumer.getCachedUserDetails();
            if (userDetailsDTO == null || !userDetailsDTO.getUserCode().equals(userCode)) {
                try {
                    userDetailsDTO = userDetailsConsumer.consumeUserDetails();
                    userDetailsConsumer.updateCachedUserDetails(userDetailsDTO);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            UserDetails userDetails = new UserDetailsAdapter(userDetailsDTO);

            TokenValidationReq tokenValidationReqToValidateToken = new TokenValidationReq(jwt, userDetails.getUsername());

            if(jwtAuthServiceClient.validateToken(tokenValidationReqToValidateToken)){
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
