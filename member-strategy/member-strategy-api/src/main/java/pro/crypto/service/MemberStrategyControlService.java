package pro.crypto.service;

import pro.crypto.request.MemberStrategyStatusChangeRequest;

import java.util.Set;

public interface MemberStrategyControlService {

    void scheduleNextExecution(Set<Long> strategyIds);

    void changeStatus(Long strategyId, MemberStrategyStatusChangeRequest request);

    void noteFailedCycle(Long strategyId);

    Set<Long> getStrategyIdsForMonitoring();

}
