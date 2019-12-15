package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.MemberStatus;
import pro.crypto.RegisterPlace;
import pro.crypto.request.MemberUpdateRequest;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Entity
@Table(schema = "crypto_member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Email
    @NotNull
    private String email;

    private String password;

    private String phone;

    private String name;

    private String surname;

    private String activationToken;

    private String activationPin;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private LocalDateTime registrationDate;

    private LocalDateTime activationDate;

    private LocalDateTime lastLoggedIn;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private RegisterPlace registerPlace;

    @PrePersist
    public void prePersist() {
        if (isNull(registrationDate)) {
            registrationDate = LocalDateTime.now();
        }
    }

    public void update(MemberUpdateRequest request) {
        email = ofNullable(request.getEmail()).orElse(email);
        phone = ofNullable(request.getPhone()).orElse(phone);
        name = ofNullable(request.getName()).orElse(name);
        surname = ofNullable(request.getSurname()).orElse(surname);
        status = ofNullable(request.getStatus()).orElse(status);
    }

}
