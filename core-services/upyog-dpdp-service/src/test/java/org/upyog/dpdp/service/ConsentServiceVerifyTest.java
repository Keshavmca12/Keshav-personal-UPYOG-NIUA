package org.upyog.dpdp.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.egov.common.contract.request.RequestInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.producer.Producer;
import org.upyog.dpdp.repository.ConsentRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.ConsentSearchCriteria;
import org.upyog.dpdp.web.models.ConsentSearchRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentServiceVerifyTest {

    @Mock
    private ConsentRepository repository;
    @Mock
    private PolicyService policyService;
    @Mock
    private PrivacyEventService privacyEventService;
    @Mock
    private Producer producer;
    @Mock
    private DpdpConfiguration configuration;

    private ConsentService consentService;

    @BeforeEach
    void setUp() {
        consentService = new ConsentService(repository, policyService, privacyEventService, new RequestValidator(),
                new ResponseInfoFactory(), producer, configuration, new SimpleMeterRegistry());
    }

    @Test
    void interServiceVerifyUsesPrincipalFromCriteria() {
        when(policyService.resolveProcessingBasis("pg.citya", "OPTIONAL_ALERTS", null))
                .thenReturn(DpdpConstants.BASIS_CONSENT);
        when(repository.search(any())).thenReturn(List.of());

        ConsentSearchRequest request = ConsentSearchRequest.builder()
                .requestInfo(RequestInfo.builder().authToken("service-token").build())
                .consentSearchCriteria(ConsentSearchCriteria.builder()
                        .tenantId("pg.citya")
                        .purposeCode("OPTIONAL_ALERTS")
                        .dataPrincipalUuid("principal-1")
                        .build())
                .build();

        var response = consentService.verifyInterService(request);
        assertTrue(response.getVerification().isRequired());
        assertFalse(response.getVerification().isAllowed());
    }

    @Test
    void verifyRoutesToInterServiceWhenAuthTokenPresent() {
        when(policyService.resolveProcessingBasis("pg.citya", "PROPERTY_TAX_SERVICE", null))
                .thenReturn(DpdpConstants.BASIS_STATUTORY_FUNCTION);

        ConsentSearchRequest request = ConsentSearchRequest.builder()
                .requestInfo(RequestInfo.builder().authToken("service-token").build())
                .consentSearchCriteria(ConsentSearchCriteria.builder()
                        .tenantId("pg.citya")
                        .purposeCode("PROPERTY_TAX_SERVICE")
                        .dataPrincipalUuid("principal-1")
                        .build())
                .build();

        var response = consentService.verify(request);
        assertFalse(response.getVerification().isRequired());
        assertTrue(response.getVerification().isAllowed());
    }
}
