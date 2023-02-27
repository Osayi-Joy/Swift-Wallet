package com.zurum.lanefinance.filters;

import com.zurum.lanefinance.constants.SecurityConstants;
import com.zurum.lanefinance.exceptions.CustomException;
import com.zurum.lanefinance.utils.SecurityUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
public class CustomAuthenticationFilter extends OncePerRequestFilter {


    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, CustomException {
        if (request.getServletPath().equals("/auth/signup") ) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
                try {
                    String token = authorizationHeader.substring(SecurityConstants.TOKEN_PREFIX.length());
                    UsernamePasswordAuthenticationToken authenticationToken = SecurityUtil.verifyToken(token);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    log.error("Error occurred {}", exception.getMessage());
                    response.setHeader("error", exception.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    throw new CustomException(exception.getMessage());
                }

            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
