package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.model.Market;
import pro.crypto.model.market.Stock;
import pro.crypto.model.market.StockMarket;
import pro.crypto.repository.MarketRepository;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.snapshot.MarketSnapshot;

import javax.transaction.Transactional;

import static java.util.Objects.isNull;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class MarketService {

    private final MarketRepository marketRepository;
    private final MarketMapper marketMapper;

    public MarketSnapshot findById(Long marketId) {
        log.trace("Getting market by id {marketId: {}}", marketId);
        MarketSnapshot marketSnapshot = marketMapper.toSnapshot(marketRepository.findOne(marketId));
        log.trace("Found market by id {marketId: {}}", marketId);
        return marketSnapshot;
    }

    public Page<MarketSnapshot> findAll(MarketFindRequest request, Pageable pageable) {
        log.trace("Searching markets {request: {}, pageable: {}}", request, pageable);
        Page<MarketSnapshot> markets = marketRepository.findAll(MarketSpecifications.build(request), pageable)
                .map(marketMapper::toSnapshot);
        log.info("Found markets {request: {}, pageable: {}, marketSize: {}}", request, pageable, markets.getContent().size());
        return markets;
    }

    public void save(StockMarket market, Stock stock) {
        log.trace("Saving or updating market {market: {}, stock: {}}", market.getMarketName(), stock);
        Market updatedMarket = marketRepository.findOneByStockAndMarketId(stock, market.getId());
        if (isNull(updatedMarket)) {
            marketRepository.save(marketMapper.toMarket(market, stock));
            log.info("Saved market {market: {}, stock: {}}", market.getMarketName(), stock);
        } else {
            updatedMarket.update(market);
            log.info("Updated market {market: {}, stock: {}}", market.getMarketName(), stock);
        }
    }

}
