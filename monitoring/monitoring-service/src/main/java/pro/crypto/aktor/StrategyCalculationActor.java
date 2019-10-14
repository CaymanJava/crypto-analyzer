package pro.crypto.aktor;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.message.DecisionMakerMessage;
import pro.crypto.message.StrategyCalculationMessage;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.request.StrategyCalculationRequest;
import pro.crypto.response.StrategyResult;
import pro.crypto.routing.CommonRouter;
import pro.crypto.service.MemberStrategyControlService;
import pro.crypto.service.MemberStrategyService;
import pro.crypto.service.StrategyService;
import pro.crypto.snapshot.MemberStrategySnapshot;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
@AllArgsConstructor
public class StrategyCalculationActor extends AbstractActor {

    private final CommonRouter router;
    private final MemberStrategyService memberStrategyService;
    private final MemberStrategyControlService memberStrategyControlService;
    private final StrategyService strategyService;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StrategyCalculationMessage.class, this::calculateStrategy)
                .build();
    }

    private void calculateStrategy(StrategyCalculationMessage message) {
        log.trace("Starting calculate member strategy {message: {}}", message);
        tryCalculate(message);
        log.info("Finished calculate member strategy {message: {}}", message);
    }

    private void tryCalculate(StrategyCalculationMessage message) {
        try {
            MemberStrategySnapshot memberStrategy = memberStrategyService.findOne(message.getStrategyId());
            StrategyResult[] result = calculateStrategy(memberStrategy);
            router.tell(new DecisionMakerMessage(memberStrategy, result));
        } catch (Exception ex) {
            log.warn("Exception has been occurred during member strategy calculation {memberStrategyId: {}, exception: {}}", message.getStrategyId(), ex);
            memberStrategyControlService.noteFailedCycle(message.getStrategyId());
        }
    }

    private StrategyResult[] calculateStrategy(MemberStrategySnapshot memberStrategy) {
        return stream(strategyService.calculate(buildStrategyCalculationRequest(memberStrategy)))
                .map(result -> (StrategyResult) result)
                .toArray(StrategyResult[]::new);
    }

    private StrategyCalculationRequest buildStrategyCalculationRequest(MemberStrategySnapshot memberStrategy) {
        return StrategyCalculationRequest.builder()
                .marketId(memberStrategy.getMarketId())
                .timeFrame(memberStrategy.getTimeFrame())
                .from(defineFromDate(memberStrategy.getTimeFrame()))
                .to(now())
                .strategyType(memberStrategy.getStrategyType())
                .configuration(memberStrategy.getStrategyConfiguration())
                .build();
    }

    private LocalDateTime defineFromDate(TimeFrame timeFrame) {
        switch (timeFrame) {
            case FOUR_HOURS:
                return now().minusMonths(3L);
            case ONE_DAY:
                return now().minusMonths(4L);
            default:
                return now().minusMonths(2L);
        }
    }

}
