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
public class DataFieldPolicy {

    @JsonProperty("field")
    private String field;
    @JsonProperty("classification")
    private String classification;
    @JsonProperty("pattern")
    private String pattern;
    @JsonProperty("required")
    private boolean required;
    @JsonProperty("justified")
    private boolean justified;
}
