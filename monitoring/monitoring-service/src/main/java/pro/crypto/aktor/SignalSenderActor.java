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
import pro.crypto.request.SmsSendRequest;
import pro.crypto.service.MailService;
import pro.crypto.service.SmsService;
import pro.crypto.snapshot.MemberStrategySnapshot;

import java.time.format.DateTimeFormatter;
import java.util.Set;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ofPattern;
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
    private final static DateTimeFormatter PATTERN = ofPattern("dd/MM/yyyy HH:mm:ss");

    private final MailService mailService;
    private final SmsService smsService;

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
        switch (destination) {
            case EMAIL:
                sendEmail(message);
                break;
            case SMS:
                sendSms(message);
                break;
            case ALL:
                sendAll(message);
                break;
            default:
                break;
        }
    }

    private void sendEmail(SignalSenderMessage message) {
        log.info("Sending signal via email {signal: {}}", message.getSignal());
        mailService.sendRawEmail(buildSignalEmail(message));
    }

    private void sendSms(SignalSenderMessage message) {
        log.info("Sending signal via SMS {signal: {}}", message.getSignal());
        smsService.sendSms(buildSms(message));
    }

    private void sendAll(SignalSenderMessage message) {
        log.info("Sending signal via all destination sources {signal: {}}", message.getSignal());
        smsService.sendSms(buildSms(message));
        mailService.sendRawEmail(buildSignalEmail(message));
    }

    private SmsSendRequest buildSms(SignalSenderMessage message) {
        return SmsSendRequest.builder()
                .memberId(message.getMemberStrategy().getMemberId())
                .body(buildBody(message))
                .build();
    }

    private RawEmailSendRequest buildSignalEmail(SignalSenderMessage message) {
        return RawEmailSendRequest.builder()
                .memberId(message.getMemberStrategy().getMemberId())
                .subject(buildMailSubject(message))
                .body(buildBody(message))
                .build();
    }

    private String buildMailSubject(SignalSenderMessage message) {
        return format("Signal from strategy %s (%s), market: %s, stock: %s",
                message.getMemberStrategy().getCustomStrategyName(),
                message.getMemberStrategy().getStrategyName(),
                message.getMemberStrategy().getMarketName(),
                message.getMemberStrategy().getStock());
    }

    private String buildBody(SignalSenderMessage message) {
        StringBuilder sb = new StringBuilder();
        MemberStrategySnapshot memberStrategy = message.getMemberStrategy();

        sb.append(format("Strategy: %s (%s)", memberStrategy.getCustomStrategyName(), memberStrategy.getStrategyName()));
        sb.append(NEW_LINE);
        sb.append(format("Stock: %s", memberStrategy.getStock()));
        sb.append(NEW_LINE);
        sb.append(format("Market: %s", memberStrategy.getMarketName()));
        sb.append(NEW_LINE);
        sb.append(format("Time frame: %s", memberStrategy.getTimeFrame().getFormattedName()));
        sb.append(NEW_LINE);
        sb.append(format("Tick time: %s", message.getSignal().getTickTime().format(PATTERN)));
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
