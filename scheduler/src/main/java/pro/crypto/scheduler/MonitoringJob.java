package pro.crypto.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.crypto.service.MonitoringService;

@Slf4j
@Component
public class MonitoringJob implements Job {

    @Autowired
    private MonitoringService monitoringService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.trace("Starting monitoring job");
        monitoringService.startMonitoring();
        log.info("Finished monitoring job");
    }

}
