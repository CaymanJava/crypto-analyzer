package pro.crypto.front.office.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

}
