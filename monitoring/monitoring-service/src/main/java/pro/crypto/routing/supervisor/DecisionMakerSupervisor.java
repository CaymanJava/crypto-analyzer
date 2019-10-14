package pro.crypto.routing.supervisor;

import akka.actor.Actor;
import akka.actor.Terminated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.MonitoringProperties;
import pro.crypto.aktor.DecisionMakerActor;
import pro.crypto.message.DecisionMakerMessage;
import pro.crypto.routing.PropsFactory;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class DecisionMakerSupervisor extends AbstractSupervisor {

    public DecisionMakerSupervisor(PropsFactory propsFactory, MonitoringProperties monitoringProperties) {
        super(propsFactory, monitoringProperties);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DecisionMakerMessage.class, command -> router.route(command, getSender()))
                .match(Terminated.class, super::replaceActor)
                .build();
    }

    @Override
    Class<? extends Actor> getChildActorClass() {
        return DecisionMakerActor.class;
    }

    @Override
    Integer getChildActorsSize() {
        return monitoringProperties.getDecisionMakerActorsSize();
    }

}
