package pro.crypto.service;

import pro.crypto.request.MemberStrategyStatusChangeRequest;

public interface MemberStrategyControlService {

    void scheduleNextExecution(Long strategyId);

    void changeStatus(Long strategyId, MemberStrategyStatusChangeRequest request);

    void noteFailedCycle(Long strategyId);

}
