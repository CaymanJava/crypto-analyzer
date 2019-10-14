package pro.crypto.aktor;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.message.DecisionMakerMessage;
import pro.crypto.message.SignalSenderMessage;
import pro.crypto.model.strategy.Position;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.request.SignalCreationRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.routing.CommonRouter;
import pro.crypto.service.MemberStrategyService;
import pro.crypto.service.SignalService;
import pro.crypto.snapshot.MemberStrategySnapshot;
import pro.crypto.snapshot.SignalSnapshot;

import java.util.Collection;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
@AllArgsConstructor
public class DecisionMakerActor extends AbstractActor {

    private final MemberStrategyService memberStrategyService;
    private final SignalService signalService;
    private final CommonRouter router;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DecisionMakerMessage.class, this::makeDecision)
                .build();
    }

    private void makeDecision(DecisionMakerMessage message) {
        log.trace("Making signal decision");
        StrategyResult[] result = message.getResult();
        StrategyResult lastResult = result[result.length - 1];
        if (hasSignals(lastResult)) {
            handleStrategySignals(message, lastResult);
        }
        log.info("Made signal decision");
    }

    private void handleStrategySignals(DecisionMakerMessage message, StrategyResult lastResult) {
        if (isNeedProcessSignal(lastResult, lastResult.getPositions(), message.getMemberStrategy())) {
            processSignal(lastResult, lastResult.getPositions(), message.getMemberStrategy());
        }
    }

    private boolean isNeedProcessSignal(StrategyResult lastResult, Set<Position> positions, MemberStrategySnapshot memberStrategy) {
        return isFirstStrategySignal(memberStrategy)
                || isLastSignalTimeChangeOrEmpty(lastResult, memberStrategy)
                || isLastSignalChanged(memberStrategy, lastResult, positions);
    }

    private void processSignal(StrategyResult lastResult, Set<Position> positions, MemberStrategySnapshot memberStrategy) {
        updateStrategy(lastResult, positions, memberStrategy);
        SignalSnapshot signalSnapshot = signalService.create(buildSignalCreationRequest(lastResult, positions, memberStrategy));
        router.tell(new SignalSenderMessage(signalSnapshot, memberStrategy));
    }

    private void updateStrategy(StrategyResult lastResult, Set<Position> positions, MemberStrategySnapshot memberStrategy) {
        memberStrategyService.update(memberStrategy.getId(), buildUpdateRequest(lastResult, positions));
    }

    private SignalCreationRequest buildSignalCreationRequest(StrategyResult lastResult, Set<Position> positions, MemberStrategySnapshot memberStrategy) {
        return SignalCreationRequest.builder()
                .positions(positions)
                .memberId(memberStrategy.getMemberId())
                .marketId(memberStrategy.getMarketId())
                .memberStrategyId(memberStrategy.getId())
                .strategyType(memberStrategy.getStrategyType())
                .timeFrame(memberStrategy.getTimeFrame())
                .marketName(memberStrategy.getMarketName())
                .stock(memberStrategy.getStock())
                .strategyName(memberStrategy.getStrategyName())
                .customStrategyName(memberStrategy.getCustomStrategyName())
                .tickTime(lastResult.getTick().getTickTime())
                .build();
    }

    private boolean isLastSignalTimeChangeOrEmpty(StrategyResult lastResult, MemberStrategySnapshot memberStrategy) {
        return isNull(memberStrategy.getLastSignalTickTime()) || isLastSignalTimeDiffer(lastResult, memberStrategy);
    }

    private boolean isFirstStrategySignal(MemberStrategySnapshot memberStrategy) {
        return isNull(memberStrategy.getLastSignalPositionHash());
    }

    private boolean isLastSignalChanged(MemberStrategySnapshot memberStrategy, StrategyResult result, Set<Position> positions) {
        return nonNull(memberStrategy.getLastSignalTickTime())
                && nonNull(memberStrategy.getLastSignalPositionHash())
                && memberStrategy.getLastSignalTickTime().equals(result.getTick().getTickTime())
                && !memberStrategy.getLastSignalPositionHash().equals(positions.hashCode());
    }

    private boolean isLastSignalTimeDiffer(StrategyResult lastResult, MemberStrategySnapshot memberStrategy) {
        return nonNull(memberStrategy.getLastSignalTickTime())
                && !lastResult.getTick().getTickTime().equals(memberStrategy.getLastSignalTickTime());
    }

    private MemberStrategyUpdateRequest buildUpdateRequest(StrategyResult lastResult, Set<Position> positions) {
        return MemberStrategyUpdateRequest.builder()
                .lastSignalTickTime(lastResult.getTick().getTickTime())
                .lastSignalPositionHash(positions.hashCode())
                .build();
    }

    private boolean hasSignals(StrategyResult result) {
        return nonEmpty(result.getPositions());
    }

    private boolean nonEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

}
