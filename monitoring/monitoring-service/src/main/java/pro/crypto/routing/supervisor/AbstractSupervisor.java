package pro.crypto.routing.supervisor;

import akka.actor.AbstractActor;
import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.Terminated;
import akka.japi.pf.DeciderBuilder;
import akka.routing.ActorRefRoutee;
import akka.routing.Routee;
import akka.routing.Router;
import akka.routing.SmallestMailboxRoutingLogic;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import pro.crypto.MonitoringProperties;
import pro.crypto.routing.PropsFactory;
import scala.PartialFunction;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.stream.IntStream;

import static akka.actor.SupervisorStrategy.restart;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSupervisor extends AbstractActor {

    private static final OneForOneStrategy STRATEGY = strategy();
    final PropsFactory propsFactory;
    final MonitoringProperties monitoringProperties;
    Router router;

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return STRATEGY;
    }

    @Override
    @SneakyThrows(Exception.class)
    public void preStart() {
        log.info("Starting prepare supervisor {supervisorClass: {}}", getClass());
        List<Routee> routes = createRoutes();
        router = new Router(new SmallestMailboxRoutingLogic(), routes);
        super.preStart();
        log.info("Prepared supervisor {supervisorClass: {}}", getClass());
    }

    abstract Class<? extends Actor> getChildActorClass();

    abstract Integer getChildActorsSize();

    void replaceActor(Terminated message) {
        router = router.removeRoutee(message.actor());
        router = router.addRoutee(createRoutee());
    }

    private List<Routee> createRoutes() {
        return IntStream.range(0, this.getChildActorsSize())
                .mapToObj(idx -> createRoutee())
                .collect(toList());
    }

    private static OneForOneStrategy strategy() {
        return new OneForOneStrategy(10, Duration.create("10 seconds"), deciderBuilder());
    }

    private static PartialFunction<Throwable, SupervisorStrategy.Directive> deciderBuilder() {
        return DeciderBuilder
                .match(RuntimeException.class, ex -> restart())
                .build();
    }

    private Routee createRoutee() {
        ActorRef actor = getContext().actorOf(propsFactory.props(getChildActorClass()));
        getContext().watch(actor);
        return new ActorRefRoutee(actor);
    }

}
