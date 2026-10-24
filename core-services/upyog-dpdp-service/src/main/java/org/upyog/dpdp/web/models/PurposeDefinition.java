package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurposeDefinition {

    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("processingBasis")
    private String processingBasis;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("retentionPolicy")
    private String retentionPolicy;
    @JsonProperty("dataFields")
    private List<DataFieldPolicy> dataFields;
}
