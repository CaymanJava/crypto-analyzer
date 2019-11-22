package pro.crypto.service.mail;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import pro.crypto.model.EmailQueue;
import pro.crypto.properties.MailProperties;
import pro.crypto.request.NotificationCreateRequest;
import pro.crypto.request.RawEmailSendRequest;
import pro.crypto.service.MailService;
import pro.crypto.service.MemberService;
import pro.crypto.service.NotificationService;
import pro.crypto.snapshot.MemberSnapshot;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Objects;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;
import static pro.crypto.model.notification.NotificationType.EMAIL;

@Service
@MessageEndpoint
@Slf4j
public class MailProviderService implements MailService {

    private final MessagingTemplate messagingTemplate;
    private final MemberService memberService;
    private final MailProperties mailProperties;
    private final MailSenderHandler mailHandler;
    private final NotificationService notificationService;

    public MailProviderService(@Qualifier("mailMessagingTemplate") MessagingTemplate messagingTemplate, MemberService memberService,
                               MailProperties mailProperties, MailSenderHandler mailHandler,
                               NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.memberService = memberService;
        this.mailProperties = mailProperties;
        this.mailHandler = mailHandler;
        this.notificationService = notificationService;
    }

    @Override
    public void sendRawEmail(RawEmailSendRequest request) {
        log.trace("Sending raw email {request: {}}", request);
        EmailQueue emailQueue = buildEmailQueue(request);
        queueEmailMessage(emailQueue);
        log.info("Sent raw email {request: {}}", request);
    }

    @ServiceActivator
    public void processEmailMessage(Message<EmailQueue> message) {
        sendEmail(message.getPayload());
    }

    private EmailQueue buildEmailQueue(RawEmailSendRequest request) {
        MemberSnapshot member = memberService.findById(request.getMemberId());
        return EmailQueue.builder()
                .memberId(member.getId())
                .memberName(member.getFullName())
                .email(member.getEmail())
                .subject(request.getSubject())
                .body(request.getBody())
                .html(request.isHtml())
                .build();
    }

    private void queueEmailMessage(EmailQueue emailQueue) {
        if (mailProperties.isQueueEnabled()) {
            messagingTemplate.send(buildMessage(emailQueue));
        } else {
            sendEmail(emailQueue);
        }
    }

    private Message<EmailQueue> buildMessage(EmailQueue emailQueue) {
        return MessageBuilder
                .withPayload(emailQueue)
                .setPriority(1)
                .build();
    }

    private void sendEmail(EmailQueue message) {
        buildAndSendMail(message, new File[]{});
        saveEmailNotification(message);
    }

    @SneakyThrows(MessagingException.class)
    private void buildAndSendMail(EmailQueue message, File[] attachments) {
        JavaMailSender javaMailSender = mailHandler.getJavaMailSender();
        MimeMessage mail = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mail, true);
        helper.setTo(message.getEmail());
        helper.setSubject(message.getSubject());
        helper.setText(message.getBody(), message.isHtml());
        addAttachments(helper, attachments);
        log.debug("Created email message: {from: {}, to: {}, subject: {}}", mail.getFrom(), message.getEmail(), message.getSubject());
        javaMailSender.send(mail);
    }

    private void addAttachments(MimeMessageHelper helper, File[] attachments) {
        if (nonNull(attachments) && attachments.length > 0) {
            Stream.of(attachments)
                    .filter(Objects::nonNull)
                    .forEach(attachment -> addAttachment(helper, attachment));
        }
    }

    private void addAttachment(MimeMessageHelper helper, File attachment) {
        try {
            helper.addAttachment(attachment.getName(), attachment);
        } catch (MessagingException e) {
            log.warn("Unable to add attachment into email {exception: {}}", e.getMessage());
        }
    }

    private void saveEmailNotification(EmailQueue message) {
        notificationService.create(buildNotificationCreateRequest(message));
    }

    private NotificationCreateRequest buildNotificationCreateRequest(EmailQueue message) {
        return NotificationCreateRequest.builder()
                .memberId(message.getMemberId())
                .memberName(message.getMemberName())
                .email(message.getEmail())
                .subject(message.getSubject())
                .body(message.getBody())
                .timeSent(now())
                .notificationType(EMAIL)
                .build();
    }

}
