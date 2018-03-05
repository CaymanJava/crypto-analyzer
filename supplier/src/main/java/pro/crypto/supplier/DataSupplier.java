package pro.crypto.supplier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TickData;
import pro.crypto.request.GetTickByTimeRequest;
import pro.crypto.request.GetTicksByPeriodRequest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DataSupplier implements StockService {

    private final Map<Stock, SpiderClient> spiderClients;

    public DataSupplier(List<SpiderClient> spiderClients) {
        this.spiderClients = createSpidersClientsMap(spiderClients);
    }

    public MarketData getAllStockMarkets(Stock stock) {
        log.debug("Getting all stock markets {stock: {}}", stock);
        MarketData markets = spiderClients.get(stock).getAllMarkets();
        log.debug("Got all stock markets {stock: {}, markets: {}}", stock, markets);
        return markets;
    }

    public MarketData getStockMarketByName(Stock stock, String name) {
        log.debug("Getting stock market by name {stock: {}, name: {}}", stock, name);
        MarketData market = spiderClients.get(stock).getMarketByName(name);
        log.debug("Got stock market by name {stock: {}, name: {}, market: {}}", stock, name, market);
        return market;
    }

    public MarketData getAllMonitoredMarket(Stock stock) {
        log.debug("Getting all stock monitored markets {stock: {}}", stock);
        MarketData monitoredMarkets = spiderClients.get(stock).getAllMonitoredMarkets();
        log.debug("Got all stock monitored markets {stock: {}, monitoredMarkets: {}}", stock, monitoredMarkets);
        return monitoredMarkets;
    }

    public TickData getTicksByPeriod(GetTicksByPeriodRequest request) {
        log.debug("Getting ticks by period {stock: {}, request: {}}", request);
        TickData ticks = spiderClients.get(request.getStock()).getTicksByPeriod(request.getMarketId(), request.getTimeFrame(), request.getPeriod());
        log.debug("Got ticks by period {stock: {}, request: {}, ticks: {}}", request, ticks);
        return ticks;
    }

    public TickData getTicksByTime(GetTickByTimeRequest request) {
        log.debug("Getting ticks by time {stock: {}, request: {}}", request);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        TickData ticks = spiderClients.get(request.getStock()).getTicksByTime(request.getMarketId(), request.getTimeFrame(),
                formatter.format(request.getFrom()), formatter.format(request.getTo()));
        log.debug("Got ticks by time {stock: {}, request: {}, ticks: {}}", request, ticks);
        return ticks;
    }

}
