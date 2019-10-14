package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.MemberStrategyControlProxy;
import pro.crypto.request.MemberStrategyStatusChangeRequest;

import java.util.Set;

@Service
@AllArgsConstructor
public class HttpMemberStrategyControlService implements MemberStrategyControlService {

    private final MemberStrategyControlProxy memberStrategyControlProxy;

    @Override
    public void scheduleNextExecution(Set<Long> strategyIds) {
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

    @Override
    public Set<Long> getStrategyIdsForMonitoring() {
        throw new NotImplementedException("getStrategyIdsForMonitoring() is not implemented in http service");
    }

}
