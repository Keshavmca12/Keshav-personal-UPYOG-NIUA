package com.example.gateway.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * UPYOG MCP is exposed at {@link #PATH_PREFIX} and uses JSON-RPC bodies without {@code RequestInfo}.
 * Gateway auth and RBAC must use session headers and exchange attributes instead of body rewrites.
 */
public final class UpyogMcpGatewaySupport {

    public static final String PATH_PREFIX = "/upyog-mcp-server";
    public static final String GATEWAY_REQUEST_INFO_ATTR = "gateway.requestInfo";

    private UpyogMcpGatewaySupport() {
    }

    public static boolean isMcpPath(String path) {
        return path != null && path.startsWith(PATH_PREFIX);
    }

    public static String firstAuthToken(HttpHeaders headers) {
        if (headers == null) {
            return null;
        }
        String token = firstHeader(headers, "Auth-Token");
        if (ObjectUtils.isEmpty(token)) {
            token = firstHeader(headers, "auth-token");
        }
        return token;
    }

    private static String firstHeader(HttpHeaders headers, String name) {
        List<String> values = headers.get(name);
        if (values == null || values.isEmpty()) {
            return null;
        }
        String value = values.get(0);
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
