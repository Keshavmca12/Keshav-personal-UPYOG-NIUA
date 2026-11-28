package org.upyog.dpdp.adapter;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.web.models.PersonalDataView;

import java.util.List;
import java.util.Map;

@Component
public class GenericHttpDataProvider implements DpdpDataProvider {

    private final RestTemplate restTemplate;

    public GenericHttpDataProvider(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String applicationCode() {
        return "GENERIC";
    }

    @Override
    public List<PersonalDataView> findPersonalData(DataPrincipalContext context) {
        return List.of(PersonalDataView.builder()
                .applicationCode(context.applicationCode())
                .maskedFields(Map.of("status", "adapter-pending", "uuid", context.userUuid()))
                .build());
    }

    @Override
    public CorrectionResult correctPersonalData(DataPrincipalContext context, String field, String value) {
        return new CorrectionResult(false, "Configure module-specific DpdpDataProvider or MDMS HTTP map");
    }

    @Override
    public DeletionResult deletePersonalData(DataPrincipalContext context) {
        return new DeletionResult(false, "SOFT_DELETE", "Generic adapter does not delete module data");
    }
}
