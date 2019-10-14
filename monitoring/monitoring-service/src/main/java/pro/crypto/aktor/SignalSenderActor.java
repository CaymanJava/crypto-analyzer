package pro.crypto.aktor;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.message.SignalSenderMessage;
import pro.crypto.model.notification.Destination;
import pro.crypto.service.MemberService;
import pro.crypto.snapshot.MemberSnapshot;

import static java.util.Objects.isNull;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
@AllArgsConstructor
public class SignalSenderActor extends AbstractActor {

    private final MemberService memberService;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SignalSenderMessage.class, this::sendSignal)
                .build();
    }

    private void sendSignal(SignalSenderMessage message) {
        log.info("Sending strategy signal {message: {}}", message);
        Destination destination = message.getMemberStrategy().getNotificationDestination();

        if (isNull(destination)) {
            log.warn("Notification destination is not defined");
            return;
        }

        sendSignalToMember(message, destination);
    }

    private void sendSignalToMember(SignalSenderMessage message, Destination destination) {
        MemberSnapshot member = memberService.findById(message.getMemberStrategy().getMemberId());
        switch (destination) {
            case EMAIL:
                log.info("Sending signal via email {signal: {}}", message.getSignal());
                // TODO send email
                break;
            case SMS:
                log.info("Sending signal via SMS {signal: {}}", message.getSignal());
                // TODO send sms
                break;
            case ALL:
                log.info("Sending signal via all destination sources {signal: {}}", message.getSignal());
                // TODO send both email and sms
                break;
            default:
                break;
        }
    }

}
