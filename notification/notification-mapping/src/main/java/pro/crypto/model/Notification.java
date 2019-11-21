package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.notification.NotificationType;
import pro.crypto.request.NotificationCreateRequest;
import pro.crypto.snapshot.NotificationSnapshot;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static javax.persistence.EnumType.STRING;

@Entity
@Table(schema = "crypto_notification")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long memberId;

    @NotNull
    private String memberName;

    private String email;

    private String phone;

    private String subject;

    @NotBlank
    private String body;

    @NotNull
    private LocalDateTime timeSent;

    @NotNull
    @Enumerated(STRING)
    private NotificationType notificationType;

    public static Notification fromRequest(NotificationCreateRequest request) {
        return Notification.builder()
                .memberId(request.getMemberId())
                .memberName(request.getMemberName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .subject(request.getSubject())
                .body(request.getBody())
                .timeSent(request.getTimeSent())
                .notificationType(request.getNotificationType())
                .build();
    }

    public NotificationSnapshot toSnapshot() {
        return NotificationSnapshot.builder()
                .id(id)
                .memberId(memberId)
                .memberName(memberName)
                .email(email)
                .phone(phone)
                .subject(subject)
                .body(body)
                .timeSent(timeSent)
                .notificationType(notificationType)
                .build();
    }

}
