package pro.crypto.front.office.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenInfo {

    private String accessToken;

    private String tokenType;

    private String refreshToken;

    private Integer expiresIn;

}
