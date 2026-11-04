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
public class ComplianceRule {

    @JsonProperty("code")
    private String code;
    @JsonProperty("name")
    private String name;
    @JsonProperty("category")
    private String category;
    @JsonProperty("severity")
    private String severity;
    @JsonProperty("enabled")
    private boolean enabled;
    @JsonProperty("conditionJson")
    private String conditionJson;
    @JsonProperty("recommendation")
    private String recommendation;
    @JsonProperty("reference")
    private String reference;
}
