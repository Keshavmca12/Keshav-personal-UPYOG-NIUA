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
import org.upyog.mcp.config.McpProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Resolves the caller on {@code /mcp}.
 * <p>
 * Production path: chatbot → API gateway (auth + RBAC) → MCP, with {@code x-user-info} and {@code Auth-Token}.
 * Developer path: chatbot → MCP directly with {@code auth-token}; MCP calls {@code /user/_details}.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class AuthTokenFilter extends OncePerRequestFilter {

    /** HTTP header name for the UPYOG session access token on direct MCP calls. */
    public static final String HEADER = "auth-token";

    private final GatewayClient gatewayClient;
    private final ObjectMapper objectMapper;
    private final McpProperties properties;
    private final Cache<String, AuthenticatedUser> users = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(1))
            .maximumSize(10_000)
            .build();

    /**
     * @param gatewayClient client used to call {@code POST /user/_details}
     * @param objectMapper  JSON serializer for error responses
     */
    public AuthTokenFilter(GatewayClient gatewayClient, ObjectMapper objectMapper, McpProperties properties) {
        this.gatewayClient = gatewayClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
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
        try {
            AuthenticatedUser user = resolveUser(request);
            if (user == null) {
                write(response, 401, "AUTHENTICATION_REQUIRED",
                        "Call MCP through the API gateway with Auth-Token, or pass auth-token for local direct access.");
                return;
            }
            UserContextHolder.set(user);
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            write(response, 401, "AUTHENTICATION_FAILED", "The auth token could not be validated.");
        } finally {
            UserContextHolder.clear();
        }
    }

    private AuthenticatedUser resolveUser(HttpServletRequest request) {
        if (properties.isTrustGatewayIdentity()) {
            AuthenticatedUser fromGateway = GatewayIdentityParser.fromGatewayHeaders(request, objectMapper);
            if (fromGateway != null) {
                return fromGateway;
            }
        }
        String token = GatewayIdentityParser.firstToken(request);
        if (token == null || token.isBlank()) {
            return null;
        }
        return users.get(hash(token), key -> load(token));
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
