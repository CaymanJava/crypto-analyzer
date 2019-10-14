package pro.crypto.service;

import org.springframework.data.jpa.domain.Specification;
import pro.crypto.SpecificationsBuilder;
import pro.crypto.model.MemberStrategy;
import pro.crypto.model.MemberStrategy_;
import pro.crypto.request.MemberStrategyFindRequest;

import static java.util.Arrays.asList;

class MemberStrategySpecifications {

    static Specification<MemberStrategy> build(Long memberId, MemberStrategyFindRequest request) {
        return SpecificationsBuilder.<MemberStrategy>create()
                .equal(MemberStrategy_.memberId, memberId)
                .like(asList(
                        MemberStrategy_.marketName,
                        MemberStrategy_.strategyName,
                        MemberStrategy_.customStrategyName),
                        request.getQuery())
                .equal(MemberStrategy_.stock, request.getStock())
                .equal(MemberStrategy_.status, request.getStatus())
                .equal(MemberStrategy_.strategyType, request.getType())
                .equal(MemberStrategy_.timeFrame, request.getTimeFrame())
                .in(MemberStrategy_.id, request.getIds())
                .build();
    }

}
