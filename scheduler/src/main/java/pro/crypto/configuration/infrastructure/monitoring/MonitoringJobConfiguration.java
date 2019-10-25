package pro.crypto.configuration.infrastructure.monitoring;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pro.crypto.configuration.infrastructure.JobInfo;
import pro.crypto.configuration.infrastructure.JobService;
import pro.crypto.scheduler.MonitoringJob;

import java.util.List;

import static java.util.Collections.singletonList;

@Component
@AllArgsConstructor
public class MonitoringJobConfiguration implements JobService {

    private static final String MONITORING_JOB_DESCRIPTION = "Executes all monitored member's strategies";
    private final MonitoringExpressionProperties monitoringExpressionProperties;

    @Override
    public List<JobInfo> getJobsToScheduling() {
        return singletonList(
                getJobInfo("MonitoringJob", "group.Monitoring", MONITORING_JOB_DESCRIPTION,
                        MonitoringJob.class, monitoringExpressionProperties.getMonitoringExpression())
        );
    }

}
