package pro.crypto.service;

import org.springframework.data.jpa.domain.Specification;
import pro.crypto.SpecificationsBuilder;
import pro.crypto.model.Member;
import pro.crypto.model.Member_;
import pro.crypto.request.MemberFindRequest;

import static java.util.Arrays.asList;

class MemberSpecifications {

    static Specification<Member> build(MemberFindRequest request) {
        return SpecificationsBuilder.<Member>create()
                .like(asList(
                        Member_.email,
                        Member_.phone,
                        Member_.name,
                        Member_.surname
                        ),
                        request.getQuery())
                .equal(Member_.status, request.getStatus())
                .build();
    }

}
