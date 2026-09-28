package org.upyog.dpdp.util;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

@Component
public class RequestValidator {

    public User requireUser(RequestInfo requestInfo) {
        if (requestInfo == null || requestInfo.getUserInfo() == null
                || StringUtils.isBlank(requestInfo.getUserInfo().getUuid())) {
            throw new CustomException(DpdpConstants.ERR_USER_REQUIRED, "RequestInfo.userInfo is required");
        }
        return requestInfo.getUserInfo();
    }

    public void assertTenantAccess(User user, String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            throw new CustomException(DpdpConstants.ERR_TENANT_REQUIRED, "tenantId is required");
        }
        if (user.getTenantId() == null) {
            return;
        }
        boolean allowed = tenantId.equals(user.getTenantId())
                || tenantId.startsWith(user.getTenantId() + ".")
                || user.getTenantId().startsWith(tenantId + ".")
                || tenantId.startsWith(user.getTenantId().split("\\.")[0]);
        if (!allowed && !isEmployee(user)) {
            throw new CustomException(DpdpConstants.ERR_CROSS_TENANT, "Cross-tenant access is not allowed");
        }
    }

    public String resolvePrincipal(User user, String requestedUuid) {
        if (isCitizen(user)) {
            if (StringUtils.isNotBlank(requestedUuid) && !requestedUuid.equals(user.getUuid())) {
                throw new CustomException(DpdpConstants.ERR_CROSS_USER, "Citizen can only access own records");
            }
            return user.getUuid();
        }
        return StringUtils.isBlank(requestedUuid) ? user.getUuid() : requestedUuid;
    }

    public boolean isCitizen(User user) {
        return user != null && DpdpConstants.USER_CITIZEN.equalsIgnoreCase(user.getType());
    }

    public boolean isEmployee(User user) {
        return user != null && !isCitizen(user);
    }
}
