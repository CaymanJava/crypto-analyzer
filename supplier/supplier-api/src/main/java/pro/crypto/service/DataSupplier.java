package pro.crypto.service;

import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TickData;
import pro.crypto.request.GetTickByTimeRequest;
import pro.crypto.request.GetTicksByPeriodRequest;

public interface DataSupplier {

    MarketData getAllStockMarkets(Stock stock);

    MarketData getStockMarketByName(Stock stock, String name);

    MarketData getAllMonitoredMarket(Stock stock);

    TickData getTicksByPeriod(GetTicksByPeriodRequest request);

    TickData getTicksByTime(GetTickByTimeRequest request);

}
