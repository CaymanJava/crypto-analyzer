package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.crypto.MemberStrategyProperties;
import pro.crypto.exception.MemberStrategyNotFoundException;
import pro.crypto.model.MemberStrategy;
import pro.crypto.repository.MemberStrategyRepository;
import pro.crypto.request.MemberStrategyStatusChangeRequest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Optional.ofNullable;
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
    public void scheduleNextExecution(Long strategyId) {
        log.trace("Scheduling next execution {strategyId: {}}", strategyId);
        MemberStrategy strategy = findStrategy(strategyId);
        strategy.setCycleCount(strategy.getCycleCount() + 1);
        strategy.setNextExecutionTime(defineNextExecutionTime(strategy));
        strategy.setLastExecutionTime(now());
        log.info("Scheduled next execution {strategyId: {}}", strategyId);
    }

    @Override
    public void changeStatus(Long strategyId, MemberStrategyStatusChangeRequest request) {
        log.trace("Changing member strategy status {strategyId: {}, request: {}}", strategyId, request);
        MemberStrategy strategy = findStrategy(strategyId);
        strategy.setStatus(ofNullable(request.getStatus()).orElse(strategy.getStatus()));
        strategy.setStoppedReason(request.getStoppedReason());
        log.trace("Changed member strategy status {strategyId: {}, request: {}}", strategyId, request);
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

    private LocalDateTime defineNextExecutionTime(MemberStrategy strategy) {
        return executionTimeService.calculateNextExecutionTime(strategy.getUpdateTimeUnit(), strategy.getUpdateTimeValue());
    }

    private MemberStrategy findStrategy(Long strategyId) {
        return repository.findById(strategyId)
                .orElseThrow(() -> new MemberStrategyNotFoundException(format("Can not find member strategy {id: {%d}}", strategyId)));
    }

}
