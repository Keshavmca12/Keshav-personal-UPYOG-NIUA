package org.upyog.dpdp.sdk.context;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

public final class DpdpRequestContext {

    public static final String HEADER_TENANT = "x-tenant-id";
    public static final String HEADER_USER_UUID = "x-user-uuid";
    public static final String HEADER_USER_TYPE = "x-user-type";

    private final String tenantId;
    private final String userUuid;
    private final String userType;
    private final String authToken;

    public DpdpRequestContext(String tenantId, String userUuid, String userType, String authToken) {
        this.tenantId = tenantId;
        this.userUuid = userUuid;
        this.userType = userType;
        this.authToken = authToken;
    }

    public static DpdpRequestContext from(HttpServletRequest request, String defaultTenantId) {
        String tenant = firstNonBlank(request.getHeader(HEADER_TENANT), defaultTenantId);
        return new DpdpRequestContext(
                tenant,
                request.getHeader(HEADER_USER_UUID),
                request.getHeader(HEADER_USER_TYPE),
                request.getHeader("authToken")
        );
    }

    public Map<String, Object> toRequestInfoMap() {
        Map<String, Object> requestInfo = new HashMap<>();
        requestInfo.put("apiId", "dpdp-sdk");
        if (isNotBlank(authToken)) {
            requestInfo.put("authToken", authToken);
        }
        if (isNotBlank(userUuid)) {
            Map<String, Object> user = new HashMap<>();
            user.put("uuid", userUuid);
            if (isNotBlank(userType)) {
                user.put("type", userType);
            }
            if (isNotBlank(tenantId)) {
                user.put("tenantId", tenantId);
            }
            requestInfo.put("userInfo", user);
        }
        return requestInfo;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public String getUserType() {
        return userType;
    }

    public String getAuthToken() {
        return authToken;
    }

    private static String firstNonBlank(String primary, String fallback) {
        if (isNotBlank(primary)) {
            return primary;
        }
        return fallback;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }
}
