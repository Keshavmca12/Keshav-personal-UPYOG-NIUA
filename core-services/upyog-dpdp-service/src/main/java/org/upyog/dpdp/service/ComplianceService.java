package org.upyog.dpdp.service;

import org.egov.common.contract.request.User;
import org.springframework.stereotype.Service;
import org.upyog.dpdp.ai.DpdpAiRecommendationProvider;
import org.upyog.dpdp.engine.ScoringService;
import org.upyog.dpdp.repository.ScanRepository;
import org.upyog.dpdp.util.RequestValidator;
import org.upyog.dpdp.util.ResponseInfoFactory;
import org.upyog.dpdp.web.models.ComplianceScore;
import org.upyog.dpdp.web.models.Finding;
import org.upyog.dpdp.web.models.GenericRequest;
import org.upyog.dpdp.web.models.GenericResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ComplianceService {

    private final ScanRepository repository;
    private final ScoringService scoringService;
    private final DpdpAiRecommendationProvider recommendationProvider;
    private final RequestValidator requestValidator;
    private final ResponseInfoFactory responseInfoFactory;

    public ComplianceService(ScanRepository repository, ScoringService scoringService,
                             DpdpAiRecommendationProvider recommendationProvider,
                             RequestValidator requestValidator, ResponseInfoFactory responseInfoFactory) {
        this.repository = repository;
        this.scoringService = scoringService;
        this.recommendationProvider = recommendationProvider;
        this.requestValidator = requestValidator;
        this.responseInfoFactory = responseInfoFactory;
    }

    private String tenant(GenericRequest request, User user) {
        Object tenantId = request.getCriteria() == null ? null : request.getCriteria().get("tenantId");
        String value = tenantId == null ? user.getTenantId() : String.valueOf(tenantId);
        requestValidator.assertTenantAccess(user, value);
        return value;
    }

    public GenericResponse summary(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = tenant(request, user);
        List<Finding> findings = repository.searchFindings(tenantId, "OPEN");
        ComplianceScore score = scoringService.score(tenantId, findings);
        Map<String, Object> summary = new HashMap<>();
        summary.put("openFindings", score.getOpenFindings());
        summary.put("criticalFindings", score.getCriticalFindings());
        summary.put("overallScore", score.getOverallScore());
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .summary(summary)
                .score(score)
                .build();
    }

    public GenericResponse score(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = tenant(request, user);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .score(scoringService.score(tenantId, repository.searchFindings(tenantId, "OPEN")))
                .build();
    }

    public GenericResponse findings(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = tenant(request, user);
        String status = request.getCriteria() == null || request.getCriteria().get("status") == null
                ? "OPEN" : String.valueOf(request.getCriteria().get("status"));
        List<Finding> findings = repository.searchFindings(tenantId, status);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .findings(findings)
                .score(scoringService.score(tenantId, findings))
                .build();
    }

    public GenericResponse recommendations(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = tenant(request, user);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .recommendations(recommendationProvider.recommend(repository.searchFindings(tenantId, "OPEN")))
                .build();
    }

    public GenericResponse trends(GenericRequest request) {
        User user = requestValidator.requireUser(request.getRequestInfo());
        String tenantId = tenant(request, user);
        return GenericResponse.builder()
                .responseInfo(responseInfoFactory.create(request.getRequestInfo(), true))
                .trends(repository.trends(tenantId))
                .build();
    }
}
