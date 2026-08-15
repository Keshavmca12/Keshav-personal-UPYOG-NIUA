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
public class Finding {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("scanId")
    private String scanId;
    @JsonProperty("ruleCode")
    private String ruleCode;
    @JsonProperty("category")
    private String category;
    @JsonProperty("severity")
    private String severity;
    @JsonProperty("status")
    private String status;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("assetName")
    private String assetName;
    @JsonProperty("fieldName")
    private String fieldName;
    @JsonProperty("classification")
    private String classification;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("recommendation")
    private String recommendation;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdTime")
    private Long createdTime;
}
