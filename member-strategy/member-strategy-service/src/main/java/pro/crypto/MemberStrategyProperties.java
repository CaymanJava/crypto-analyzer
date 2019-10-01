package pro.crypto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotNull;

@Data
@ConfigurationProperties(prefix = "member.strategy")
public class MemberStrategyProperties {

    @NotNull
    private Integer failedCyclesAllowedValue;

}
