package org.upyog.dpdp.service;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.repository.NoticeRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.NoticeAcknowledgement;
import org.upyog.dpdp.web.models.NoticeRequest;
import org.upyog.dpdp.web.models.NoticeResponse;
import org.upyog.dpdp.web.models.NoticeSearchCriteria;
import org.upyog.dpdp.web.models.PrivacyNotice;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MdmsPolicyClient mdmsPolicyClient;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public NoticeService(NoticeRepository noticeRepository, MdmsPolicyClient mdmsPolicyClient,
                         RequestValidator requestValidator, ResponseInfoFactory responseInfoFactory) {
        this.noticeRepository = noticeRepository;
        this.mdmsPolicyClient = mdmsPolicyClient;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    public NoticeResponse search(NoticeRequest request) {
        NoticeSearchCriteria resolved = request.getNoticeSearchCriteria();
        if (resolved == null || StringUtils.isBlank(resolved.getTenantId())) {
            User user = requestValidator.requireUser(request.getRequestInfo());
            if (resolved == null) {
                resolved = new NoticeSearchCriteria();
            }
            resolved.setTenantId(user.getTenantId());
            requestValidator.assertTenantAccess(user, resolved.getTenantId());
        }
        final NoticeSearchCriteria criteria = resolved;
        List<PrivacyNotice> stored = noticeRepository.search(criteria);
        if (!stored.isEmpty()) {
            return NoticeResponse.builder()
                    .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                    .notices(stored)
                    .build();
        }
        String locale = StringUtils.defaultIfBlank(criteria.getLocale(), "en_IN");
        List<Map<String, Object>> templates = mdmsPolicyClient.getNoticeTemplates(criteria.getTenantId(), locale);
        long now = System.currentTimeMillis();
        List<PrivacyNotice> notices = templates.stream()
                .filter(t -> criteria.getPurposeCode() == null
                        || criteria.getPurposeCode().equals(String.valueOf(t.get("purposeCode"))))
                .filter(t -> criteria.getVersion() == null
                        || criteria.getVersion().equals(String.valueOf(t.get("version"))))
                .map(t -> {
                    PrivacyNotice notice = PrivacyNotice.builder()
                            .id(UUID.randomUUID().toString())
                            .tenantId(criteria.getTenantId())
                            .purposeCode(String.valueOf(t.get("purposeCode")))
                            .locale(String.valueOf(t.getOrDefault("locale", locale)))
                            .version(String.valueOf(t.get("version")))
                            .title(String.valueOf(t.get("title")))
                            .body(String.valueOf(t.get("body")))
                            .effectiveFrom(now)
                            .status(DpdpConstants.STATUS_ACTIVE)
                            .createdBy("system")
                            .createdTime(now)
                            .build();
                    noticeRepository.upsert(notice);
                    return notice;
                })
                .toList();
        return NoticeResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .notices(notices)
                .build();
    }

    public NoticeResponse acknowledge(NoticeRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        NoticeAcknowledgement ack = request.getNoticeAcknowledgement();
        requestValidator.assertTenantAccess(user, ack.getTenantId());
        String principal = requestValidator.resolvePrincipal(user, ack.getDataPrincipalUuid());
        noticeRepository.acknowledge(ack.getTenantId(), ack.getNoticeId(), principal, ack.getChannel(), user.getUuid());
        return NoticeResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .acknowledged(true)
                .build();
    }
}
