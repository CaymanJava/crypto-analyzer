package pro.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pro.crypto.model.Market;
import pro.crypto.model.market.Stock;

public interface MarketRepository extends JpaRepository<Market, Long>, JpaSpecificationExecutor<Market> {

    Market findOneByStockAndMarketId(Stock stock, long marketId);

}
