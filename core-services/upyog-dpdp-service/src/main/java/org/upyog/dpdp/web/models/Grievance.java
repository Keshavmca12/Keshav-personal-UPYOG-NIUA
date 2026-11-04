package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grievance {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("requestId")
    private String requestId;
    @JsonProperty("dataPrincipalUuid")
    private String dataPrincipalUuid;
    @JsonProperty("subject")
    private String subject;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private String status;
    @JsonProperty("slaDueAt")
    private Long slaDueAt;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
