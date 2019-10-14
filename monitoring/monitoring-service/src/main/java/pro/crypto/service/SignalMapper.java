package pro.crypto.service;

import org.springframework.stereotype.Component;
import pro.crypto.model.Signal;
import pro.crypto.request.SignalCreationRequest;
import pro.crypto.snapshot.SignalSnapshot;

@Component
public class SignalMapper {

    Signal fromCreationRequest(SignalCreationRequest request) {
        return Signal.builder()
                .positions(request.getPositions())
                .memberId(request.getMemberId())
                .marketId(request.getMarketId())
                .memberStrategyId(request.getMemberStrategyId())
                .strategyType(request.getStrategyType())
                .timeFrame(request.getTimeFrame())
                .marketName(request.getMarketName())
                .stock(request.getStock())
                .strategyName(request.getStrategyName())
                .customStrategyName(request.getCustomStrategyName())
                .tickTime(request.getTickTime())
                .build();
    }

    SignalSnapshot toSnapshot(Signal signal) {
        return SignalSnapshot.builder()
                .id(signal.getId())
                .positions(signal.getPositions())
                .memberId(signal.getMemberId())
                .marketId(signal.getMarketId())
                .memberStrategyId(signal.getMemberStrategyId())
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
