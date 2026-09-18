package org.upyog.dpdp.service;

import io.micrometer.core.instrument.MeterRegistry;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.config.DpdpConfiguration;
import org.upyog.dpdp.producer.Producer;
import org.upyog.dpdp.repository.ConsentRepository;
import org.upyog.dpdp.util.DpdpConstants;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.Consent;
import org.upyog.dpdp.web.models.ConsentRequest;
import org.upyog.dpdp.web.models.ConsentResponse;
import org.upyog.dpdp.web.models.ConsentSearchCriteria;
import org.upyog.dpdp.web.models.ConsentSearchRequest;
import org.upyog.dpdp.web.models.ConsentVerification;

import java.util.List;
import java.util.UUID;

@Service
public class ConsentService {

    private final ConsentRepository repository;
    private final PolicyService policyService;
    private final PrivacyEventService privacyEventService;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;
    private final Producer producer;
    private final DpdpConfiguration configuration;
    private final MeterRegistry meterRegistry;

    public ConsentService(ConsentRepository repository, PolicyService policyService,
                          PrivacyEventService privacyEventService, RequestValidator requestValidator,
                          ResponseInfoFactory responseInfoFactory, Producer producer,
                          DpdpConfiguration configuration, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.policyService = policyService;
        this.privacyEventService = privacyEventService;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
        this.producer = producer;
        this.configuration = configuration;
        this.meterRegistry = meterRegistry;
    }

    public ConsentResponse create(ConsentRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        Consent consent = request.getConsent();
        requestValidator.assertTenantAccess(user, consent.getTenantId());
        consent.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, consent.getDataPrincipalUuid()));
        consent.setProcessingBasis(policyService.resolveProcessingBasis(
                consent.getTenantId(), consent.getPurposeCode(), consent.getProcessingBasis()));
        long now = System.currentTimeMillis();
        consent.setId(UUID.randomUUID().toString());
        consent.setConsentNumber("DPDP-CNS-" + now);
        consent.setStatus(DpdpConstants.CONSENT_GRANTED);
        consent.setVersion(consent.getVersion() == null ? 1 : consent.getVersion());
        consent.setGrantedTime(now);
        consent.setCreatedBy(user.getUuid());
        consent.setLastModifiedBy(user.getUuid());
        consent.setCreatedTime(now);
        consent.setLastModifiedTime(now);
        repository.insert(consent);
        if (configuration.isPersisterEnabled()) {
            producer.push(configuration.getSaveConsentTopic(), request);
        }
        privacyEventService.emit(request.getRequestInfo(), consent.getTenantId(), "DPDP_CONSENT_GRANTED",
                "CONSENT", consent.getId(), consent.getApplicationCode(), consent.getPurposeCode(),
                consent.getDataCategory(), "SUCCESS", configuration.getConsentEventTopic());
        meterRegistry.counter("dpdp.consent.granted").increment();
        return ConsentResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .consent(consent)
                .build();
    }

    public ConsentResponse search(ConsentSearchRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        ConsentSearchCriteria criteria = request.getConsentSearchCriteria() == null
                ? new ConsentSearchCriteria() : request.getConsentSearchCriteria();
        if (criteria.getTenantId() == null) {
            criteria.setTenantId(user.getTenantId());
        }
        requestValidator.assertTenantAccess(user, criteria.getTenantId());
        criteria.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, criteria.getDataPrincipalUuid()));
        List<Consent> consents = repository.search(criteria);
        return ConsentResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .consents(consents)
                .build();
    }

    public ConsentResponse withdraw(ConsentRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        Consent incoming = request.getConsent();
        requestValidator.assertTenantAccess(user, incoming.getTenantId());
        Consent existing = repository.findById(incoming.getId(), incoming.getTenantId());
        if (existing == null) {
            throw new CustomException(DpdpConstants.ERR_NOT_FOUND, "Consent not found");
        }
        requestValidator.resolvePrincipal(user, existing.getDataPrincipalUuid());
        long now = System.currentTimeMillis();
        existing.setStatus(DpdpConstants.CONSENT_WITHDRAWN);
        existing.setWithdrawalTime(now);
        existing.setLastModifiedBy(user.getUuid());
        existing.setLastModifiedTime(now);
        repository.update(existing);
        if (configuration.isPersisterEnabled()) {
            producer.push(configuration.getUpdateConsentTopic(), request);
        }
        privacyEventService.emit(request.getRequestInfo(), existing.getTenantId(), "DPDP_CONSENT_WITHDRAWN",
                "CONSENT", existing.getId(), existing.getApplicationCode(), existing.getPurposeCode(),
                existing.getDataCategory(), "SUCCESS", configuration.getConsentEventTopic());
        meterRegistry.counter("dpdp.consent.withdrawn").increment();
        return ConsentResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .consent(existing)
                .build();
    }

    public ConsentResponse verify(ConsentSearchRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        ConsentSearchCriteria criteria = request.getConsentSearchCriteria() == null
                ? new ConsentSearchCriteria() : request.getConsentSearchCriteria();
        requestValidator.assertTenantAccess(user, criteria.getTenantId());
        String basis = policyService.resolveProcessingBasis(criteria.getTenantId(), criteria.getPurposeCode(), null);
        boolean required = DpdpConstants.BASIS_CONSENT.equals(basis);
        boolean allowed = !required;
        String status = "NOT_REQUIRED";
        if (required) {
            criteria.setDataPrincipalUuid(requestValidator.resolvePrincipal(user, criteria.getDataPrincipalUuid()));
            criteria.setStatus(DpdpConstants.CONSENT_GRANTED);
            allowed = !repository.search(criteria).isEmpty();
            status = allowed ? DpdpConstants.CONSENT_GRANTED : "MISSING";
        }
        return ConsentResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .verification(ConsentVerification.builder()
                        .required(required)
                        .allowed(allowed)
                        .status(status)
                        .processingBasis(basis)
                        .purposeCode(criteria.getPurposeCode())
                        .build())
                .build();
    }
}
