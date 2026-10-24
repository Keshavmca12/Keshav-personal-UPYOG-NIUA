package org.upyog.dpdp.util;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
        if (user == null || user.getTenantId() == null) {
            return;
        }
        if (tenantId.equals(user.getTenantId())) {
            return;
        }
        if (isEmployee(user) && employeeTenants(user).contains(tenantId)) {
            return;
        }
        if (!isEmployee(user)) {
            throw new CustomException(DpdpConstants.ERR_CROSS_TENANT, "Cross-tenant access is not allowed");
        }
        throw new CustomException(DpdpConstants.ERR_CROSS_TENANT, "Cross-tenant access is not allowed");
    }

    private Set<String> employeeTenants(User user) {
        Set<String> tenants = new HashSet<>();
        if (StringUtils.isNotBlank(user.getTenantId())) {
            tenants.add(user.getTenantId());
        }
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role != null && StringUtils.isNotBlank(role.getTenantId())) {
                    tenants.add(role.getTenantId());
                }
            }
        }
        return tenants;
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
