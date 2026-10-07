package org.upyog.mcp.guard;

import org.springframework.stereotype.Component;
import org.upyog.mcp.web.AuthenticatedUser;

/**
 * Checks a requested {@code tenantId} against the signed-in user.
 * A tenant is allowed when it equals the user or role tenant, or is a child of that tenant
 * ({@code pg} covers {@code pg.citya}). {@code SUPERUSER} is not limited here.
 * The gateway remains the authority if this check is too wide.
 */
@Component
public class TenantValidator {

    public void requireAllowed(AuthenticatedUser user, String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId is required");
        }
        if (user.hasRole("SUPERUSER")) {
            return;
        }
        if (covers(user.getTenantId(), tenantId)) {
            return;
        }
        for (String roleTenant : user.getRoleTenants()) {
            if (covers(roleTenant, tenantId)) {
                return;
            }
        }
        throw new IllegalArgumentException("tenant is outside the signed-in user context");
    }

    static boolean covers(String allowed, String requested) {
        if (allowed == null || allowed.isBlank()) {
            return false;
        }
        return requested.equals(allowed) || requested.startsWith(allowed + ".");
    }
}
