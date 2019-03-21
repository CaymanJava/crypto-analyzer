package pro.crypto.supplier;


import pro.crypto.model.market.Stock;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public interface StockService {

    default Map<Stock, StockClient> createSpidersClientsMap(List<StockClient> stockClients) {
        return stockClients.stream()
                .collect(toMap(StockClient::getStock, stockClient -> stockClient));
    }

}
