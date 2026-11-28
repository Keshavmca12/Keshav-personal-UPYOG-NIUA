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
public class ConsentVerification {

    @JsonProperty("required")
    private boolean required;
    @JsonProperty("allowed")
    private boolean allowed;
    @JsonProperty("status")
    private String status;
    @JsonProperty("processingBasis")
    private String processingBasis;
    @JsonProperty("purposeCode")
    private String purposeCode;
}
