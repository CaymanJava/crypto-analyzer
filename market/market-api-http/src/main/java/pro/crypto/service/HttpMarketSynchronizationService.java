package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpMarketProxy;

@Service
@AllArgsConstructor
public class HttpMarketSynchronizationService implements MarketSynchronizationService {

    private final HttpMarketProxy marketProxy;

    @Override
    public void synchronizeMarkets() {
        marketProxy.synchronize();
    }

}
