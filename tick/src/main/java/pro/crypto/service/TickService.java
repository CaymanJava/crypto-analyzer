package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.model.tick.TickData;
import pro.crypto.request.GetTickByTimeRequest;
import pro.crypto.request.GetTicksByPeriodRequest;
import pro.crypto.request.TickPeriodFindRequest;
import pro.crypto.request.TickTimeFindRequest;
import pro.crypto.snapshot.MarketSnapshot;
import pro.crypto.snapshot.TickDataSnapshot;
import pro.crypto.supplier.DataSupplier;

@Slf4j
@Service
@AllArgsConstructor
public class TickService {

    private final DataSupplier dataSupplier;
    private final MarketService marketService;
    private final TickDataMapper tickDataMapper;

    public TickDataSnapshot getTicksByTime(TickTimeFindRequest request) {
        log.trace("Getting ticks by time {request: {}}", request);
        MarketSnapshot market = marketService.findById(request.getMarketId());
        TickData tickData = dataSupplier.getTicksByTime(buildGetTickByTimeRequest(request, market));
        log.info("Got ticks by time {request: {}, stock: {}, market: {}, timeFrame: {}, tickSize: {}}",
                request, tickData.getStockExchangeName(), tickData.getMarket().getMarketName(), tickData.getTimeFrame(), tickData.getTicks().length);
        return tickDataMapper.fromTickData(tickData, market);
    }

    public TickDataSnapshot getTicksByPeriod(TickPeriodFindRequest request) {
        log.trace("Getting ticks by period {request: {}}", request);
        MarketSnapshot market = marketService.findById(request.getMarketId());
        TickData tickData = dataSupplier.getTicksByPeriod(buildGetTicksByPeriodRequest(request, market));
        log.info("Got ticks by period {request: {}, stock: {}, market: {}, timeFrame: {}, tickSize: {}}",
                request, tickData.getStockExchangeName(), tickData.getMarket().getMarketName(), tickData.getTimeFrame(), tickData.getTicks().length);
        return tickDataMapper.fromTickData(tickData, market);
    }

    private GetTickByTimeRequest buildGetTickByTimeRequest(TickTimeFindRequest request, MarketSnapshot market) {
        return GetTickByTimeRequest.builder()
                .stock(market.getStock())
                .marketId(market.getMarketId())
                .timeFrame(request.getTimeFrame())
                .from(request.getFrom())
                .to(request.getTo())
                .build();
    }

    private GetTicksByPeriodRequest buildGetTicksByPeriodRequest(TickPeriodFindRequest request, MarketSnapshot market) {
        return GetTicksByPeriodRequest.builder()
                .stock(market.getStock())
                .marketId(market.getMarketId())
                .timeFrame(request.getTimeFrame())
                .period(request.getPeriod())
                .build();
    }

}
