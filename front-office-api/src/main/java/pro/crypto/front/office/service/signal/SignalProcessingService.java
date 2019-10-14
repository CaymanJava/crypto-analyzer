package pro.crypto.front.office.service.signal;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.front.office.api.response.SignalResponse;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.request.SignalFindRequest;
import pro.crypto.service.MarketService;
import pro.crypto.service.SignalService;
import pro.crypto.snapshot.MarketSnapshot;
import pro.crypto.snapshot.SignalSnapshot;

import java.util.Map;
import java.util.Set;

import static java.lang.Integer.MAX_VALUE;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@AllArgsConstructor
public class SignalProcessingService {

    private final SignalService signalService;
    private final MarketService marketService;

    public Page<SignalResponse> find(Long memberId, SignalFindRequest request, Pageable pageable) {
        Page<SignalSnapshot> signals = signalService.findAll(memberId, request, pageable);
        if (isEmpty(signals.getContent())) {
            return new PageImpl<>(emptyList());
        }
        Map<Long, MarketSnapshot> markets = getMarketsMap(signals);
        return signals.map(signal -> toResponse(signal, markets.get(signal.getMarketId())));
    }

    private Map<Long, MarketSnapshot> getMarketsMap(Page<SignalSnapshot> signals) {
        Set<Long> marketIds = extractMarketIds(signals);
        return getMarketsMap(marketIds);
    }

    private Set<Long> extractMarketIds(Page<SignalSnapshot> signals) {
        return signals.getContent().stream()
                .map(SignalSnapshot::getMarketId)
                .collect(toSet());
    }

    private Map<Long, MarketSnapshot> getMarketsMap(Set<Long> marketIds) {
        return marketService.findAll(buildMarketFindRequest(marketIds), PageRequest.of(0, MAX_VALUE)).getContent().stream()
                .collect(toMap(MarketSnapshot::getId, identity()));
    }

    private MarketFindRequest buildMarketFindRequest(Set<Long> marketIds) {
        return MarketFindRequest.builder()
                .ids(marketIds)
                .build();
    }

    private SignalResponse toResponse(SignalSnapshot signal, MarketSnapshot marketSnapshot) {
        return SignalResponse.builder()
                .id(signal.getId())
                .positions(signal.getPositions())
                .marketId(signal.getMarketId())
                .memberStrategyId(signal.getMemberStrategyId())
                .logoUrl(marketSnapshot.getLogoUrl())
                .baseVolume(marketSnapshot.getBaseVolume())
                .priceDiff(marketSnapshot.getPriceDiff())
                .strategyType(signal.getStrategyType())
                .timeFrame(signal.getTimeFrame())
                .marketName(signal.getMarketName())
                .stock(signal.getStock())
                .strategyName(signal.getStrategyName())
                .customStrategyName(signal.getCustomStrategyName())
                .tickTime(signal.getTickTime())
                .creationTime(signal.getCreationTime())
                .build();
    }

}
