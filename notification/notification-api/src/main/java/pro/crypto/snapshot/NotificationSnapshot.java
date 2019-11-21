package pro.crypto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.notification.NotificationType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationSnapshot {

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
    private NotificationType notificationType;

}
