package pro.crypto.aktor;

import akka.actor.AbstractActor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pro.crypto.message.SignalSenderMessage;
import pro.crypto.model.notification.Destination;
import pro.crypto.model.strategy.Position;
import pro.crypto.request.RawEmailSendRequest;
import pro.crypto.service.MailService;
import pro.crypto.service.MemberService;
import pro.crypto.snapshot.MemberSnapshot;
import pro.crypto.snapshot.MemberStrategySnapshot;

import java.util.Set;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(SCOPE_PROTOTYPE)
@AllArgsConstructor
public class SignalSenderActor extends AbstractActor {

    private final static String NEW_LINE = "\n";

    private final MemberService memberService;
    private final MailService mailService;

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
                mailService.sendRawEmail(buildSignalEmail(message, member));
                break;
            case SMS:
                log.info("Sending signal via SMS {signal: {}}", message.getSignal());
                // TODO send sms
                break;
            case ALL:
                log.info("Sending signal via all destination sources {signal: {}}", message.getSignal());
                // TODO send both email and sms
                mailService.sendRawEmail(buildSignalEmail(message, member));
                break;
            default:
                break;
        }
    }

    private RawEmailSendRequest buildSignalEmail(SignalSenderMessage message, MemberSnapshot member) {
        return RawEmailSendRequest.builder()
                .memberId(member.getId())
                .subject(buildMailSubject(message))
                .body(buildMailBody(message))
                .build();
    }

    private String buildMailSubject(SignalSenderMessage message) {
        return format("Signal from strategy %s (%s), market: %s",
                message.getMemberStrategy().getCustomStrategyName(),
                message.getMemberStrategy().getStrategyName(),
                message.getMemberStrategy().getMarketName());
    }

    private String buildMailBody(SignalSenderMessage message) {
        StringBuilder sb = new StringBuilder();
        MemberStrategySnapshot memberStrategy = message.getMemberStrategy();

        sb.append(format("Strategy: %s (%s)", memberStrategy.getCustomStrategyName(), memberStrategy.getStrategyName()));
        sb.append(NEW_LINE);
        sb.append(format("Time frame: %s", memberStrategy.getTimeFrame()));
        sb.append(NEW_LINE);
        sb.append(format("Stock: %s", memberStrategy.getStock()));
        sb.append(NEW_LINE);
        sb.append(format("Market: %s", memberStrategy.getMarketName()));
        sb.append(NEW_LINE);
        sb.append(format("Signals: %s", getSignals(message.getSignal().getPositions())));

        return sb.toString();
    }

    private String getSignals(Set<Position> positions) {
        return ofNullable(positions)
                .map(this::extractSignals)
                .orElse("");
    }

    private String extractSignals(Set<Position> positions) {
        return positions.stream()
                .map(Position::toString)
                .collect(joining(", "));
    }

}
