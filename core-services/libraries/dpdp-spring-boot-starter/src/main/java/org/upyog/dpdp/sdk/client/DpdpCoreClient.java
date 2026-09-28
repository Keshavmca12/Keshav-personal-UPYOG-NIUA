package org.upyog.dpdp.sdk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class DpdpCoreClient {

    private static final Logger log = LoggerFactory.getLogger(DpdpCoreClient.class);

    private final RestTemplate restTemplate;
    private final String coreUrl;
    private final int timeoutMs;

    public DpdpCoreClient(RestTemplate restTemplate, String coreUrl, int timeoutMs) {
        this.restTemplate = restTemplate;
        this.coreUrl = coreUrl;
        this.timeoutMs = timeoutMs;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> verifyConsent(Map<String, Object> requestInfo, String tenantId,
                                             String purposeCode, String principalUuid) {
        Map<String, Object> body = new HashMap<>();
        body.put("RequestInfo", requestInfo);
        body.put("ConsentSearchCriteria", Map.of(
                "tenantId", tenantId,
                "purposeCode", purposeCode,
                "dataPrincipalUuid", principalUuid == null ? "" : principalUuid
        ));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Map<String, Object> response = restTemplate.postForObject(
                    coreUrl + "/consent/v1/_verify", new HttpEntity<>(body, headers), Map.class);
            if (response != null && response.get("verification") instanceof Map<?, ?> verification) {
                return (Map<String, Object>) verification;
            }
        } catch (Exception ex) {
            log.warn("DPDP consent verify failed within {} ms: {}", timeoutMs, ex.getMessage());
            throw ex;
        }
        return Map.of("required", false, "allowed", true, "status", "UNKNOWN");
    }
}
