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
public class RightsResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
    @JsonProperty("PrincipalRequest")
    private PrincipalRequest principalRequest;
    @JsonProperty("PrincipalRequests")
    private List<PrincipalRequest> principalRequests;
    @JsonProperty("personalData")
    private List<PersonalDataView> personalData;
}
