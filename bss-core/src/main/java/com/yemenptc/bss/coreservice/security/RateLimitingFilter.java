package com.yemenptc.bss.coreservice.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> clientBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> endpointBuckets = new ConcurrentHashMap<>();
    
    private static final int DEFAULT_REQUESTS_PER_MINUTE = 100;
    private static final int BURST_CAPACITY = 20;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String clientId = extractClientId(httpRequest);
        String endpoint = extractEndpoint(httpRequest);
        
        Bucket clientBucket = clientBuckets.computeIfAbsent(clientId, 
                k -> createClientBucket());
        Bucket endpointBucket = endpointBuckets.computeIfAbsent(endpoint, 
                k -> createEndpointBucket());
        
        if (!clientBucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for client: {}", clientId);
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Try again later.\"}");
            return;
        }
        
        if (!endpointBucket.tryConsume(1)) {
            log.warn("Endpoint rate limit exceeded: {}", endpoint);
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"error\":\"Too Many Requests\",\"message\":\"Endpoint rate limit exceeded.\"}");
            return;
        }
        
        httpResponse.setHeader("X-RateLimit-Remaining", 
                String.valueOf(clientBucket.getAvailableTokens()));
        httpResponse.setHeader("X-RateLimit-Limit", 
                String.valueOf(DEFAULT_REQUESTS_PER_MINUTE));
        
        chain.doFilter(request, response);
    }
    
    private Bucket createClientBucket() {
        Bandwidth limit = Bandwidth.classic(
                DEFAULT_REQUESTS_PER_MINUTE,
                Refill.greedy(DEFAULT_REQUESTS_PER_MINUTE, Duration.ofMinutes(1))
        );
        
        Bandwidth burst = Bandwidth.classic(
                BURST_CAPACITY,
                Refill.intervally(BURST_CAPACITY, Duration.ofSeconds(10))
        );
        
        return Bucket.builder()
                .addLimit(limit)
                .addLimit(burst)
                .build();
    }
    
    private Bucket createEndpointBucket() {
        Bandwidth limit = Bandwidth.classic(
                1000,
                Refill.greedy(1000, Duration.ofMinutes(1))
        );
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
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
    
    public void clearBuckets() {
        clientBuckets.clear();
        endpointBuckets.clear();
    }
    
    public long getRemainingTokens(String clientId) {
        Bucket bucket = clientBuckets.get(clientId);
        return bucket != null ? bucket.getAvailableTokens() : DEFAULT_REQUESTS_PER_MINUTE;
    }
}
