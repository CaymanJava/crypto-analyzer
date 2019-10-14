package pro.crypto.message;

import lombok.Value;
import pro.crypto.snapshot.MemberStrategySnapshot;
import pro.crypto.snapshot.SignalSnapshot;

@Value
public class SignalSenderMessage implements ActorMessage {

    private final SignalSnapshot signal;

    private final MemberStrategySnapshot memberStrategy;

}
