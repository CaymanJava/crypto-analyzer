package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;

import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class RepositoryMarketSynchronizationService implements MarketSynchronizationService {

    private DataSupplier dataSupplier;
    private MarketService marketService;

    @Async
    @Override
    public void synchronizeMarkets() {
        log.trace("Starting market synchronization");
        Stream.of(Stock.values())
                .map(dataSupplier::getAllStockMarkets)
                .forEach(this::saveMarkets);
        log.info("Finished market synchronization");
    }

    private void saveMarkets(MarketData marketData) {
        Stock stock = marketData.getStockExchangeName();
        Stream.of(marketData.getMarkets())
                .forEach(market -> marketService.save(market, stock));
    }

}
