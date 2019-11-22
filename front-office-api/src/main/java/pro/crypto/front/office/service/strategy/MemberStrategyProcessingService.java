package pro.crypto.front.office.service.strategy;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.front.office.api.response.MemberStrategyResponse;
import pro.crypto.request.MarketFindRequest;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.service.MarketService;
import pro.crypto.service.MemberStrategyService;
import pro.crypto.snapshot.MarketSnapshot;
import pro.crypto.snapshot.MemberStrategySnapshot;

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
public class MemberStrategyProcessingService {

    private final MemberStrategyService memberStrategyService;
    private final MarketService marketService;

    public Page<MemberStrategyResponse> find(Long memberId, MemberStrategyFindRequest request, Pageable pageable) {
        Page<MemberStrategySnapshot> memberStrategySnapshots = memberStrategyService.find(memberId, request, pageable);
        if (isEmpty(memberStrategySnapshots.getContent())) {
            return new PageImpl<>(emptyList());
        }
        Map<Long, MarketSnapshot> markets = getMarketsMap(memberStrategySnapshots);
        return memberStrategySnapshots.map(memberStrategy -> toResponse(memberStrategy, markets.get(memberStrategy.getMarketId())));
    }

    private Map<Long, MarketSnapshot> getMarketsMap(Page<MemberStrategySnapshot> memberStrategySnapshots) {
        Set<Long> marketIds = extractMarketIds(memberStrategySnapshots);
        return getMarketsMap(marketIds);
    }

    private Set<Long> extractMarketIds(Page<MemberStrategySnapshot> memberStrategySnapshots) {
        return memberStrategySnapshots.getContent().stream()
                .map(MemberStrategySnapshot::getMarketId)
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

    private MemberStrategyResponse toResponse(MemberStrategySnapshot memberStrategy, MarketSnapshot marketSnapshot) {
        return MemberStrategyResponse.builder()
                .id(memberStrategy.getId())
                .memberId(memberStrategy.getMemberId())
                .marketId(memberStrategy.getMarketId())
                .marketName(memberStrategy.getMarketName())
                .logoUrl(marketSnapshot.getLogoUrl())
                .baseVolume(marketSnapshot.getBaseVolume())
                .priceDiff(marketSnapshot.getPriceDiff())
                .strategyType(memberStrategy.getStrategyType())
                .timeFrame(memberStrategy.getTimeFrame())
                .updateTimeUnit(memberStrategy.getUpdateTimeUnit())
                .updateTimeValue(memberStrategy.getUpdateTimeValue())
                .stock(memberStrategy.getStock())
                .strategyName(memberStrategy.getStrategyName())
                .customStrategyName(memberStrategy.getCustomStrategyName())
                .status(memberStrategy.getStatus())
                .notificationDestination(memberStrategy.getNotificationDestination())
                .build();
    }

}
