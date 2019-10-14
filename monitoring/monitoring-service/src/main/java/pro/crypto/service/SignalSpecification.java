package pro.crypto.service;

import org.springframework.data.jpa.domain.Specification;
import pro.crypto.SpecificationsBuilder;
import pro.crypto.model.Signal;
import pro.crypto.model.Signal_;
import pro.crypto.request.SignalFindRequest;

import static java.util.Arrays.asList;

public class SignalSpecification {

    static Specification<Signal> build(Long memberId, SignalFindRequest request) {
        return SpecificationsBuilder.<Signal>create()
                .equal(Signal_.memberId, memberId)
                .like(asList(
                        Signal_.marketName,
                        Signal_.strategyName,
                        Signal_.customStrategyName),
                        request.getQuery())
                .equal(Signal_.stock, request.getStock())
                .equal(Signal_.strategyType, request.getType())
                .equal(Signal_.timeFrame, request.getTimeFrame())
                .build();
    }

}
