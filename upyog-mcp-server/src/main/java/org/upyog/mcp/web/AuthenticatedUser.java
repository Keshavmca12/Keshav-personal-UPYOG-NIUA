package org.upyog.mcp.web;

import java.util.List;

/**
 * Caller identity loaded from egov-user {@code POST /user/_details}.
 * <p>
 * The access token is kept only so the server can place it in {@code RequestInfo.authToken}.
 * {@link #toString()} omits the token, mobile number, and name.
 */
public final class AuthenticatedUser {

    private final String uuid;
    private final String type;
    private final String tenantId;
    private final List<String> roleCodes;
    private final List<String> roleTenants;
    private final String authToken;

    public AuthenticatedUser(String uuid, String type, String tenantId, List<String> roleCodes,
                             List<String> roleTenants, String authToken) {
        this.uuid = uuid;
        this.type = type;
        this.tenantId = tenantId;
        this.roleCodes = List.copyOf(roleCodes);
        this.roleTenants = List.copyOf(roleTenants);
        this.authToken = authToken;
    }

    public String getUuid() {
        return uuid;
    }

    public String getType() {
        return type;
    }

    public String getTenantId() {
        return tenantId;
    }

    public List<String> getRoleCodes() {
        return roleCodes;
    }

    public List<String> getRoleTenants() {
        return roleTenants;
    }

    public String getAuthToken() {
        return authToken;
    }

    public boolean hasRole(String code) {
        return roleCodes.stream().anyMatch(role -> role.equalsIgnoreCase(code));
    }

    @Override
    public String toString() {
        return "AuthenticatedUser{uuid=" + uuid + ", type=" + type + "}";
    }
}
