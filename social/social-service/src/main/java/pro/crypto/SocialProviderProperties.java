package pro.crypto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SocialProviderProperties {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;

    @NotBlank
    private String redirectUrl;

}
