package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pro.crypto.model.Member;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.snapshot.MemberSnapshot;

import static java.util.Optional.ofNullable;
import static pro.crypto.MemberStatus.ACTIVE;

@Component
@AllArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    MemberSnapshot toSnapshot(Member member) {
        return MemberSnapshot.builder()
                .id(member.getId())
                .email(member.getEmail())
                .phone(member.getPhone())
                .name(member.getName())
                .surname(member.getSurname())
                .status(member.getStatus())
                .registrationDate(member.getRegistrationDate())
                .lastLoggedIn(member.getLastLoggedIn())
                .avatarUrl(member.getAvatarUrl())
                .registerPlace(member.getRegisterPlace())
                .build();
    }

    Member fromCreationRequest(MemberCreationRequest request) {
        return Member.builder()
                .email(request.getEmail())
                .password(encodePassword(request.getPassword()))
                .phone(request.getPhone())
                .name(request.getName())
                .surname(request.getSurname())
                .status(ACTIVE)
                .build();
    }

    Member fromRegistrationRequest(MemberRegisterRequest request) {
        return Member.builder()
                .email(request.getEmail())
                .password(encodePassword(request.getPassword()))
                .phone(request.getPhone())
                .name(request.getName())
                .surname(request.getSurname())
                .avatarUrl(request.getAvatarUrl())
                .build();
    }

    private String encodePassword(String password) {
        return ofNullable(password)
                .map(passwordEncoder::encode)
                .orElse(null);
    }

}
