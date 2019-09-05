package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pro.crypto.model.Member;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.snapshot.MemberSnapshot;

import static pro.crypto.MemberStatus.ACTIVE;

@Component
@AllArgsConstructor
public class MemberMapper {

    private final PasswordEncoder passwordEncoder;

    MemberSnapshot toSnapshot(Member member) {
        return MemberSnapshot.builder()
                .email(member.getEmail())
                .phone(member.getPhone())
                .name(member.getName())
                .surname(member.getSurname())
                .status(member.getStatus())
                .registrationDate(member.getRegistrationDate())
                .lastLoggedIn(member.getLastLoggedIn())
                .build();
    }

    Member fromCreationRequest(MemberCreationRequest request) {
        return Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .name(request.getName())
                .surname(request.getSurname())
                .status(ACTIVE)
                .build();
    }

    Member fromRegistrationRequest(MemberRegisterRequest request) {
        return Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .name(request.getName())
                .surname(request.getSurname())
                .build();
    }

}
