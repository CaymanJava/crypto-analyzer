package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import pro.crypto.model.notification.NotificationType;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString(exclude = "body")
public class NotificationCreateRequest {

    private Long memberId;

    private String memberName;

    private String email;

    private String phone;

    private String subject;

    private String body;

    private LocalDateTime timeSent;

    private NotificationType notificationType;

}
