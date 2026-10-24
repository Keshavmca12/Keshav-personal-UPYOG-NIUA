package org.upyog.dpdp.scheduler;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.upyog.dpdp.repository.ScanRepository;
import org.upyog.dpdp.service.BreachNotificationService;
import org.upyog.dpdp.web.models.PrivacyIncident;

import java.util.List;

@Component
@ConditionalOnProperty(name = "scheduler.breach.enabled", havingValue = "true", matchIfMissing = true)
public class BreachDeadlineScheduler {

    private final ScanRepository scanRepository;
    private final BreachNotificationService breachNotificationService;

    public BreachDeadlineScheduler(ScanRepository scanRepository,
                                   BreachNotificationService breachNotificationService) {
        this.scanRepository = scanRepository;
        this.breachNotificationService = breachNotificationService;
    }

    @Scheduled(cron = "${scheduler.breach.cron:0 0/15 * * * *}")
    @SchedulerLock(name = "dpdpBreachDeadlineJob", lockAtMostFor = "PT15M", lockAtLeastFor = "PT2M")
    public void run() {
        List<PrivacyIncident> incidents = scanRepository.findIncidentsWithPendingBoardNotification(
                System.currentTimeMillis());
        for (PrivacyIncident incident : incidents) {
            breachNotificationService.notifyBoardIfDue(incident);
            breachNotificationService.flagCertInIfRequired(incident);
            scanRepository.updateIncident(incident);
        }
    }
}
