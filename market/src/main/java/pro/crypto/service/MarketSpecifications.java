package pro.crypto.service;

import org.springframework.data.jpa.domain.Specification;
import pro.crypto.SpecificationsBuilder;
import pro.crypto.model.Market;
import pro.crypto.model.Market_;
import pro.crypto.request.MarketFindRequest;

import static java.util.Arrays.asList;

class MarketSpecifications {

    static Specification<Market> build(MarketFindRequest request) {
        return SpecificationsBuilder.<Market>create()
                .like(asList(
                        Market_.marketCurrency,
                        Market_.baseCurrency,
                        Market_.baseCurrencyLong,
                        Market_.marketName,
                        Market_.marketCurrencyLong),
                        request.getQuery())
                .equal(Market_.stock, request.getStock())
                .equal(Market_.active, request.getActive())
                .equal(Market_.status, request.getStatus())
                .build();
    }

}
