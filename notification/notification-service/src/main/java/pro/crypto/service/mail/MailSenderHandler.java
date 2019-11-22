package pro.crypto.service.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import pro.crypto.SettingKey;
import pro.crypto.service.SettingService;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Properties;

import static java.lang.Integer.parseInt;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_LOGIN;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_OUTCOMING;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_PASSWORD;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_REQUIRE_SECURE_CONNECTION;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_SMTP_HOST;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_SMTP_PORT;
import static pro.crypto.SettingType.NOTIFICATION_EMAIL;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailSenderHandler {

    private final SettingService settingService;

    @Getter
    private JavaMailSender javaMailSender;

    @PostConstruct
    private void init() {
        initMailSender();
    }

    private void initMailSender() {
        Map<SettingKey, String> notificationSettings = getEmailNotificationSettings();
        initMailSender(notificationSettings);
    }

    private void initMailSender(Map<SettingKey, String> notificationSettings) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        setMailParams(notificationSettings, mailSender);
        setMailProperties(notificationSettings, mailSender);
        javaMailSender = mailSender;
    }

    private void setMailParams(Map<SettingKey, String> notificationSettings, JavaMailSenderImpl mailSender) {
        mailSender.setUsername(notificationSettings.get(NOTIFICATIONS_EMAIL_LOGIN));
        mailSender.setPassword(notificationSettings.get(NOTIFICATIONS_EMAIL_PASSWORD));
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setHost(notificationSettings.get(NOTIFICATIONS_EMAIL_SMTP_HOST));
        mailSender.setPort(parseInt(notificationSettings.get(NOTIFICATIONS_EMAIL_SMTP_PORT)));
    }

    private void setMailProperties(Map<SettingKey, String> notificationSettings, JavaMailSenderImpl mailSender) {
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", notificationSettings.get(NOTIFICATIONS_EMAIL_REQUIRE_SECURE_CONNECTION));
        props.put("mail.smtp.starttls.enable", notificationSettings.get(NOTIFICATIONS_EMAIL_REQUIRE_SECURE_CONNECTION));
        props.put("mail.smtp.from", notificationSettings.get(NOTIFICATIONS_EMAIL_OUTCOMING));
    }

    private Map<SettingKey, String> getEmailNotificationSettings() {
        return settingService.getSettings(NOTIFICATION_EMAIL);
    }

}
