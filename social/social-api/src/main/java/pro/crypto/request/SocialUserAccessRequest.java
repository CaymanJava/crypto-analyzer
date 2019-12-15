package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.Provider;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SocialUserAccessRequest {

    private String authCode;

    private Provider provider;

}
