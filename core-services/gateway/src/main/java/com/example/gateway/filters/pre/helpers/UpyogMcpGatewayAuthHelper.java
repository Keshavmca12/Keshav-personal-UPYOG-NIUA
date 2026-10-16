package com.example.gateway.filters.pre.helpers;

import com.example.gateway.utils.UpyogMcpGatewaySupport;
import com.example.gateway.utils.UserUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Authenticates Streamable HTTP MCP calls without injecting {@code RequestInfo} into the JSON-RPC body.
 */
@Slf4j
@Component
public class UpyogMcpGatewayAuthHelper {

    private static final String USER_INFO_HEADER = "x-user-info";
    private static final String PASS_THROUGH_HEADER = "x-pass-through-gateway";
    private static final String PASS_THROUGH_VALUE = "true";

    private final UserUtils userUtils;
    private final ObjectMapper objectMapper;

    public UpyogMcpGatewayAuthHelper(UserUtils userUtils, ObjectMapper objectMapper) {
        this.userUtils = userUtils;
        this.objectMapper = objectMapper;
    }

    public Mono<Void> authenticateAndForward(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authToken = UpyogMcpGatewaySupport.firstAuthToken(exchange.getRequest().getHeaders());
        if (ObjectUtils.isEmpty(authToken)) {
            CustomException customException = new CustomException(
                    "You are not authorized to access this resource",
                    "You are not authorized to access this resource");
            customException.setCode(HttpStatus.UNAUTHORIZED.toString());
            throw customException;
        }
        try {
            User user = userUtils.getUser(authToken);
            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setAuthToken(authToken);
            requestInfo.setUserInfo(user);
            exchange.getAttributes().put(UpyogMcpGatewaySupport.GATEWAY_REQUEST_INFO_ATTR, requestInfo);

            String userJson = objectMapper.writeValueAsString(user);
            ServerWebExchange forwarded = exchange.mutate().request(builder -> builder.headers(headers -> {
                headers.set(USER_INFO_HEADER, userJson);
                headers.set(PASS_THROUGH_HEADER, PASS_THROUGH_VALUE);
                if (!headers.containsKey("Auth-Token")) {
                    headers.set("Auth-Token", authToken);
                }
            })).build();

            log.info("UPYOG MCP: authenticated user {} for {}", user.getUuid(), exchange.getRequest().getPath());
            return chain.filter(forwarded);
        } catch (CustomException exception) {
            throw exception;
        } catch (Exception exception) {
            log.error("UPYOG MCP authentication failed", exception);
            CustomException customException = new CustomException("AUTHENTICATION_ERROR", exception.getMessage());
            customException.setCode(HttpStatus.UNAUTHORIZED.toString());
            throw customException;
        }
    }
}
