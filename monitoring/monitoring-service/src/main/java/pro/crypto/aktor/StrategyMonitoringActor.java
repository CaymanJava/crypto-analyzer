package pro.crypto.aktor;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.message.StrategyCalculationMessage;
import pro.crypto.message.StrategyMonitoringMessage;
import pro.crypto.routing.CommonRouter;
import pro.crypto.service.MemberStrategyControlService;

import java.util.Set;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;
import static pro.crypto.helper.CollectionHelper.nonEmpty;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
@AllArgsConstructor
public class StrategyMonitoringActor extends AbstractActor {

    private final MemberStrategyControlService memberStrategyControlService;
    private final CommonRouter router;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StrategyMonitoringMessage.class, (ignoredCommand) -> startMonitoring())
                .build();
    }

    private void startMonitoring() {
        log.trace("Prepare strategy ids for monitoring");
        Set<Long> strategyIdsForMonitoring = memberStrategyControlService.getStrategyIdsForMonitoring();
        if (nonEmpty(strategyIdsForMonitoring)) {
            memberStrategyControlService.scheduleNextExecution(strategyIdsForMonitoring);
            strategyIdsForMonitoring.forEach(this::calculate);
            log.info("Sent strategy ids to calculation {strategyIdsSize: {}}", strategyIdsForMonitoring.size());
        }
    }

    private void calculate(Long strategyId) {
        router.tell(new StrategyCalculationMessage(strategyId));
    }

}
