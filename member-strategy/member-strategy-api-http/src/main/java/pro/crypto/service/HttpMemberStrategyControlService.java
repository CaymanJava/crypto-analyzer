package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.proxy.MemberStrategyControlProxy;
import pro.crypto.request.MemberStrategyStatusChangeRequest;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
public class HttpMemberStrategyControlService implements MemberStrategyControlService {

    private final MemberStrategyControlProxy memberStrategyControlProxy;

    @Override
    public void scheduleNextExecution(Long strategyId) {
        throw new NotImplementedException("scheduleNextExecution() is not implemented in http service");
    }

    @Override
    public void changeStatus(Long strategyId, MemberStrategyStatusChangeRequest request) {
        memberStrategyControlProxy.changeStatus(strategyId, request);
    }

    @Override
    public void noteFailedCycle(Long strategyId) {
        throw new NotImplementedException("scheduleNextExecution() is not implemented in http service");
    }

}
