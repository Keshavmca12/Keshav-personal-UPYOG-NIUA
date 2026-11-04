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
public class NoticeAcknowledgement {

    @JsonProperty("tenantId")
    private String tenantId;
    @JsonProperty("noticeId")
    private String noticeId;
    @JsonProperty("dataPrincipalUuid")
    private String dataPrincipalUuid;
    @JsonProperty("channel")
    private String channel;
}
