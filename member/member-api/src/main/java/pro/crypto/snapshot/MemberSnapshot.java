package pro.crypto.snapshot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.MemberStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberSnapshot {

    private String email;

    private String phone;

    private String name;

    private String surname;

    private MemberStatus status;

    private LocalDateTime registrationDate;

    private LocalDateTime lastLoggedIn;

}
