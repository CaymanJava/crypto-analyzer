package pro.crypto.service;

import org.springframework.stereotype.Component;
import pro.crypto.model.MemberStrategy;
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.snapshot.MemberStrategySnapshot;

@Component
public class MemberStrategyMapper {

    MemberStrategy fromCreationRequest(MemberStrategyCreateRequest request) {
        return MemberStrategy.builder()
                .marketId(request.getMarketId())
                .strategyConfiguration(request.getStrategyConfiguration())
                .drawConfiguration(request.getDrawConfiguration())
                .strategyType(request.getStrategyType())
                .timeFrame(request.getTimeFrame())
                .updateTimeUnit(request.getUpdateTimeUnit())
                .updateTimeValue(request.getUpdateTimeValue())
                .marketName(request.getMarketName())
                .stock(request.getStock())
                .strategyName(request.getStrategyName())
                .customStrategyName(request.getCustomStrategyName())
                .build();
    }

    MemberStrategySnapshot toSnapshot(MemberStrategy memberStrategy) {
        return MemberStrategySnapshot.builder()
                .id(memberStrategy.getId())
                .memberId(memberStrategy.getMemberId())
                .marketId(memberStrategy.getMarketId())
                .strategyConfiguration(memberStrategy.getStrategyConfiguration())
                .drawConfiguration(memberStrategy.getDrawConfiguration())
                .strategyType(memberStrategy.getStrategyType())
                .timeFrame(memberStrategy.getTimeFrame())
                .updateTimeUnit(memberStrategy.getUpdateTimeUnit())
                .updateTimeValue(memberStrategy.getUpdateTimeValue())
                .marketName(memberStrategy.getMarketName())
                .stock(memberStrategy.getStock())
                .strategyName(memberStrategy.getStrategyName())
                .customStrategyName(memberStrategy.getCustomStrategyName())
                .status(memberStrategy.getStatus())
                .cycleCount(memberStrategy.getCycleCount())
                .failedCount(memberStrategy.getFailedCount())
                .stoppedReason(memberStrategy.getStoppedReason())
                .nextExecutionTime(memberStrategy.getNextExecutionTime())
                .lastExecutionTime(memberStrategy.getLastExecutionTime())
                .lastSignalTickTime(memberStrategy.getLastSignalTickTime())
                .lastSignalPosition(memberStrategy.getLastSignalPosition())
                .build();
    }

}
