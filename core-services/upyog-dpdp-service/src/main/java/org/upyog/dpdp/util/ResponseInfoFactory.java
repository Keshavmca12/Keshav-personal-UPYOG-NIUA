package org.upyog.dpdp.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

@Component
public class ResponseInfoFactory {

    public ResponseInfo create(RequestInfo requestInfo, boolean success) {
        ResponseInfo.ResponseInfoBuilder builder = ResponseInfo.builder()
                .apiId(requestInfo == null ? null : requestInfo.getApiId())
                .ver(requestInfo == null ? null : requestInfo.getVer())
                .ts(requestInfo == null ? null : requestInfo.getTs())
                .msgId(requestInfo == null ? null : requestInfo.getMsgId())
                .status(success ? "successful" : "failed");
        return builder.build();
    }
}
