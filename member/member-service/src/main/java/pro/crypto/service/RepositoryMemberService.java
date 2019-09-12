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
import pro.crypto.response.ActivationResult;
import pro.crypto.snapshot.MemberSnapshot;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static pro.crypto.MemberStatus.ACTIVE;
import static pro.crypto.response.ActivationStatus.SUCCESS;

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
        log.info("Updated member {memberId: {}, request: {}}", memberId, request);
    }

    @Override
    public ActivationResult activateByToken(TokenActivationRequest request) {
        log.trace("Activating member by token {request: {}}", request);
        ActivationResult activationResult = memberRepository.findByActivationToken(request.getToken())
                .map(this::activateMember)
                .orElseGet(ActivationResult::failed);
        log.info("Activated member by token {request: {}, activationResult: {}}", request, activationResult);
        return activationResult;
    }

    @Override
    public ActivationResult activateByPin(PinActivationRequest request) {
        log.trace("Activating member by pin {request: {}}", request);
        ActivationResult activationResult = memberRepository.findByActivationPin(request.getPin())
                .map(this::activateMember)
                .orElseGet(ActivationResult::failed);
        log.info("Activated member by pin {request: {}, activationResult: {}}", request, activationResult);
        return activationResult;
    }

    private ActivationResult activateMember(Member member) {
        member.setActivationDate(now());
        member.setActivationToken(null);
        member.setActivationPin(null);
        member.setStatus(ACTIVE);
        return ActivationResult.builder()
                .memberId(member.getId())
                .status(SUCCESS)
                .build();
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException(format("Cannot found member with ID %d", id)));
    }

}
