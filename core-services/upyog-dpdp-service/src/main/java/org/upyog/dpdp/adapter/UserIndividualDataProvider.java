package org.upyog.dpdp.adapter;

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

    public UserIndividualDataProvider(RestTemplate restTemplate, DpdpConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    @Override
    public String applicationCode() {
        return "USER";
    }

    @Override
    public List<PersonalDataView> findPersonalData(DataPrincipalContext context) {
        Map<String, String> masked = new HashMap<>();
        masked.put("uuid", context.userUuid());
        masked.put("mobileNumber", PiiMasker.maskMobile("**********"));
        try {
            restTemplate.getForObject(configuration.getUserHost() + configuration.getUserSearchEndpoint(), Map.class);
        } catch (Exception ignored) {
            // Adapter is best-effort; rights orchestration still records the request.
        }
        return List.of(PersonalDataView.builder().applicationCode(applicationCode()).maskedFields(masked).build());
    }

    @Override
    public CorrectionResult correctPersonalData(DataPrincipalContext context, String field, String value) {
        return new CorrectionResult(false, "User/Individual correction must be completed in egov-user");
    }

    @Override
    public DeletionResult deletePersonalData(DataPrincipalContext context) {
        return new DeletionResult(false, "SOFT_DELETE", "User record marked for fiduciary review; physical delete is never default");
    }
}
