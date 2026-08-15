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
public class PrivacyIncident {

    @JsonProperty("id")
    private String id;
    @JsonProperty("incidentNumber")
    private String incidentNumber;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("dataCategory")
    private String dataCategory;
    @JsonProperty("incidentType")
    private String incidentType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("severity")
    private String severity;
    @JsonProperty("discoveryTime")
    private Long discoveryTime;
    @JsonProperty("affectedRecords")
    private Integer affectedRecords;
    @JsonProperty("containmentAction")
    private String containmentAction;
    @JsonProperty("notificationStatus")
    private String notificationStatus;
    @JsonProperty("description")
    private String description;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
