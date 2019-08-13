package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpTickProxy;
import pro.crypto.request.TickPeriodFindRequest;
import pro.crypto.request.TickTimeFindRequest;
import pro.crypto.response.TickDataSnapshot;

@Service
@AllArgsConstructor
public class HttpTickService implements TickService {

    private final HttpTickProxy tickProxy;

    @Override
    public TickDataSnapshot getTicksByTime(TickTimeFindRequest request) {
        return tickProxy.getTicksByTime(request.getMarketId(), request.getTimeFrame(),
                request.getFrom(), request.getTo());
    }

    @Override
    public TickDataSnapshot getTicksByPeriod(TickPeriodFindRequest request) {
        return tickProxy.getTicksByPeriod(request.getMarketId(), request.getTimeFrame(), request.getPeriod());
    }

}
