package pro.crypto.front.office.configuration.oauth;

import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pro.crypto.front.office.api.Identity;

import java.util.Optional;

import static java.lang.Long.parseLong;
import static java.util.Optional.ofNullable;

public class IdentityMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().equals(Identity.class)
                && methodParameter.hasParameterAnnotation(AuthenticationPrincipal.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) {
        return getAuthenticatedIdentity()
                .orElseThrow(() -> new AccessDeniedException("AuthenticationPrincipal required authentication context!"));
    }

    private Optional<Identity> getAuthenticatedIdentity() {
        return ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getName)
                .map(this::parsePrincipalNameToLong)
                .map(Identity::new);
    }

    private Long parsePrincipalNameToLong(String principalName) {
        try {
            return parseLong(principalName);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
