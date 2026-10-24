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
public class PrivacyNotice {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("purposeCode")
    @NotBlank
    private String purposeCode;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("version")
    private String version;
    @JsonProperty("title")
    private String title;
    @JsonProperty("body")
    private String body;
    @JsonProperty("effectiveFrom")
    private Long effectiveFrom;
    @JsonProperty("status")
    private String status;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("createdTime")
    private Long createdTime;
}
