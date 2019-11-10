package pro.crypto.service;

import pro.crypto.model.market.Stock;

public interface StockMonitoringService {

    void startMonitorStockMarket(Stock stock, long marketId);

    void startAllStockMonitors(Stock stock);

    void stopMonitorStockMarket(Stock stock, long marketId);

    void stopAllStockMonitors(Stock stock);

}
