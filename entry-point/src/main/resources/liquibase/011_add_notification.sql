CREATE TABLE crypto_notification.notification
(
    id                SERIAL PRIMARY KEY,
    member_id         BIGINT                NOT NULL,
    member_name       CHARACTER VARYING(255),
    email             CHARACTER VARYING(255),
    phone             CHARACTER VARYING(255),
    subject           TEXT,
    body              TEXT                  NOT NULL,
    time_sent         TIMESTAMP             NOT NULL,
    notification_type CHARACTER VARYING(20) NOT NULL
);
