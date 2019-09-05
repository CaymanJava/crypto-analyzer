package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberRegisterRequest {

    private String email;

    private String password;

    private String phone;

    private String name;

    private String surname;

}
