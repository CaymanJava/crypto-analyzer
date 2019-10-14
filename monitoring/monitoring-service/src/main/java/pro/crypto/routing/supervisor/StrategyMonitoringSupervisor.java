package pro.crypto.routing.supervisor;

import akka.actor.Actor;
import akka.actor.Terminated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.MonitoringProperties;
import pro.crypto.aktor.StrategyMonitoringActor;
import pro.crypto.message.StrategyMonitoringMessage;
import pro.crypto.routing.PropsFactory;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class StrategyMonitoringSupervisor extends AbstractSupervisor {

    public StrategyMonitoringSupervisor(PropsFactory propsFactory, MonitoringProperties monitoringProperties) {
        super(propsFactory, monitoringProperties);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StrategyMonitoringMessage.class, command -> router.route(command, getSender()))
                .match(Terminated.class, super::replaceActor)
                .build();
    }

    @Override
    Class<? extends Actor> getChildActorClass() {
        return StrategyMonitoringActor.class;
    }

    @Override
    Integer getChildActorsSize() {
        return monitoringProperties.getStrategyMonitoringActorsSize();
    }

}
