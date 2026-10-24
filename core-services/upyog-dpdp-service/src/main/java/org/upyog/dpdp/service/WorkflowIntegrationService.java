package org.upyog.dpdp.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.web.models.PrincipalRequest;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WorkflowIntegrationService {

    private final RestTemplate restTemplate;
    private final DpdpConfiguration configuration;

    public WorkflowIntegrationService(RestTemplate restTemplate, DpdpConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    public String startRightsWorkflow(RequestInfo requestInfo, PrincipalRequest principal) {
        if (!configuration.isWorkflowEnabled()) {
            return null;
        }
        Map<String, Object> body = new HashMap<>();
        body.put("RequestInfo", requestInfo);
        body.put("ProcessInstance", Map.of(
                "tenantId", principal.getTenantId(),
                "businessService", configuration.getWorkflowBusinessService(),
                "businessId", principal.getRequestNumber(),
                "action", "APPLY"
        ));
        try {
            Map<?, ?> response = restTemplate.postForObject(
                    configuration.getWorkflowHost() + "/egov-workflow-v2/egov-wf/process/_transition",
                    body, Map.class);
            if (response != null && response.get("ProcessInstance") instanceof Map<?, ?> instance) {
                Object id = instance.get("id");
                return id == null ? null : String.valueOf(id);
            }
        } catch (Exception ex) {
            log.warn("Workflow start failed for rights request {}: {}", principal.getId(), ex.getMessage());
        }
        return null;
    }
}
