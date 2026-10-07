package org.upyog.mcp.web;

/**
 * Request-scoped holder for the user validated by {@link AuthTokenFilter}.
 * Cleared at the end of the servlet request so a later call cannot reuse the identity.
 */
public final class UserContextHolder {

    private static final ThreadLocal<AuthenticatedUser> CURRENT = new ThreadLocal<>();

    private UserContextHolder() {
    }

    public static void set(AuthenticatedUser user) {
        CURRENT.set(user);
    }

    public static AuthenticatedUser get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
