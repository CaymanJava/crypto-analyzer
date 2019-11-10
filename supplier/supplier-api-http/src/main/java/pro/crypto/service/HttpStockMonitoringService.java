package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.model.market.Stock;
import pro.crypto.proxy.HttpStockMonitoringProxy;

@Service
@AllArgsConstructor
public class HttpStockMonitoringService implements StockMonitoringService {

    private final HttpStockMonitoringProxy stockMonitoringProxy;

    @Override
    public void startMonitorStockMarket(Stock stock, long marketId) {
        stockMonitoringProxy.startMonitorStockMarket(stock, marketId);
    }

    @Override
    public void startAllStockMonitors(Stock stock) {
        stockMonitoringProxy.startAllStockMonitors(stock);
    }

    @Override
    public void stopMonitorStockMarket(Stock stock, long marketId) {
        stockMonitoringProxy.stopMonitorStockMarket(stock, marketId);
    }

    @Override
    public void stopAllStockMonitors(Stock stock) {
        stockMonitoringProxy.stopAllStockMonitors(stock);
    }

}
