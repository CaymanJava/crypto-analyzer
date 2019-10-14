package pro.crypto.routing;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pro.crypto.message.ActorMessage;
import pro.crypto.message.DecisionMakerMessage;
import pro.crypto.message.SignalSenderMessage;
import pro.crypto.message.StrategyCalculationMessage;
import pro.crypto.message.StrategyMonitoringMessage;
import pro.crypto.routing.supervisor.DecisionMakerSupervisor;
import pro.crypto.routing.supervisor.SignalSenderSupervisor;
import pro.crypto.routing.supervisor.StrategyCalculationSupervisor;
import pro.crypto.routing.supervisor.StrategyMonitoringSupervisor;

import javax.annotation.PostConstruct;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.API.run;
import static io.vavr.Predicates.instanceOf;

@Component
@RequiredArgsConstructor
public class CommonRouter {

    private final ActorSystem system;
    private final PropsFactory propsFactory;

    private ActorRef strategyMonitoringSupervisor;
    private ActorRef strategyCalculationSupervisor;
    private ActorRef decisionMakerSupervisor;
    private ActorRef signalSenderSupervisor;

    public void tell(ActorMessage message) {
        Match(message).of(
                Case($(instanceOf(StrategyMonitoringMessage.class)), run(() -> sendToStrategyMonitoringSupervisor(message))),
                Case($(instanceOf(StrategyCalculationMessage.class)), run(() -> sendToStrategyCalculationSupervisor(message))),
                Case($(instanceOf(DecisionMakerMessage.class)), run(() -> sendToDecisionMakerSupervisor(message))),
                Case($(instanceOf(SignalSenderMessage.class)), run(() -> sendToSignalSenderSupervisor(message)))
        );
    }

    private void sendToStrategyMonitoringSupervisor(ActorMessage message) {
        strategyMonitoringSupervisor.tell(message, ActorRef.noSender());
    }

    private void sendToStrategyCalculationSupervisor(ActorMessage message) {
        strategyCalculationSupervisor.tell(message, ActorRef.noSender());
    }

    private void sendToDecisionMakerSupervisor(ActorMessage message) {
        decisionMakerSupervisor.tell(message, ActorRef.noSender());
    }

    private void sendToSignalSenderSupervisor(ActorMessage message) {
        signalSenderSupervisor.tell(message, ActorRef.noSender());
    }

    @PostConstruct
    private void init() {
        strategyMonitoringSupervisor = system.actorOf(propsFactory.props(StrategyMonitoringSupervisor.class));
        strategyCalculationSupervisor = system.actorOf(propsFactory.props(StrategyCalculationSupervisor.class));
        decisionMakerSupervisor = system.actorOf(propsFactory.props(DecisionMakerSupervisor.class));
        signalSenderSupervisor = system.actorOf(propsFactory.props(SignalSenderSupervisor.class));
    }

}
