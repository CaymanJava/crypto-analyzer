package pro.crypto.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.snapshot.MemberStrategySnapshot;

public interface MemberStrategyService {

    Long create(Long memberId, MemberStrategyCreateRequest request);

    MemberStrategySnapshot update(Long strategyId, MemberStrategyUpdateRequest request);

    Page<MemberStrategySnapshot> find(Long memberId, MemberStrategyFindRequest request, Pageable pageable);

    MemberStrategySnapshot findOne(Long strategyId);

}
