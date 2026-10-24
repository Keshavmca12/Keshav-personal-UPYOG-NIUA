package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalDataView {

    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("maskedFields")
    private Map<String, String> maskedFields;
}
