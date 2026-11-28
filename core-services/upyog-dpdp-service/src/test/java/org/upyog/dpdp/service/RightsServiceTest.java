package org.upyog.dpdp.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dpdp.adapter.DpdpDataProvider;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.repository.RightsRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.PrincipalRequest;
import org.upyog.dpdp.web.models.RightsRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RightsServiceTest {

    @Mock
    private RightsRepository repository;
    @Mock
    private PrivacyEventService privacyEventService;
    @Mock
    private DpdpConfiguration configuration;
    @Mock
    private GrievanceService grievanceService;
    @Mock
    private AccessLogService accessLogService;
    @Mock
    private WorkflowIntegrationService workflowIntegrationService;
    @Mock
    private DpdpDataProvider dataProvider;

    private RightsService rightsService;

    @BeforeEach
    void setUp() {
        rightsService = new RightsService(repository, List.of(dataProvider), new RequestValidator(),
                new ResponseInfoFactory(), privacyEventService, configuration, grievanceService, accessLogService,
                workflowIntegrationService);
    }

    @Test
    void createAccessRequestIsPendingWithSla() {
        User citizen = User.builder().uuid("u1").type("CITIZEN").tenantId("pg.citya").build();
        RightsRequest request = RightsRequest.builder()
                .requestInfo(RequestInfo.builder().userInfo(citizen).build())
                .principalRequest(PrincipalRequest.builder()
                        .tenantId("pg.citya")
                        .requestType(DpdpConstants.RIGHTS_ACCESS)
                        .applicationCode("USER")
                        .build())
                .build();
        rightsService.create(request);

        ArgumentCaptor<PrincipalRequest> captor = ArgumentCaptor.forClass(PrincipalRequest.class);
        verify(repository).insert(captor.capture());
        assertEquals(DpdpConstants.STATUS_PENDING, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getSlaDueAt());
    }
}
