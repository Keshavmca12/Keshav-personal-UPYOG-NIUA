package org.upyog.dpdp.util;

import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TenantIsolationTest {

    private final RequestValidator validator = new RequestValidator();

    @Test
    void rejectsCrossCityCitizenAccess() {
        User citizen = User.builder().uuid("c1").type("CITIZEN").tenantId("pg.citya").build();
        assertThrows(CustomException.class, () -> validator.assertTenantAccess(citizen, "pg.cityb"));
    }

    @Test
    void allowsEmployeeWithMatchingRoleTenant() {
        User dpo = User.builder().uuid("e1").type("EMPLOYEE").tenantId("pg")
                .roles(List.of(Role.builder().tenantId("pg.cityb").code("DPO").build()))
                .build();
        assertDoesNotThrow(() -> validator.assertTenantAccess(dpo, "pg.cityb"));
    }
}
