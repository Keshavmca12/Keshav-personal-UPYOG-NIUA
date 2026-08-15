package org.upyog.dpdp.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Test;

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
    void allowsStateTenantForCity() {
        User employee = User.builder().uuid("e1").type("EMPLOYEE").tenantId("pg").build();
        validator.assertTenantAccess(employee, "pg.citya");
    }
}
