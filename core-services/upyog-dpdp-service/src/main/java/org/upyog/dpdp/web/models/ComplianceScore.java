package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceScore {

    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("overallScore")
    private BigDecimal overallScore;
    @JsonProperty("categoryScores")
    private Map<String, BigDecimal> categoryScores;
    @JsonProperty("openFindings")
    private Integer openFindings;
    @JsonProperty("criticalFindings")
    private Integer criticalFindings;
    @JsonProperty("highRiskFindings")
    private Integer highRiskFindings;
    @JsonProperty("formula")
    private String formula;
    @JsonProperty("snapshotDay")
    private String snapshotDay;
}
