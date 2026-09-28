package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
    @JsonProperty("purposes")
    private List<PurposeDefinition> purposes;
    @JsonProperty("minimization")
    private Map<String, Object> minimization;
    @JsonProperty("findings")
    private List<Finding> findings;
    @JsonProperty("score")
    private ComplianceScore score;
    @JsonProperty("recommendations")
    private List<String> recommendations;
    @JsonProperty("trends")
    private List<ComplianceScore> trends;
    @JsonProperty("activities")
    private List<ProcessingActivity> activities;
    @JsonProperty("incidents")
    private List<PrivacyIncident> incidents;
    @JsonProperty("scans")
    private List<ScanJob> scans;
    @JsonProperty("summary")
    private Map<String, Object> summary;
}
