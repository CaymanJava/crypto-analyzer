package pro.crypto.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.crypto.model.market.Stock;
import pro.crypto.model.market.StockMarket;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.snapshot.MarketSnapshot;

public interface MarketService {

    MarketSnapshot findById(Long marketId);

    Page<MarketSnapshot> findAll(MarketFindRequest request, Pageable pageable);

    void save(StockMarket market, Stock stock);

}
