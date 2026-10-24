package org.upyog.dpdp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.web.models.PurposeDefinition;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MdmsPolicyClientTest {

    @Test
    void fallsBackToClasspathWhenMdmsFails() {
        RestTemplate restTemplate = mock(RestTemplate.class);
        when(restTemplate.postForObject(anyString(), any(), any())).thenThrow(new RuntimeException("down"));
        DpdpConfiguration configuration = mock(DpdpConfiguration.class);
        when(configuration.getMdmsHost()).thenReturn("http://localhost");
        when(configuration.getMdmsSearchEndpoint()).thenReturn("/mdms/_search");

        MdmsPolicyClient client = new MdmsPolicyClient(restTemplate, configuration, new ObjectMapper());
        List<PurposeDefinition> purposes = client.getPurposes("pg");
        assertFalse(purposes.isEmpty());
    }
}
