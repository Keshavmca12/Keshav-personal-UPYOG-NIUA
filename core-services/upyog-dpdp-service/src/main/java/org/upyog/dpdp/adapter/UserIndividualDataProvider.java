package org.upyog.dpdp.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.util.PiiMasker;
import org.upyog.dpdp.web.models.PersonalDataView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserIndividualDataProvider implements DpdpDataProvider {

    private final RestTemplate restTemplate;
    private final DpdpConfiguration configuration;
    private final ObjectMapper objectMapper;

    public UserIndividualDataProvider(RestTemplate restTemplate, DpdpConfiguration configuration,
                                      ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
        this.objectMapper = objectMapper;
    }

    @Override
    public String applicationCode() {
        return "USER";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PersonalDataView> findPersonalData(DataPrincipalContext context) {
        Map<String, String> masked = new HashMap<>();
        masked.put("uuid", context.userUuid());
        try {
            Map<String, Object> body = Map.of(
                    "RequestInfo", Map.of("apiId", "upyog-dpdp-service"),
                    "userSearchCriteria", Map.of(
                            "tenantId", context.tenantId(),
                            "uuid", List.of(context.userUuid())
                    )
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> response = restTemplate.postForObject(
                    configuration.getUserHost() + configuration.getUserSearchEndpoint(),
                    new HttpEntity<>(body, headers), Map.class);
            if (response != null && response.get("user") instanceof List<?> users && !users.isEmpty()) {
                Map<String, Object> user = objectMapper.convertValue(users.get(0), Map.class);
                masked.put("name", mask(String.valueOf(user.getOrDefault("name", ""))));
                masked.put("mobileNumber", PiiMasker.maskMobile(String.valueOf(user.getOrDefault("mobileNumber", ""))));
                masked.put("emailId", mask(String.valueOf(user.getOrDefault("emailId", ""))));
            }
        } catch (Exception ignored) {
            masked.put("mobileNumber", PiiMasker.maskMobile("**********"));
        }
        return List.of(PersonalDataView.builder().applicationCode(applicationCode()).maskedFields(masked).build());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CorrectionResult correctPersonalData(DataPrincipalContext context, String field, String value) {
        try {
            Map<String, Object> user = Map.of(
                    "uuid", context.userUuid(),
                    "tenantId", context.tenantId(),
                    field, value
            );
            Map<String, Object> body = Map.of(
                    "RequestInfo", Map.of("apiId", "upyog-dpdp-service"),
                    "user", user
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.postForObject(configuration.getUserHost() + configuration.getUserUpdateEndpoint(),
                    new HttpEntity<>(body, headers), Map.class);
            return new CorrectionResult(true, "User record update submitted");
        } catch (Exception ex) {
            return new CorrectionResult(false,
                    "User/Individual correction must be completed in egov-user: " + ex.getMessage());
        }
    }

    @Override
    public DeletionResult deletePersonalData(DataPrincipalContext context) {
        return new DeletionResult(false, "SOFT_DELETE",
                "User record marked for fiduciary review; physical delete is never default");
    }

    private static String mask(String value) {
        if (value == null || value.length() < 2) {
            return "***";
        }
        return value.charAt(0) + "***" + value.charAt(value.length() - 1);
    }
}
