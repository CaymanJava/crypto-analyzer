package pro.crypto.routing;

import akka.actor.Actor;
import akka.actor.Props;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class PropsFactory {

    private final ApplicationContext applicationContext;

    public Props props(Class<? extends Actor> actorClass) {
        return Props.create(ActorProducer.class, applicationContext, actorClass);
    }

}
