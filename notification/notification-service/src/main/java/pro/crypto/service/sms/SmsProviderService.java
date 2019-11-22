package pro.crypto.service.sms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import pro.crypto.SettingKey;
import pro.crypto.model.SmsQueue;
import pro.crypto.properties.SmsProperties;
import pro.crypto.request.NotificationCreateRequest;
import pro.crypto.request.SmsSendRequest;
import pro.crypto.service.MemberService;
import pro.crypto.service.NotificationService;
import pro.crypto.service.SettingService;
import pro.crypto.service.SmsService;
import pro.crypto.snapshot.MemberSnapshot;

import java.util.Map;

import static java.time.LocalDateTime.now;
import static pro.crypto.SettingKey.NOTIFICATIONS_SMS_API_KEY;
import static pro.crypto.SettingKey.NOTIFICATIONS_SMS_COMMAND;
import static pro.crypto.SettingKey.NOTIFICATIONS_SMS_FROM;
import static pro.crypto.SettingKey.NOTIFICATION_SMS_API_VERSION;
import static pro.crypto.SettingType.NOTIFICATION_SMS;
import static pro.crypto.model.notification.NotificationType.SMS;

@Slf4j
@Service
public class SmsProviderService implements SmsService {

    private final MessagingTemplate messagingTemplate;
    private final MemberService memberService;
    private final SmsProperties smsProperties;
    private final SettingService settingService;
    private final AlphaSmsClient smsClient;
    private final NotificationService notificationService;

    public SmsProviderService(@Qualifier("smsMessagingTemplate") MessagingTemplate messagingTemplate, MemberService memberService,
                              SmsProperties smsProperties, SettingService settingService,
                              AlphaSmsClient smsClient, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.memberService = memberService;
        this.smsProperties = smsProperties;
        this.settingService = settingService;
        this.smsClient = smsClient;
        this.notificationService = notificationService;
    }

    @Override
    public void sendSms(SmsSendRequest request) {
        log.trace("Sending sms {request: {}}", request);
        SmsQueue smsQueue = buildSmsQueue(request);
        queueSmsMessage(smsQueue);
        log.info("Sent sms {request: {}}", request);
    }

    @ServiceActivator
    public void processSms(Message<SmsQueue> message) {
        processSms(message.getPayload());
    }

    private void queueSmsMessage(SmsQueue smsQueue) {
        if (smsProperties.isQueueEnabled()) {
            messagingTemplate.send(buildMessage(smsQueue));
        } else {
            processSms(smsQueue);
        }
    }

    private void processSms(SmsQueue smsQueue) {
        String result = trySendSms(smsQueue);
        log.info("Sent sms {result: {}}", result);
        saveEmailNotification(smsQueue);
    }

    private String trySendSms(SmsQueue smsQueue) {
        try {
            Map<SettingKey, String> settings = settingService.getSettings(NOTIFICATION_SMS);
            return sendSms(smsQueue, settings);
        } catch (Exception ex) {
            log.warn("Exception has been occurred during sending sms", ex);
            return "";
        }
    }

    private String sendSms(SmsQueue smsQueue, Map<SettingKey, String> settings) {
        return smsClient.sendSms(
                settings.get(NOTIFICATION_SMS_API_VERSION), settings.get(NOTIFICATIONS_SMS_API_KEY),
                settings.get(NOTIFICATIONS_SMS_COMMAND), settings.get(NOTIFICATIONS_SMS_FROM),
                smsQueue.getPhone(), smsQueue.getBody());
    }

    private Message<SmsQueue> buildMessage(SmsQueue smsQueue) {
        return MessageBuilder
                .withPayload(smsQueue)
                .setPriority(1)
                .build();
    }

    private SmsQueue buildSmsQueue(SmsSendRequest request) {
        MemberSnapshot member = memberService.findById(request.getMemberId());
        return SmsQueue.builder()
                .memberId(member.getId())
                .memberName(member.getFullName())
                .phone(member.getPhone())
                .body(request.getBody())
                .build();
    }

    private void saveEmailNotification(SmsQueue smsQueue) {
        notificationService.create(buildNotificationCreateRequest(smsQueue));
    }

    private NotificationCreateRequest buildNotificationCreateRequest(SmsQueue smsQueue) {
        return NotificationCreateRequest.builder()
                .memberId(smsQueue.getMemberId())
                .memberName(smsQueue.getMemberName())
                .phone(smsQueue.getPhone())
                .body(smsQueue.getBody())
                .timeSent(now())
                .notificationType(SMS)
                .build();
    }

}
