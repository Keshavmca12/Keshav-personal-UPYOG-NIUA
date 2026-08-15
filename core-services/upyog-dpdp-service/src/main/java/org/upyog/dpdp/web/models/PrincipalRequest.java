package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrincipalRequest {

    @JsonProperty("id")
    private String id;
    @JsonProperty("requestNumber")
    private String requestNumber;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("dataPrincipalUuid")
    private String dataPrincipalUuid;
    @JsonProperty("requestType")
    @NotBlank
    private String requestType;
    @JsonProperty("purposeCode")
    private String purposeCode;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("justification")
    private String justification;
    @JsonProperty("processInstanceId")
    private String processInstanceId;
    @JsonProperty("resultSummary")
    private String resultSummary;
    @JsonProperty("correctionField")
    private String correctionField;
    @JsonProperty("correctionValue")
    private String correctionValue;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
