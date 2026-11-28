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
public class GrievanceResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;
    @JsonProperty("Grievances")
    private List<Grievance> grievances;
    @JsonProperty("Grievance")
    private Grievance grievance;
}
