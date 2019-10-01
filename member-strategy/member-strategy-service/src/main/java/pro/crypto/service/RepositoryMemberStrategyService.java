package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.exception.MemberStrategyNotFoundException;
import pro.crypto.model.MemberStrategy;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.repository.MemberStrategyRepository;
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.snapshot.MemberStrategySnapshot;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static java.lang.String.format;
import static pro.crypto.model.strategy.MemberStrategyStatus.ACTIVE;
import static pro.crypto.model.strategy.MemberStrategyStatus.PAUSED;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class RepositoryMemberStrategyService implements MemberStrategyService {

    private final MemberStrategyRepository repository;
    private final MemberStrategyMapper memberStrategyMapper;
    private final ExecutionTimeService executionTimeService;

    @Override
    public Long create(Long memberId, MemberStrategyCreateRequest request) {
        log.trace("Creating member strategy {memberId: {}, marketName: {}, stock: {}, strategyName: {}, timeFrame: {}}",
                memberId, request.getMarketName(), request.getStock(), request.getStrategyName(), request.getTimeFrame());
        MemberStrategy memberStrategy = memberStrategyMapper.fromCreationRequest(request);
        memberStrategy.setMemberId(memberId);
        memberStrategy.setStatus(defineNewStrategyStatus(request));
        memberStrategy.setNextExecutionTime(defineNextExecutionTime(request));
        memberStrategy.setCycleCount(0L);
        memberStrategy.setFailedCount(0L);
        MemberStrategy savedMemberStrategy = repository.save(memberStrategy);
        log.info("Created member strategy {id: {}, memberId: {}, marketName: {}, stock: {}, strategyName: {}, timeFrame: {}}",
                savedMemberStrategy.getId(), memberId, request.getMarketName(), request.getStock(), request.getStrategyName(), request.getTimeFrame());
        return savedMemberStrategy.getId();
    }

    @Override
    public MemberStrategySnapshot update(Long strategyId, MemberStrategyUpdateRequest request) {
        log.trace("Updating member strategy {strategyId: {}, marketName: {}, stock: {}, strategyName: {}, timeFrame: {}}",
                strategyId, request.getMarketName(), request.getStock(), request.getStrategyName(), request.getTimeFrame());
        MemberStrategy memberStrategy = findStrategy(strategyId);
        memberStrategy.update(request);
        memberStrategy.setFailedCount(0L);
        log.info("Updated member strategy {strategyId: {}, marketName: {}, stock: {}, strategyName: {}, timeFrame: {}}",
                strategyId, request.getMarketName(), request.getStock(), request.getStrategyName(), request.getTimeFrame());
        return memberStrategyMapper.toSnapshot(memberStrategy);
    }

    @Override
    public Page<MemberStrategySnapshot> find(Long memberId, MemberStrategyFindRequest request, Pageable pageable) {
        log.trace("Getting member strategies {memberId: {}, request: {}}", memberId, request);
        Page<MemberStrategySnapshot> memberStrategies = repository.findAll(MemberStrategySpecifications.build(memberId, request), pageable)
                .map(memberStrategyMapper::toSnapshot);
        log.info("Getting member strategies {memberId: {}, request: {}, memberStrategiesSize: {}}", memberId, request, memberStrategies.getContent().size());
        return memberStrategies;
    }

    @Override
    public MemberStrategySnapshot findOne(Long strategyId) {
        log.trace("Getting member strategy {strategyId: {}}", strategyId);
        MemberStrategySnapshot memberStrategy = memberStrategyMapper.toSnapshot(findStrategy(strategyId));
        log.info("Got member strategy {strategyId: {}, marketName: {}, stock: {}, strategyName: {}, timeFrame: {}}",
                strategyId, memberStrategy.getMarketName(), memberStrategy.getStock(), memberStrategy.getStrategyName(), memberStrategy.getTimeFrame());
        return memberStrategy;
    }

    private LocalDateTime defineNextExecutionTime(MemberStrategyCreateRequest request) {
        return request.isImmediatelyStart()
                ? executionTimeService.calculateNextExecutionTime(request.getUpdateTimeUnit(), request.getUpdateTimeValue())
                : null;
    }

    private MemberStrategyStatus defineNewStrategyStatus(MemberStrategyCreateRequest request) {
        return request.isImmediatelyStart()
                ? ACTIVE
                : PAUSED;
    }

    private MemberStrategy findStrategy(Long strategyId) {
        return repository.findById(strategyId)
                .orElseThrow(() -> new MemberStrategyNotFoundException(format("Can not find member strategy {id: {%d}}", strategyId)));
    }

}
