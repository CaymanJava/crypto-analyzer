package pro.crypto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.model.strategy.MemberStrategyStatus;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberStrategyStatusChangeRequest {

    @NotNull
    private MemberStrategyStatus status;

    private String stoppedReason;

}
