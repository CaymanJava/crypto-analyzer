package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.crypto.MemberRegisterProperties;
import pro.crypto.MemberStatus;
import pro.crypto.model.Member;
import pro.crypto.repository.MemberRepository;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.snapshot.MemberSnapshot;

import static pro.crypto.MemberStatus.ACTIVE;
import static pro.crypto.MemberStatus.REGISTER_NOT_COMPLETED;
import static pro.crypto.RegisterPlace.SOCIAL;
import static pro.crypto.RegisterPlace.WEB;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class RepositoryRegisterMemberService implements RegisterMemberService {

    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper;
    private final MemberRegisterProperties memberRegisterProperties;

    @Override
    public MemberSnapshot register(MemberRegisterRequest request) {
        log.trace("Registering member {request: {}}", request);
        Member member = memberMapper.fromRegistrationRequest(request);
        member.setStatus(defineMemberStatus());
        member.setRegisterPlace(WEB);
        Member savedMember = memberRepository.save(member);
        continueRegistrationProcess(savedMember);
        log.info("Registered member {request: {}, memberId: {}", request, savedMember.getId());
        return memberMapper.toSnapshot(savedMember);
    }

    @Override
    public MemberSnapshot registerSocial(MemberRegisterRequest request) {
        log.trace("Registering social member {request: {}}", request);
        Member member = memberMapper.fromRegistrationRequest(request);
        member.setStatus(REGISTER_NOT_COMPLETED);
        member.setRegisterPlace(SOCIAL);
        Member savedMember = memberRepository.save(member);
        log.info("Registered social member {request: {}, memberId: {}", request, savedMember.getId());
        return memberMapper.toSnapshot(savedMember);
    }

    private MemberStatus defineMemberStatus() {
        return memberRegisterProperties.isActivationProcessEnabled()
                ? REGISTER_NOT_COMPLETED
                : ACTIVE;
    }

    private void continueRegistrationProcess(Member member) {
        if (member.getStatus() == REGISTER_NOT_COMPLETED && member.getRegisterPlace() != SOCIAL) {
            // TODO generate url, token and send email
        }
    }

}
