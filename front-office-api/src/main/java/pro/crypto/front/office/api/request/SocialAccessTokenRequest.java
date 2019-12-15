package pro.crypto.front.office.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.Provider;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialAccessTokenRequest {

    private Provider provider;

    private String authCode;

}
