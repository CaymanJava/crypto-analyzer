package pro.crypto.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberFindRequest;
import pro.crypto.request.MemberUpdateRequest;
import pro.crypto.request.PinActivationRequest;
import pro.crypto.request.TokenActivationRequest;
import pro.crypto.snapshot.MemberSnapshot;

public interface MemberService {

    Page<MemberSnapshot> find(MemberFindRequest request, Pageable pageable);

    MemberSnapshot findById(Long id);

    Long create(MemberCreationRequest request);

    void update(Long memberId, MemberUpdateRequest request);

    Long activateByToken(TokenActivationRequest request);

    Long activateByPin(PinActivationRequest request);

}
