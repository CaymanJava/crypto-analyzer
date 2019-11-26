package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.MemberStrategyProperties;
import pro.crypto.exception.MemberStrategyNotFoundException;
import pro.crypto.model.MemberStrategy;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.repository.MemberStrategyRepository;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.request.MemberStrategyStatusChangeRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
import static pro.crypto.model.strategy.MemberStrategyStatus.ACTIVE;
import static pro.crypto.model.strategy.MemberStrategyStatus.PAUSED;
import static pro.crypto.model.strategy.MemberStrategyStatus.STOPPED;

@Slf4j
@Service
@AllArgsConstructor
@Transactional
public class MemberStrategySupervisor implements MemberStrategyControlService {

    private final MemberStrategyRepository repository;
    private final ExecutionTimeService executionTimeService;
    private final MemberStrategyProperties memberStrategyProperties;

    @Override
    public void scheduleNextExecution(Set<Long> strategyIds) {
        log.trace("Scheduling next execution {strategyIdsSize: {}}", strategyIds.size());
        List<MemberStrategy> memberStrategies = findMemberStrategies(strategyIds);
        memberStrategies.forEach(this::scheduleNextExecution);
        log.info("Scheduled next execution {strategyIdsSize: {}}", strategyIds.size());
    }

    @Override
    public void changeStatus(Long memberStrategyId, MemberStrategyStatusChangeRequest request) {
        log.trace("Changing member strategy status {memberStrategyId: {}, request: {}}", memberStrategyId, request);
        MemberStrategy strategy = findStrategy(memberStrategyId);
        changeStatus(request.getStatus(), request.getStoppedReason(), strategy);
        log.info("Changed member strategy status {memberStrategyId: {}, request: {}}", memberStrategyId, request);
    }

    @Override
    public void stopMonitoring(Set<Long> marketIds, String reason) {
        log.trace("Stopping monitoring deleted markets member strategies {marketSize: {}}", marketIds);
        List<MemberStrategy> memberStrategies = findMemberStrategiesByMarketIds(marketIds);
        memberStrategies.forEach(memberStrategy -> changeStatus(STOPPED, reason, memberStrategy));
        log.info("Stopped monitoring deleted markets member strategies {}", memberStrategies.size());
    }

    @Override
    public void noteFailedCycle(Long strategyId) {
        log.trace("Noting member strategy failed execution cycle {strategyId: {}}", strategyId);
        MemberStrategy strategy = findStrategy(strategyId);
        strategy.setFailedCount(strategy.getFailedCount() + 1);
        if (strategy.getFailedCount() > memberStrategyProperties.getFailedCyclesAllowedValue()) {
            strategy.setStatus(STOPPED);
            strategy.setStoppedReason("Failed cycles allowed value was exceeded");
            log.info("Stopped member strategy after allowed failed cycles exceeded {strategyId: {}}", strategyId);
        }
    }

    @Override
    public Set<Long> getStrategyIdsForMonitoring() {
        log.trace("Getting member strategy ids for monitoring");
        Set<Long> idsForMonitoring = repository.findIdsForMonitoring(ACTIVE, now());
        log.info("Found member strategy ids for monitoring {size: {}}", idsForMonitoring.size());
        return idsForMonitoring;
    }

    private void changeStatus(MemberStrategyStatus status, String reason, MemberStrategy strategy) {
        strategy.setStatus(ofNullable(status).orElse(strategy.getStatus()));
        strategy.setStoppedReason(reason);
    }

    private List<MemberStrategy> findMemberStrategies(Set<Long> strategyIds) {
        return repository.findAll(MemberStrategySpecifications.build(null, buildFindRequestWithIds(strategyIds)));
    }

    private List<MemberStrategy> findMemberStrategiesByMarketIds(Set<Long> marketIds) {
        return repository.findAll(MemberStrategySpecifications.build(null, buildFindRequestWithMarketIds(marketIds)));
    }

    private MemberStrategyFindRequest buildFindRequestWithMarketIds(Set<Long> marketIds) {
        return MemberStrategyFindRequest.builder()
                .marketIds(marketIds)
                .statuses(newHashSet(ACTIVE, PAUSED))
                .build();
    }

    private MemberStrategyFindRequest buildFindRequestWithIds(Set<Long> strategyIds) {
        return MemberStrategyFindRequest.builder()
                .ids(strategyIds)
                .build();
    }

    private void scheduleNextExecution(MemberStrategy strategy) {
        strategy.setCycleCount(strategy.getCycleCount() + 1);
        strategy.setNextExecutionTime(defineNextExecutionTime(strategy));
        strategy.setLastExecutionTime(now());
    }

    private LocalDateTime defineNextExecutionTime(MemberStrategy strategy) {
        return executionTimeService.calculateNextExecutionTime(strategy.getUpdateTimeUnit(), strategy.getUpdateTimeValue());
    }

    private MemberStrategy findStrategy(Long strategyId) {
        return repository.findById(strategyId)
                .orElseThrow(() -> new MemberStrategyNotFoundException(format("Can not find member strategy {id: {%d}}", strategyId)));
    }

}
