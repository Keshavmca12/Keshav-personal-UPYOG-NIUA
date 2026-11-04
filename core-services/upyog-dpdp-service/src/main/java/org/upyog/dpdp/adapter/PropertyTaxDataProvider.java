package org.upyog.dpdp.adapter;

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
public class PropertyTaxDataProvider implements DpdpDataProvider {

    private final RestTemplate restTemplate;
    private final DpdpConfiguration configuration;

    public PropertyTaxDataProvider(RestTemplate restTemplate, DpdpConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    @Override
    public String applicationCode() {
        return "PT";
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<PersonalDataView> findPersonalData(DataPrincipalContext context) {
        Map<String, String> masked = new HashMap<>();
        masked.put("uuid", context.userUuid());
        try {
            Map<String, Object> body = Map.of(
                    "RequestInfo", Map.of("apiId", "upyog-dpdp-service"),
                    "searchCriteria", Map.of(
                            "tenantId", context.tenantId(),
                            "ownerIds", List.of(context.userUuid())
                    )
            );
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            Map<String, Object> response = restTemplate.postForObject(
                    configuration.getPropertyHost() + configuration.getPropertySearchEndpoint(),
                    new HttpEntity<>(body, headers), Map.class);
            if (response != null && response.get("Properties") instanceof List<?> properties && !properties.isEmpty()) {
                masked.put("propertyCount", String.valueOf(properties.size()));
                masked.put("status", "FOUND");
            } else {
                masked.put("status", "NOT_FOUND");
            }
        } catch (Exception ex) {
            masked.put("status", "UNAVAILABLE");
            masked.put("ownerHint", PiiMasker.maskMobile("**********"));
        }
        return List.of(PersonalDataView.builder().applicationCode(applicationCode()).maskedFields(masked).build());
    }

    @Override
    public CorrectionResult correctPersonalData(DataPrincipalContext context, String field, String value) {
        return new CorrectionResult(false, "Property tax corrections must be completed in property-services");
    }

    @Override
    public DeletionResult deletePersonalData(DataPrincipalContext context) {
        return new DeletionResult(false, "ANONYMISE",
                "Property records anonymised per retention policy; statutory records may be retained");
    }
}
