package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.MemberStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberUpdateRequest {

    private String email;

    private String phone;

    private String name;

    private String surname;

    private MemberStatus status;

}
