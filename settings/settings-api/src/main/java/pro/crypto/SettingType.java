package pro.crypto;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_LOGIN;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_OUTCOMING;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_PASSWORD;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_REQUIRE_SECURE_CONNECTION;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_SMTP_HOST;
import static pro.crypto.SettingKey.NOTIFICATIONS_EMAIL_SMTP_PORT;
import static pro.crypto.SettingKey.NOTIFICATIONS_SMS_API_KEY;
import static pro.crypto.SettingKey.NOTIFICATIONS_SMS_COMMAND;
import static pro.crypto.SettingKey.NOTIFICATIONS_SMS_FROM;
import static pro.crypto.SettingKey.NOTIFICATION_SMS_API_VERSION;

public enum SettingType {

    NOTIFICATION_EMAIL(newHashSet(
            NOTIFICATIONS_EMAIL_LOGIN,
            NOTIFICATIONS_EMAIL_PASSWORD,
            NOTIFICATIONS_EMAIL_SMTP_HOST,
            NOTIFICATIONS_EMAIL_SMTP_PORT,
            NOTIFICATIONS_EMAIL_REQUIRE_SECURE_CONNECTION,
            NOTIFICATIONS_EMAIL_OUTCOMING
    )),
    NOTIFICATION_SMS(newHashSet(
            NOTIFICATION_SMS_API_VERSION,
            NOTIFICATIONS_SMS_API_KEY,
            NOTIFICATIONS_SMS_COMMAND,
            NOTIFICATIONS_SMS_FROM
    ));

    private Set<SettingKey> settingKeys;

    SettingType(Set<SettingKey> settingKeys) {
        this.settingKeys = settingKeys;
    }

    public Set<SettingKey> getSettingKeys() {
        return settingKeys;
    }

}
