package pro.crypto.message;

import lombok.Value;
import pro.crypto.response.StrategyResult;
import pro.crypto.snapshot.MemberStrategySnapshot;

@Value
public class DecisionMakerMessage implements ActorMessage {

    private final MemberStrategySnapshot memberStrategy;

    private final StrategyResult[] result;

}
