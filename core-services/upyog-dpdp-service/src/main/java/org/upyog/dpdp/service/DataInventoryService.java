package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.repository.DataInventoryValidationRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataInventoryService {

    private final MdmsPolicyClient mdmsPolicyClient;
    private final DataInventoryValidationRepository validationRepository;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public DataInventoryService(MdmsPolicyClient mdmsPolicyClient,
                                DataInventoryValidationRepository validationRepository,
                                RequestValidator requestValidator,
                                ResponseInfoFactory responseInfoFactory) {
        this.mdmsPolicyClient = mdmsPolicyClient;
        this.validationRepository = validationRepository;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    @SuppressWarnings("unchecked")
    public GenericResponse validate(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = String.valueOf(request.getCriteria().get("tenantId"));
        requestValidator.assertTenantAccess(user, tenantId);
        String applicationCode = String.valueOf(request.getCriteria().getOrDefault("applicationCode", "USER"));
        List<Map<String, Object>> inventory = mdmsPolicyClient.getDataInventory(tenantId);
        List<Map<String, Object>> findings = new ArrayList<>();
        for (Map<String, Object> asset : inventory) {
            if (!applicationCode.equals(String.valueOf(asset.get("applicationCode")))) {
                continue;
            }
            Object fields = asset.get("fields");
            if (!(fields instanceof List<?> fieldList)) {
                continue;
            }
            for (Object fieldObj : fieldList) {
                if (!(fieldObj instanceof Map<?, ?> fieldMap)) {
                    continue;
                }
                String fieldName = String.valueOf(fieldMap.get("field"));
                String expected = String.valueOf(fieldMap.get("category"));
                String actual = String.valueOf(request.getCriteria().getOrDefault(fieldName, expected));
                String status = expected.equals(actual) ? DpdpConstants.STATUS_COMPLETED : DpdpConstants.STATUS_REJECTED;
                validationRepository.save(tenantId, applicationCode, String.valueOf(asset.get("assetName")),
                        fieldName, expected, actual, status, null, user.getUuid());
                Map<String, Object> row = new HashMap<>();
                row.put("field", fieldName);
                row.put("expectedCategory", expected);
                row.put("actualCategory", actual);
                row.put("status", status);
                findings.add(row);
            }
        }
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .summary(Map.of("validations", findings))
                .build();
    }
}
