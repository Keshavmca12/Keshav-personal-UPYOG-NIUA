package org.upyog.dpdp.sdk.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.sdk.context.DpdpRequestContext;

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
        body.put("RequestInfo", enrichRequestInfo(requestInfo, tenantId, principalUuid));
        body.put("ConsentSearchCriteria", Map.of(
                "tenantId", tenantId,
                "purposeCode", purposeCode,
                "dataPrincipalUuid", principalUuid == null ? "" : principalUuid
        ));
        return postVerification("/consent/v1/_verify", body);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> verifyConsentInterService(DpdpRequestContext context, String purposeCode) {
        Map<String, Object> body = new HashMap<>();
        body.put("RequestInfo", context.toRequestInfoMap());
        body.put("ConsentSearchCriteria", Map.of(
                "tenantId", context.getTenantId(),
                "purposeCode", purposeCode,
                "dataPrincipalUuid", context.getUserUuid() == null ? "" : context.getUserUuid()
        ));
        return postVerification("/consent/v1/_verifyInterService", body);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postVerification(String path, Map<String, Object> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        try {
            Map<String, Object> response = restTemplate.postForObject(
                    coreUrl + path, new HttpEntity<>(body, headers), Map.class);
            if (response != null && response.get("verification") instanceof Map<?, ?> verification) {
                return (Map<String, Object>) verification;
            }
        } catch (Exception ex) {
            log.warn("DPDP consent verify failed within {} ms: {}", timeoutMs, ex.getMessage());
            throw ex;
        }
        return Map.of("required", false, "allowed", true, "status", "UNKNOWN");
    }

    private Map<String, Object> enrichRequestInfo(Map<String, Object> requestInfo, String tenantId,
                                                  String principalUuid) {
        Map<String, Object> info = requestInfo == null ? new HashMap<>() : new HashMap<>(requestInfo);
        if (!info.containsKey("apiId")) {
            info.put("apiId", "dpdp-sdk");
        }
        Object existingUser = info.get("userInfo");
        if (existingUser instanceof Map<?, ?> userMap && userMap.get("uuid") != null) {
            return info;
        }
        if (principalUuid != null && !principalUuid.isBlank()) {
            Map<String, Object> user = new HashMap<>();
            user.put("uuid", principalUuid);
            user.put("tenantId", tenantId);
            user.put("type", "CITIZEN");
            info.put("userInfo", user);
        }
        return info;
    }
}
