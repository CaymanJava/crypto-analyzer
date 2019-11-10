CREATE TABLE crypto_settings.setting
(
    key CHARACTER VARYING(255) PRIMARY KEY NOT NULL,
    value  CHARACTER VARYING NOT NULL
);

INSERT INTO crypto_settings.setting VALUES ('NOTIFICATIONS_EMAIL_LOGIN', 'onlinebanksend@gmail.com');
INSERT INTO crypto_settings.setting VALUES ('NOTIFICATIONS_EMAIL_PASSWORD', 'onlinebanksend1234');
INSERT INTO crypto_settings.setting VALUES ('NOTIFICATIONS_EMAIL_SMTP_HOST', 'smtp.gmail.com');
INSERT INTO crypto_settings.setting VALUES ('NOTIFICATIONS_EMAIL_SMTP_PORT', '587');
INSERT INTO crypto_settings.setting VALUES ('NOTIFICATIONS_EMAIL_REQUIRE_SECURE_CONNECTION', 'true');
INSERT INTO crypto_settings.setting VALUES ('NOTIFICATIONS_EMAIL_OUTCOMING', 'onlinebanksend@gmail.com');
