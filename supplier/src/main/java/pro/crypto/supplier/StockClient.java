package pro.crypto.supplier;

import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TickData;
import pro.crypto.model.tick.TimeFrame;

import java.time.LocalDateTime;

public interface StockClient {

    MarketData getAllMarkets();

    MarketData getMarketByName(String name);

    MarketData getAllMonitoredMarkets();

    void startMonitorMarket(long marketId);

    void startAllMonitors();

    void stopMonitor(long marketId);

    void stopAllMonitors();

    TickData getTicksByPeriod(long marketId, TimeFrame timeFrame, int period);

    TickData getTicksByTime(long marketId, TimeFrame timeFrame, LocalDateTime from, LocalDateTime to);

    Stock getStock();

}
