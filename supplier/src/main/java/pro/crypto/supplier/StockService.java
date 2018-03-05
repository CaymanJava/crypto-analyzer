package pro.crypto.supplier;


import pro.crypto.model.market.Stock;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public interface StockService {

    default Map<Stock, SpiderClient> createSpidersClientsMap(List<SpiderClient> spiderClients) {
        return spiderClients.stream()
                .collect(toMap(SpiderClient::getStock, spiderClient -> spiderClient));
    }

}
