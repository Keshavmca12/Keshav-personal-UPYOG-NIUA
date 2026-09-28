package org.upyog.dpdp.sdk.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.upyog.dpdp.sdk.annotation.Purpose;
import org.upyog.dpdp.sdk.annotation.RequiresConsent;
import org.upyog.dpdp.sdk.client.DpdpCoreClient;

import java.util.Map;

public class DpdpHandlerInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(DpdpHandlerInterceptor.class);

    public enum FailMode { FAIL_OPEN, FAIL_CLOSED }

    private final boolean enabled;
    private final boolean enforceConsent;
    private final boolean enforcePurpose;
    private final FailMode failMode;
    private final String tenantId;
    private final DpdpCoreClient client;

    public DpdpHandlerInterceptor(boolean enabled, boolean enforceConsent, boolean enforcePurpose,
                                  FailMode failMode, String tenantId, DpdpCoreClient client) {
        this.enabled = enabled;
        this.enforceConsent = enforceConsent;
        this.enforcePurpose = enforcePurpose;
        this.failMode = failMode;
        this.tenantId = tenantId;
        this.client = client;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!enabled || !(handler instanceof HandlerMethod method)) {
            return true;
        }
        Purpose purposeAnn = method.getMethodAnnotation(Purpose.class);
        if (purposeAnn == null) {
            purposeAnn = method.getBeanType().getAnnotation(Purpose.class);
        }
        RequiresConsent requiresConsent = method.getMethodAnnotation(RequiresConsent.class);
        String purpose = purposeAnn != null ? purposeAnn.value()
                : (requiresConsent != null ? requiresConsent.purpose() : request.getHeader("X-DPDP-Purpose"));
        if (enforcePurpose && purpose == null) {
            log.debug("DPDP purpose not declared for {}", method.getMethod().getName());
        }
        if (!enforceConsent || purpose == null || purpose.isBlank()) {
            return true;
        }
        try {
            Map<String, Object> verification = client.verifyConsent(
                    Map.of("apiId", "dpdp-sdk"), tenantId, purpose, request.getHeader("x-user-uuid"));
            boolean allowed = Boolean.TRUE.equals(verification.get("allowed"));
            boolean required = Boolean.TRUE.equals(verification.get("required"));
            if (required && !allowed) {
                response.sendError(403, "DPDP consent required for purpose " + purpose);
                return false;
            }
            return true;
        } catch (Exception ex) {
            if (failMode == FailMode.FAIL_CLOSED) {
                log.warn("DPDP core unavailable; failing closed");
                try {
                    response.sendError(503, "DPDP compliance service unavailable");
                } catch (Exception ignored) {
                    return false;
                }
                return false;
            }
            log.warn("DPDP core unavailable; failing open for statutory/configured operation");
            return true;
        }
    }
}
