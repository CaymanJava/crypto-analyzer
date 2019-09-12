package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.HttpMemberProxy;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberFindRequest;
import pro.crypto.request.MemberUpdateRequest;
import pro.crypto.request.PinActivationRequest;
import pro.crypto.request.TokenActivationRequest;
import pro.crypto.response.ActivationResult;
import pro.crypto.snapshot.MemberSnapshot;

@Service
@AllArgsConstructor
public class HttpMemberService implements MemberService {

    private final HttpMemberProxy memberProxy;

    @Override
    public Page<MemberSnapshot> find(MemberFindRequest request, Pageable pageable) {
        return memberProxy.findAll(request.getQuery(), request.getStatus(), pageable);
    }

    @Override
    public MemberSnapshot findById(Long id) {
        return memberProxy.findById(id);
    }

    @Override
    public Long create(MemberCreationRequest request) {
        return memberProxy.create(request);
    }

    @Override
    public void update(Long memberId, MemberUpdateRequest request) {
        memberProxy.update(memberId, request);
    }

    @Override
    public ActivationResult activateByToken(TokenActivationRequest request) {
        return memberProxy.activateByToken(request);
    }

    @Override
    public ActivationResult activateByPin(PinActivationRequest request) {
        return memberProxy.activateByPin(request);
    }

}
