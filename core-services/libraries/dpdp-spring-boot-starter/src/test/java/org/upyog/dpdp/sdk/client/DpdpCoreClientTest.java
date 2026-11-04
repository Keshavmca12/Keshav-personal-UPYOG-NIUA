package org.upyog.dpdp.sdk.client;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.sdk.context.DpdpRequestContext;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DpdpCoreClientTest {

    @Test
    void verifyInterServiceCallsDedicatedEndpoint() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("http://core/upyog-dpdp-service/consent/v1/_verifyInterService"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"verification":{"required":true,"allowed":true,"status":"GRANTED"}}
                        """, MediaType.APPLICATION_JSON));

        DpdpCoreClient client = new DpdpCoreClient(restTemplate, "http://core/upyog-dpdp-service", 800);
        DpdpRequestContext context = new DpdpRequestContext("pg.citya", "u1", "CITIZEN", "token");
        Map<String, Object> verification = client.verifyConsentInterService(context, "OPTIONAL_ALERTS");
        assertTrue((Boolean) verification.get("allowed"));
        assertEquals("GRANTED", verification.get("status"));
        server.verify();
    }

    @Test
    void verifyConsentBuildsUserInRequestInfo() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("http://core/upyog-dpdp-service/consent/v1/_verify"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("""
                        {"verification":{"required":false,"allowed":true,"status":"NOT_REQUIRED"}}
                        """, MediaType.APPLICATION_JSON));

        DpdpCoreClient client = new DpdpCoreClient(restTemplate, "http://core/upyog-dpdp-service", 800);
        Map<String, Object> verification = client.verifyConsent(Map.of("apiId", "sdk"), "pg.citya",
                "PROPERTY_TAX_SERVICE", "u1");
        assertTrue((Boolean) verification.get("allowed"));
        server.verify();
    }
}
