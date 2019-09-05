package pro.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "member.registration")
public class MemberRegisterProperties {

    private boolean activationProcessEnabled;

}
