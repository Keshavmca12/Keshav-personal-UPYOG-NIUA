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
public class ClassificationResult {

    @JsonProperty("fieldName")
    private String fieldName;
    @JsonProperty("classification")
    private String classification;
    @JsonProperty("personalData")
    private boolean personalData;
    @JsonProperty("highRiskPersonal")
    private boolean highRiskPersonal;
    @JsonProperty("matchedRule")
    private String matchedRule;
}
