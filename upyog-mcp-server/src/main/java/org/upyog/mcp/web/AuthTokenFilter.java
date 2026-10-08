package org.upyog.mcp.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.upyog.mcp.client.GatewayClient;
import org.upyog.mcp.client.RequestInfoBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Validates the {@code auth-token} header on {@code /mcp} by calling egov-user.
 * The cache key is a hash of the token. The token itself is not logged.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuthTokenFilter extends OncePerRequestFilter {

    /** HTTP header name for the UPYOG session access token. */
    public static final String HEADER = "auth-token";

    private final GatewayClient gatewayClient;
    private final ObjectMapper objectMapper;
    private final Cache<String, AuthenticatedUser> users = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .maximumSize(10_000)
            .build();

    /**
     * @param gatewayClient client used to call {@code POST /user/_details}
     * @param objectMapper  JSON serializer for error responses
     */
    public AuthTokenFilter(GatewayClient gatewayClient, ObjectMapper objectMapper) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
    }

    /** Applies only to {@code /mcp} routes; health and actuator paths skip token validation. */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null || !path.startsWith("/mcp");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = request.getHeader(HEADER);
        if (token == null || token.isBlank()) {
            write(response, 401, "AUTHENTICATION_REQUIRED", "An auth-token header is required.");
            return;
        }
        try {
            AuthenticatedUser user = users.get(hash(token), key -> load(token));
            UserContextHolder.set(user);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            write(response, 401, "AUTHENTICATION_FAILED", "The auth token could not be validated.");
        } finally {
            UserContextHolder.clear();
        }
    }

    private AuthenticatedUser load(String token) {
        JsonNode user = gatewayClient.postJson("/user/_details",
                java.util.Map.of("access_token", List.of(token)),
                objectMapper.createObjectNode(), true);
        String uuid = user.path("uuid").asText("");
        if (uuid.isBlank()) {
            throw new IllegalStateException("user uuid missing");
        }
        List<String> roles = new ArrayList<>();
        List<String> roleTenants = new ArrayList<>();
        for (JsonNode role : user.path("roles")) {
            roles.add(role.path("code").asText(""));
            roleTenants.add(role.path("tenantId").asText(""));
        }
        return new AuthenticatedUser(uuid, user.path("type").asText(""), user.path("tenantId").asText(""),
                roles, roleTenants, token);
    }

    private void write(HttpServletResponse response, int status, String code, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(java.util.Map.of(
                "code", code,
                "message", message,
                "retryable", false,
                "suggestedNextStep", "Sign in to UPYOG and pass the session auth-token header.",
                "correlationId", RequestInfoBuilder.correlationId()
        )));
    }

    private static String hash(String token) {
        try {
            return Base64.getUrlEncoder().withoutPadding().encodeToString(
                    MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
