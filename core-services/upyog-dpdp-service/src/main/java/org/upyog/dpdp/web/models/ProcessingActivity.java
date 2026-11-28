package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingActivity {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("purposeCode")
    private String purposeCode;
    @JsonProperty("processingBasis")
    private String processingBasis;
    @JsonProperty("dataCategories")
    private String dataCategories;
    @JsonProperty("retentionPolicyCode")
    private String retentionPolicyCode;
    @JsonProperty("legalHold")
    private boolean legalHold;
    @JsonProperty("status")
    private String status;
    @JsonProperty("expiresAt")
    private Long expiresAt;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdTime")
    private Long createdTime;
}
