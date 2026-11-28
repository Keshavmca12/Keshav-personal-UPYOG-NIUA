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
public class GuardianLink {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("childUuid")
    @NotBlank
    private String childUuid;
    @JsonProperty("guardianUuid")
    @NotBlank
    private String guardianUuid;
    @JsonProperty("relationship")
    private String relationship;
    @JsonProperty("verificationMethod")
    private String verificationMethod;
    @JsonProperty("authorityOrderRef")
    private String authorityOrderRef;
    @JsonProperty("verified")
    private boolean verified;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
