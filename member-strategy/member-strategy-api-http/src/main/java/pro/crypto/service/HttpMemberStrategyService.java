package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.MemberStrategyProxy;
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.snapshot.MemberStrategySnapshot;

@Service
@AllArgsConstructor
public class HttpMemberStrategyService implements MemberStrategyService {

    private final MemberStrategyProxy memberStrategyProxy;

    @Override
    public Long create(Long memberId, MemberStrategyCreateRequest request) {
        return memberStrategyProxy.create(memberId, request);
    }

    @Override
    public MemberStrategySnapshot update(Long strategyId, MemberStrategyUpdateRequest request) {
        return memberStrategyProxy.update(strategyId, request);
    }

    @Override
    public Page<MemberStrategySnapshot> find(Long memberId, MemberStrategyFindRequest request, Pageable pageable) {
        return memberStrategyProxy.findAll(memberId,
                request.getQuery(), request.getStock(), request.getStatus(),
                request.getType(), request.getTimeFrame(),
                pageable);
    }

    @Override
    public MemberStrategySnapshot findOne(Long strategyId) {
        return memberStrategyProxy.findById(strategyId);
    }

}
