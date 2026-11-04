package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuardianResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
    @JsonProperty("GuardianLink")
    private GuardianLink guardianLink;
    @JsonProperty("GuardianLinks")
    private List<GuardianLink> guardianLinks;
}
