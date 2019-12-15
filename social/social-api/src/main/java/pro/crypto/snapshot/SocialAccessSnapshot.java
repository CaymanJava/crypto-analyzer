package pro.crypto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SocialAccessSnapshot {

    private Long memberId;

    private String name;

    private String surname;

    private String email;

    private String avatarUrl;

    private String phone;

    private boolean registrationCompleted;

}
