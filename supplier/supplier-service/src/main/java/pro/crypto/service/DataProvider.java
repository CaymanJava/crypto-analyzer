package pro.crypto.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TickData;
import pro.crypto.request.GetTickByTimeRequest;
import pro.crypto.request.GetTicksByPeriodRequest;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DataProvider implements DataSupplier, StockService {

    private final Map<Stock, StockClient> spiderClients;

    public DataProvider(List<StockClient> stockClients) {
        this.spiderClients = createSpidersClientsMap(stockClients);
    }

    @Override
    public MarketData getAllStockMarkets(Stock stock) {
        log.debug("Getting all stock markets {stock: {}}", stock);
        MarketData markets = spiderClients.get(stock).getAllMarkets();
        log.debug("Got all stock markets {stock: {}, markets: {}}", stock, markets);
        return markets;
    }

    @Override
    public MarketData getStockMarketByName(Stock stock, String name) {
        log.debug("Getting stock market by name {stock: {}, name: {}}", stock, name);
        MarketData market = spiderClients.get(stock).getMarketByName(name);
        log.debug("Got stock market by name {stock: {}, name: {}, market: {}}", stock, name, market);
        return market;
    }

    @Override
    public MarketData getAllMonitoredMarket(Stock stock) {
        log.debug("Getting all stock monitored markets {stock: {}}", stock);
        MarketData monitoredMarkets = spiderClients.get(stock).getAllMonitoredMarkets();
        log.debug("Got all stock monitored markets {stock: {}, monitoredMarkets: {}}", stock, monitoredMarkets);
        return monitoredMarkets;
    }

    @Override
    public TickData getTicksByPeriod(GetTicksByPeriodRequest request) {
        log.debug("Getting ticks by period {request: {}}", request);
        TickData ticks = spiderClients.get(request.getStock()).getTicksByPeriod(request.getMarketId(), request.getTimeFrame(), request.getPeriod());
        log.debug("Got ticks by period {request: {}, ticks: {}}", request, ticks);
        return ticks;
    }

    @Override
    public TickData getTicksByTime(GetTickByTimeRequest request) {
        log.debug("Getting ticks by time {request: {}}", request);
        TickData ticks = spiderClients.get(request.getStock()).getTicksByTime(request.getMarketId(), request.getTimeFrame(),
                request.getFrom(), request.getTo());
        log.debug("Got ticks by time {request: {}, ticks: {}}", request, ticks);
        return ticks;
    }

}
