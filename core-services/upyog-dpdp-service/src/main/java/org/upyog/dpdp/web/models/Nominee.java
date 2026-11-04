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
public class Nominee {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    @NotBlank
    private String tenantId;
    @JsonProperty("dataPrincipalUuid")
    @NotBlank
    private String dataPrincipalUuid;
    @JsonProperty("nomineeName")
    @NotBlank
    private String nomineeName;
    @JsonProperty("nomineeContact")
    private String nomineeContact;
    @JsonProperty("relationship")
    private String relationship;
    @JsonProperty("status")
    private String status;
    @JsonProperty("createdBy")
    private String createdBy;
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;
    @JsonProperty("createdTime")
    private Long createdTime;
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
