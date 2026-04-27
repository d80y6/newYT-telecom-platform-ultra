package com.yemenptc.bss.coreservice.security;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RateLimitingFilter implements Filter {

    private final Map<String, AtomicInteger> clientCounters = new ConcurrentHashMap<>();
    private final Map<String, RateLimiter> clientRateLimiters = new ConcurrentHashMap<>();
    private final Map<String, RateLimiter> endpointRateLimiters = new ConcurrentHashMap<>();
    
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    private static final int BURST_CAPACITY = 20;
    
    private final RateLimiterConfig clientConfig;
    private final RateLimiterConfig endpointConfig;
    
    public RateLimitingFilter() {
        this.clientConfig = RateLimiterConfig.custom()
                .limitForPeriod(DEFAULT_REQUESTS_PER_MINUTE)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();
        
        this.endpointConfig = RateLimiterConfig.custom()
                .limitForPeriod(1000)
                .limitRefreshPeriod(Duration.ofMinutes(1))
                .timeoutDuration(Duration.ZERO)
                .build();
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientId = extractClientId(httpRequest);
        String endpoint = extractEndpoint(httpRequest);
        
        RateLimiter clientLimiter = clientRateLimiters.computeIfAbsent(clientId, 
                k -> RateLimiter.of("client-" + k, clientConfig));
        RateLimiter endpointLimiter = endpointRateLimiters.computeIfAbsent(endpoint, 
                k -> RateLimiter.of("endpoint-" + k, endpointConfig));
        
        if (!clientLimiter.acquirePermission()) {
            log.warn("Rate limit exceeded for client: {}", clientId);
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Try again later.\"}");
            return;
        }
        
        if (!endpointLimiter.acquirePermission()) {
            log.warn("Endpoint rate limit exceeded: {}", endpoint);
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Endpoint rate limit exceeded.\"}");
            return;
        }
        
        httpResponse.setHeader("X-RateLimit-Remaining", 
                String.valueOf(clientLimiter.getMetrics().getAvailablePermissions()));
        httpResponse.setHeader("X-RateLimit-Limit", 
                String.valueOf(DEFAULT_REQUESTS_PER_MINUTE));
        
        chain.doFilter(request, response);
    }
    
    private String extractClientId(HttpServletRequest request) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return "api-key:" + apiKey;
        }
        
        String clientId = request.getHeader("X-Client-Id");
        if (clientId != null && !clientId.isEmpty()) {
            return "client:" + clientId;
        }
        
        return "ip:" + request.getRemoteAddr();
    }
    
    private String extractEndpoint(HttpServletRequest request) {
        return request.getMethod() + ":" + request.getRequestURI();
    }
    
    public void clearRateLimiters() {
        clientRateLimiters.clear();
        endpointRateLimiters.clear();
    }
    
    public int getRemainingTokens(String clientId) {
        RateLimiter limiter = clientRateLimiters.get(clientId);
        return limiter != null ? limiter.getMetrics().getAvailablePermissions() : DEFAULT_REQUESTS_PER_MINUTE;
    }
}
