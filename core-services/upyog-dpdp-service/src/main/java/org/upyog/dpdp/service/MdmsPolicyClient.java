package org.upyog.dpdp.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.web.models.PurposeDefinition;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MdmsPolicyClient {

    private final RestTemplate restTemplate;
    private final DpdpConfiguration configuration;
    private final ObjectMapper objectMapper;

    public MdmsPolicyClient(RestTemplate restTemplate, DpdpConfiguration configuration, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
        this.objectMapper = objectMapper;
    }

    public List<PurposeDefinition> getPurposes(String tenantId) {
        return readMasterList(tenantId, "Purpose", "mdms/DPDP/Purpose.json",
                new TypeReference<List<PurposeDefinition>>() {
                });
    }

    public List<Map<String, Object>> getRetentionPolicies(String tenantId) {
        return readMasterList(tenantId, "RetentionPolicy", "mdms/DPDP/RetentionPolicy.json",
                new TypeReference<List<Map<String, Object>>>() {
                });
    }

    public List<Map<String, Object>> getNoticeTemplates(String tenantId, String locale) {
        List<Map<String, Object>> templates = readMasterList(tenantId, "NoticeTemplate",
                "mdms/DPDP/NoticeTemplate.json", new TypeReference<List<Map<String, Object>>>() {
                });
        if (StringUtils.isBlank(locale)) {
            return templates;
        }
        return templates.stream()
                .filter(t -> locale.equalsIgnoreCase(String.valueOf(t.getOrDefault("locale", locale))))
                .toList();
    }

    public List<Map<String, Object>> getFiduciaryContact(String tenantId) {
        return readMasterList(tenantId, "FiduciaryContact", "mdms/DPDP/FiduciaryContact.json",
                new TypeReference<List<Map<String, Object>>>() {
                });
    }

    public List<Map<String, Object>> getDataInventory(String tenantId) {
        return readMasterList(tenantId, "DataInventory", "mdms/DPDP/DataInventory.json",
                new TypeReference<List<Map<String, Object>>>() {
                });
    }

    public List<Map<String, Object>> getNotificationConfig(String tenantId) {
        return readMasterList(tenantId, "NotificationConfig", "mdms/DPDP/NotificationConfig.json",
                new TypeReference<List<Map<String, Object>>>() {
                });
    }

    public List<Map<String, Object>> getDataProviderConfigs(String tenantId) {
        return readMasterList(tenantId, "DataProviderConfig", "mdms/DPDP/DataProviderConfig.json",
                new TypeReference<List<Map<String, Object>>>() {
                });
    }

    private <T> List<T> readMasterList(String tenantId, String masterName, String classpathFallback,
                                       TypeReference<List<T>> type) {
        try {
            List<T> fromMdms = fetchFromMdms(tenantId, masterName, type);
            if (fromMdms != null && !fromMdms.isEmpty()) {
                return fromMdms;
            }
        } catch (Exception ex) {
            log.warn("MDMS fetch failed for {} / {}: {}", tenantId, masterName, ex.getMessage());
        }
        return loadClasspath(classpathFallback, type);
    }

    private <T> List<T> fetchFromMdms(String tenantId, String masterName, TypeReference<List<T>> type) {
        Map<String, Object> body = new HashMap<>();
        body.put("RequestInfo", Map.of("apiId", "upyog-dpdp-service"));
        body.put("MdmsCriteria", Map.of(
                "tenantId", tenantId,
                "moduleDetails", List.of(Map.of(
                        "moduleName", "DPDP",
                        "masterDetails", List.of(Map.of("name", masterName))
                ))
        ));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(
                configuration.getMdmsHost() + configuration.getMdmsSearchEndpoint(),
                new HttpEntity<>(body, headers),
                Map.class);
        if (response == null) {
            return List.of();
        }
        Object mdmsRes = response.get("MdmsRes");
        if (!(mdmsRes instanceof Map<?, ?> mdmsMap)) {
            return List.of();
        }
        Object dpdp = mdmsMap.get("DPDP");
        if (!(dpdp instanceof Map<?, ?> dpdpMap)) {
            return List.of();
        }
        Object master = dpdpMap.get(masterName);
        if (master == null) {
            return List.of();
        }
        return objectMapper.convertValue(master, type);
    }

    private <T> List<T> loadClasspath(String path, TypeReference<List<T>> type) {
        try (InputStream in = new ClassPathResource(path).getInputStream()) {
            List<T> loaded = objectMapper.readValue(in, type);
            return loaded == null ? new ArrayList<>() : loaded;
        } catch (Exception ex) {
            log.warn("Classpath fallback missing for {}: {}", path, ex.getMessage());
            return new ArrayList<>();
        }
    }
}
