package pro.crypto.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Value
@Builder
public class SocialUser {

    private String socialId;

    private String name;

    private String surname;

    private String email;

    private String avatarUrl;

}
