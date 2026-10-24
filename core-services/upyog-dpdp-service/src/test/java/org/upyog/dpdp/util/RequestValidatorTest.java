package org.upyog.dpdp.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RequestValidatorTest {

    private final RequestValidator validator = new RequestValidator();

    @Test
    void requiresUser() {
        assertThrows(CustomException.class, () -> validator.requireUser(new RequestInfo()));
    }

    @Test
    void citizenCannotAccessAnotherPrincipal() {
        User citizen = User.builder().uuid("u1").type("CITIZEN").tenantId("pg.citya").build();
        assertThrows(CustomException.class, () -> validator.resolvePrincipal(citizen, "u2"));
        assertEquals("u1", validator.resolvePrincipal(citizen, null));
    }

    @Test
    void citizenCannotUseStateTenantPrefixBypass() {
        User citizen = User.builder().uuid("u1").type("CITIZEN").tenantId("pg").build();
        assertThrows(CustomException.class, () -> validator.assertTenantAccess(citizen, "pg.citya"));
    }

    @Test
    void employeeAllowedViaRoleTenantList() {
        User employee = User.builder().uuid("e1").type("EMPLOYEE").tenantId("pg.state")
                .roles(List.of(Role.builder().tenantId("pg.citya").code("DPO").build()))
                .build();
        validator.assertTenantAccess(employee, "pg.citya");
    }

    @Test
    void exactTenantMatchAlwaysAllowed() {
        User user = User.builder().uuid("u1").type("CITIZEN").tenantId("pg.citya").build();
        validator.assertTenantAccess(user, "pg.citya");
    }
}
