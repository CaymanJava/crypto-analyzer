package pro.crypto.front.office.service.member;

import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.crypto.front.office.api.exception.MemberBlockedException;
import pro.crypto.front.office.api.exception.RegisterNotCompletedException;
import pro.crypto.front.office.repository.Member;
import pro.crypto.front.office.repository.MemberRepository;

import java.util.Optional;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Optional.of;

@Service
@AllArgsConstructor
@Transactional
public class RepositoryMemberService implements MemberService, UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrId) throws UsernameNotFoundException {
        return parseId(emailOrId)
                .map(memberRepository::findById)
                .orElse(memberRepository.findByEmailIgnoreCase(emailOrId))
                .map(this::mapMemberToAuthorizedUser)
                .orElseThrow(() -> new UsernameNotFoundException(format("Cannot find member by email or id {emailOrId: {%s}}", emailOrId)));
    }

    @Override
    public void addLastLoggedIn(Long memberId) {
        memberRepository.findById(memberId).ifPresent(member -> member.setLastLoggedIn(now()));
    }

    private Optional<Long> parseId(String loginOrEmailOrId) {
        try {
            return of(Long.parseLong(loginOrEmailOrId));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    private UserDetails mapMemberToAuthorizedUser(Member member) {
        switch (member.getStatus()) {
            case BLOCKED:
                throw new MemberBlockedException("Member is blocked by administration");
            case REGISTER_NOT_COMPLETED:
                throw new RegisterNotCompletedException("Member registration is not completed");
            default:
                return buildAuthorizedUser(member);
        }
    }

    private User buildAuthorizedUser(Member member) {
        return new User(member.getId().toString(), member.getPassword(),
                true, true,
                true, true,
                emptyList());
    }

}
