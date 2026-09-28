package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScanJob {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("scanType")
    @NotBlank
    private String scanType;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("assetId")
    private String assetId;
    @JsonProperty("jdbcUrl")
    private String jdbcUrl;
    @JsonProperty("jdbcUser")
    private String jdbcUser;
    @JsonProperty("jdbcPassword")
    private String jdbcPassword;
    @JsonProperty("schemaName")
    private String schemaName;
    @JsonProperty("allowSampleAnalysis")
    private boolean allowSampleAnalysis;
    @JsonProperty("openApiSpec")
    private String openApiSpec;
    @JsonProperty("logSamples")
    private List<String> logSamples;
    @JsonProperty("status")
    private String status;
    @JsonProperty("startedAt")
    private Long startedAt;
    @JsonProperty("completedAt")
    private Long completedAt;
    @JsonProperty("findingCount")
    private Integer findingCount;
    @JsonProperty("errorMessage")
    private String errorMessage;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdTime")
    private Long createdTime;
}
