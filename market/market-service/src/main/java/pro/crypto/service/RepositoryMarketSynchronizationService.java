package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.market.StockMarket;

import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.of;
import static pro.crypto.model.market.Status.DELETED;

@Service
@Slf4j
@AllArgsConstructor
public class RepositoryMarketSynchronizationService implements MarketSynchronizationService {

    private DataSupplier dataSupplier;
    private MarketService marketService;
    private MemberStrategyControlService memberStrategyControlService;

    @Async
    @Override
    public void synchronizeMarkets() {
        log.trace("Starting market synchronization");
        of(Stock.values())
                .map(dataSupplier::getAllStockMarkets)
                .forEach(this::saveMarkets);
        log.info("Finished market synchronization");
    }

    private void saveMarkets(MarketData marketData) {
        Stock stock = marketData.getStockExchangeName();
        Set<Long> marketIds = of(marketData.getMarkets())
                .map(market -> updateMarket(stock, market))
                .filter(Objects::nonNull)
                .collect(toSet());
        memberStrategyControlService.stopMonitoring(marketIds, "Market was deleted");
    }

    private Long updateMarket(Stock stock, StockMarket market) {
        Long marketId = marketService.save(market, stock);
        return filterDeletedMarket(market, marketId);
    }

    private Long filterDeletedMarket(StockMarket market, Long marketId) {
        if (market.getStatus() == DELETED) {
            return marketId;
        }
        return null;
    }

}
