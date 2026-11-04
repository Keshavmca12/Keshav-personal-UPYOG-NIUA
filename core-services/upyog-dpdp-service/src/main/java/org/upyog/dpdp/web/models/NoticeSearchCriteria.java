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
public class NoticeSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("purposeCode")
    private String purposeCode;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("version")
    private String version;
}
