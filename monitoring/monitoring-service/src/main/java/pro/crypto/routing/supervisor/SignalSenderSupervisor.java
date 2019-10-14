package pro.crypto.routing.supervisor;

import akka.actor.Actor;
import akka.actor.Terminated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.MonitoringProperties;
import pro.crypto.aktor.SignalSenderActor;
import pro.crypto.message.SignalSenderMessage;
import pro.crypto.routing.PropsFactory;

import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
public class SignalSenderSupervisor extends AbstractSupervisor {

    public SignalSenderSupervisor(PropsFactory propsFactory, MonitoringProperties monitoringProperties) {
        super(propsFactory, monitoringProperties);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SignalSenderMessage.class, command -> router.route(command, getSender()))
                .match(Terminated.class, super::replaceActor)
                .build();
    }

    @Override
    Class<? extends Actor> getChildActorClass() {
        return SignalSenderActor.class;
    }

    @Override
    Integer getChildActorsSize() {
        return monitoringProperties.getSignalSenderActorsSize();
    }

}
