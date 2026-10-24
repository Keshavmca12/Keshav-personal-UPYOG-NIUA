package org.upyog.dpdp.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NominationRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    @JsonProperty("Nominee")
    private Nominee nominee;
}
