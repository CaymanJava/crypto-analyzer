package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpMarketProxy;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.snapshot.MarketSnapshot;

@Service
@AllArgsConstructor
public class HttpMarketService implements MarketService {

    private final HttpMarketProxy marketProxy;

    @Override
    public MarketSnapshot findById(Long marketId) {
        return marketProxy.findById(marketId);
    }

    @Override
    public Page<MarketSnapshot> findAll(MarketFindRequest request, Pageable pageable) {
        return marketProxy.findAll(request.getQuery(), request.getStock(),
                request.getActive(), request.getStatus(), pageable);
    }

}
