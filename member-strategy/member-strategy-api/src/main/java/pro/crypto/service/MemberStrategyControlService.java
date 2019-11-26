package pro.crypto.service;

import pro.crypto.request.MemberStrategyStatusChangeRequest;

import java.util.Set;

public interface MemberStrategyControlService {

    void scheduleNextExecution(Set<Long> memberStrategyIds);

    void changeStatus(Long strategyId, MemberStrategyStatusChangeRequest request);

    void stopMonitoring(Set<Long> marketIds, String reason);

    void noteFailedCycle(Long memberStrategyId);

    Set<Long> getStrategyIdsForMonitoring();

}
