package pro.crypto.message;

import lombok.Value;

@Value
public class StrategyCalculationMessage implements ActorMessage {

    private final Long strategyId;

}
