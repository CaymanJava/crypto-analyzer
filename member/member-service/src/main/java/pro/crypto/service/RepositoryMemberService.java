package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.crypto.exception.MemberNotFoundException;
import pro.crypto.model.Member;
import pro.crypto.repository.MemberRepository;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberFindRequest;
import pro.crypto.request.MemberUpdateRequest;
import pro.crypto.request.PinActivationRequest;
import pro.crypto.request.TokenActivationRequest;
import pro.crypto.snapshot.MemberSnapshot;

import static java.lang.String.format;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class RepositoryMemberService implements MemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;

    @Override
    public Page<MemberSnapshot> find(MemberFindRequest request, Pageable pageable) {
        log.trace("Searching members {request: {}}", request);
        Page<MemberSnapshot> members = memberRepository.findAll(MemberSpecifications.build(request), pageable).map(memberMapper::toSnapshot);
        log.info("Found members {request:{}, memberSize: {}}", request, members.getContent().size());
        return members;
    }

    @Override
    public MemberSnapshot findById(Long id) {
        log.trace("Searching member {id: {}}", id);
        MemberSnapshot memberSnapshot = memberMapper.toSnapshot(findMemberById(id));
        log.info("Found member {id: {}}", id);
        return memberSnapshot;
    }

    @Override
    public Long create(MemberCreationRequest request) {
        log.trace("Creating member {request: {}}", request);
        Member savedMember = memberRepository.save(memberMapper.fromCreationRequest(request));
        log.info("Created member {request: {}, memberId: {}}", request, savedMember.getId());
        return savedMember.getId();
    }

    @Override
    public void update(Long memberId, MemberUpdateRequest request) {
        log.trace("Updating member {memberId: {}, request: {}}", memberId, request);
        Member member = findMemberById(memberId);
        member.update(request);
        log.trace("Updated member {memberId: {}, request: {}}", memberId, request);
    }

    @Override
    public Long activateByToken(TokenActivationRequest request) {
        // TODO
        return null;
    }

    @Override
    public Long activateByPin(PinActivationRequest request) {
        // TODO
        return null;
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(format("Can not found member with ID %d", id)));
    }

}
