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
public class PrivacyEvent {

    @JsonProperty("id")
    private String id;
    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("actorUuid")
    private String actorUuid;
    @JsonProperty("action")
    private String action;
    @JsonProperty("entityType")
    private String entityType;
    @JsonProperty("entityId")
    private String entityId;
    @JsonProperty("applicationCode")
    private String applicationCode;
    @JsonProperty("purposeCode")
    private String purposeCode;
    @JsonProperty("dataCategory")
    private String dataCategory;
    @JsonProperty("result")
    private String result;
    @JsonProperty("sourceIp")
    private String sourceIp;
    @JsonProperty("detail")
    private String detail;
    @JsonProperty("createdTime")
    private Long createdTime;
}
