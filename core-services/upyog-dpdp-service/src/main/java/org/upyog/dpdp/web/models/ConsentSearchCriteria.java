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
public class ConsentSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("dataPrincipalUuid")
    private String dataPrincipalUuid;
    @JsonProperty("purposeCode")
    private String purposeCode;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("status")
    private String status;
    @JsonProperty("id")
    private String id;
    @JsonProperty("offset")
    private Integer offset;
    @JsonProperty("limit")
    private Integer limit;
}
