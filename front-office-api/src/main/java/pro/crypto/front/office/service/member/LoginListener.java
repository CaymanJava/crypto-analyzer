package pro.crypto.front.office.service.member;

import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
@AllArgsConstructor
public class LoginListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final MemberService memberService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authenticationSuccessEvent) {
        if (isNull(authenticationSuccessEvent.getAuthentication().getCredentials())) {
            saveLoginSuccess(authenticationSuccessEvent.getAuthentication());
        }
    }

    private void saveLoginSuccess(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user.getUsername().equals("clientId")) {
            return;
        }
        Long userId = Long.parseLong(user.getUsername());
        memberService.addLastLoggedIn(userId);
    }

}
