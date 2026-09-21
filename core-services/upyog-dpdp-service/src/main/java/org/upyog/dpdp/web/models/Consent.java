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
public class Consent {

    @JsonProperty("id")
    private String id;
    @JsonProperty("consentNumber")
    private String consentNumber;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("dataPrincipalUuid")
    private String dataPrincipalUuid;
    @JsonProperty("fiduciary")
    private String fiduciary;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("dataCategory")
    private String dataCategory;
    @JsonProperty("purposeCode")
    @NotBlank
    private String purposeCode;
    @JsonProperty("processingBasis")
    private String processingBasis;
    @JsonProperty("status")
    private String status;
    @JsonProperty("version")
    private Integer version;
    @JsonProperty("source")
    private String source;
    @JsonProperty("language")
    private String language;
    @JsonProperty("grantedTime")
    private Long grantedTime;
    @JsonProperty("withdrawalTime")
    private Long withdrawalTime;
    @JsonProperty("expiryTime")
    private Long expiryTime;
    @JsonProperty("noticeRef")
    private String noticeRef;
    @JsonProperty("purposes")
    private List<String> purposes;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
