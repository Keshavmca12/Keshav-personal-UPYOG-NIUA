package org.upyog.dpdp.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dpdp.service.RetentionService;

@Component
@ConditionalOnProperty(name = "scheduler.retention.enabled", havingValue = "true", matchIfMissing = true)
public class RetentionScheduler {

    private final RetentionService retentionService;

    public RetentionScheduler(RetentionService retentionService) {
        this.retentionService = retentionService;
    }

    @Scheduled(cron = "${scheduler.retention.cron}")
    @SchedulerLock(name = "dpdpRetentionJob", lockAtMostFor = "PT30M", lockAtLeastFor = "PT5M")
    public void run() {
        retentionService.evaluateExpired(new RequestInfo());
    }
}
