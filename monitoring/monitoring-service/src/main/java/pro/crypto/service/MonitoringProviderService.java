package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.message.StrategyMonitoringMessage;
import pro.crypto.routing.CommonRouter;

@AllArgsConstructor
@Service
@Slf4j
public class MonitoringProviderService implements MonitoringService {

    private final CommonRouter router;

    @Override
    public void startMonitoring() {
        log.trace("Starting monitoring");
        router.tell(new StrategyMonitoringMessage());
        log.info("Monitoring has started");
    }

}
