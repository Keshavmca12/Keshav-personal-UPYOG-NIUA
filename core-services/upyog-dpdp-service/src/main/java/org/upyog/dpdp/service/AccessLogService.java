package org.upyog.dpdp.service;

import org.springframework.stereotype.Service;
import org.upyog.dpdp.repository.AccessLogRepository;

@Service
public class AccessLogService {

    private final AccessLogRepository repository;

    public AccessLogService(AccessLogRepository repository) {
        this.repository = repository;
    }

    public void logAccess(String tenantId, String actorUuid, String principalUuid, String applicationCode,
                          String purposeCode, String action, String resource, String sourceIp) {
        repository.insert(tenantId, actorUuid, principalUuid, applicationCode, purposeCode, action, resource, sourceIp);
    }
}
