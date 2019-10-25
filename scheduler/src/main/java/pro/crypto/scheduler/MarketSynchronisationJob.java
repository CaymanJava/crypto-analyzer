package pro.crypto.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pro.crypto.service.MarketSynchronizationService;

@Slf4j
@Component
public class MarketSynchronisationJob implements Job {

    @Autowired
    private MarketSynchronizationService marketSynchronizationService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.trace("Starting market synchronisation job");
        marketSynchronizationService.synchronizeMarkets();
        log.info("Finished market synchronisation job");
    }

}
