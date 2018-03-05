package pro.crypto.supplier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.crypto.model.market.Stock;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class MonitorSupervisor implements StockService {

    private final Map<Stock, SpiderClient> spiderClients;

    public MonitorSupervisor(List<SpiderClient> spiderClients) {
        this.spiderClients = createSpidersClientsMap(spiderClients);
    }

    public void startMonitorStockMarket(Stock stock, long marketId) {
        log.info("Adding stock market to monitoring {stock: {}, marketId: {}}", stock, marketId);
        spiderClients.get(stock).startMonitorMarket(marketId);
        log.info("Added stock market to monitoring {stock: {}, marketId: {}}", stock, marketId);
    }

    public void startAllStockMonitors(Stock stock) {
        log.info("Adding all available stock markets to monitoring {stock: {}}", stock);
        spiderClients.get(stock).startAllMonitors();
        log.info("Added all available stock markets to monitoring {stock: {}}", stock);
    }

    public void stopMonitorStockMarket(Stock stock, long marketId) {
        log.info("Stopping monitoring {stock: {}, marketId: {}}", stock, marketId);
        spiderClients.get(stock).stopMonitor(marketId);
        log.info("Stopped monitoring {stock: {}, marketId: {}}", stock, marketId);
    }

    public void stopAllStockMonitors(Stock stock) {
        log.info("Stopping all available markets to monitoring {stock: {}}", stock);
        spiderClients.get(stock).stopAllMonitors();
        log.info("Stopped all available markets to monitoring {stock: {}}", stock);
    }

}
