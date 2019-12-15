package pro.crypto.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.crypto.Provider;
import pro.crypto.access.SocialAccessProvider;
import pro.crypto.model.MemberSocialAccess;
import pro.crypto.repository.MemberSocialAccessRepository;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.request.SocialUserAccessRequest;
import pro.crypto.snapshot.MemberSnapshot;
import pro.crypto.snapshot.SocialAccessSnapshot;
import pro.crypto.snapshot.SocialUser;
import pro.crypto.user.SocialUserProvider;

import java.util.Map;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static pro.crypto.MemberStatus.ACTIVE;

@Slf4j
@Service
@Transactional
public class SocialIntegrationProvider implements SocialIntegrationService {

    private final RegisterMemberService registerMemberService;
    private final MemberService memberService;
    private final MemberSocialAccessRepository memberSocialAccessRepository;
    private final Map<Provider, SocialAccessProvider> socialAccessProviders;
    private final Map<Provider, SocialUserProvider> socialUserProviders;

    public SocialIntegrationProvider(RegisterMemberService registerMemberService,
                                     MemberSocialAccessRepository memberSocialAccessRepository, MemberService memberService,
                                     Set<SocialAccessProvider> socialAccessProviders, Set<SocialUserProvider> socialUserProviders) {
        this.registerMemberService = registerMemberService;
        this.memberSocialAccessRepository = memberSocialAccessRepository;
        this.memberService = memberService;
        this.socialAccessProviders = getSocialAccessProvidersMap(socialAccessProviders);
        this.socialUserProviders = getSocialUserProvidersMap(socialUserProviders);
    }

    @Override
    public SocialAccessSnapshot processSocialAccess(SocialUserAccessRequest request) {
        log.trace("Processing social user access {request: {}}", request);
        AccessGrant accessGrant = getAccessGrant(request);
        SocialUser socialUser = getSocialUser(accessGrant, request);
        MemberSocialAccess memberSocialAccess = getMemberSocialAccess(socialUser, request);
        SocialAccessSnapshot socialAccessSnapshot = getSocialAccess(memberSocialAccess, socialUser);
        log.trace("Got social access {socialAccessSnapshot: {}}", socialAccessSnapshot);
        return socialAccessSnapshot;
    }

    private Map<Provider, SocialUserProvider> getSocialUserProvidersMap(Set<SocialUserProvider> socialUserProviders) {
        return socialUserProviders.stream()
                .collect(toMap(SocialUserProvider::getProvider, identity()));
    }

    private Map<Provider, SocialAccessProvider> getSocialAccessProvidersMap(Set<SocialAccessProvider> socialAccessProviders) {
        return socialAccessProviders.stream()
                .collect(toMap(SocialAccessProvider::getProvider, identity()));
    }

    private AccessGrant getAccessGrant(SocialUserAccessRequest request) {
        return socialAccessProviders.get(request.getProvider())
                .getAccessGrant(request.getAuthCode());
    }

    private SocialUser getSocialUser(AccessGrant accessGrant, SocialUserAccessRequest request) {
        return socialUserProviders.get(request.getProvider())
                .getUser(accessGrant.getAccessToken());
    }

    private MemberSocialAccess getMemberSocialAccess(SocialUser socialUser, SocialUserAccessRequest request) {
        return memberSocialAccessRepository.findFirstBySocialIdAndProvider(socialUser.getSocialId(), request.getProvider())
                .orElseGet(() -> createMemberSocialAccess(socialUser, request));
    }

    private MemberSocialAccess createMemberSocialAccess(SocialUser socialUser, SocialUserAccessRequest request) {
        return memberSocialAccessRepository.save(buildMemberSocialAccess(socialUser, request));
    }

    private MemberSocialAccess buildMemberSocialAccess(SocialUser socialUser, SocialUserAccessRequest request) {
        return MemberSocialAccess.builder()
                .socialId(socialUser.getSocialId())
                .provider(request.getProvider())
                .build();
    }

    private SocialAccessSnapshot getSocialAccess(MemberSocialAccess memberSocialAccess, SocialUser socialUser) {
        MemberSnapshot memberSnapshot = getMember(memberSocialAccess, socialUser);
        return buildSocialAccess(memberSnapshot);
    }

    private MemberSnapshot getMember(MemberSocialAccess memberSocialAccess, SocialUser socialUser) {
        return ofNullable(memberSocialAccess.getMemberId())
                .map(memberService::findById)
                .orElseGet(() -> registerSocialMember(memberSocialAccess, socialUser));
    }

    private MemberSnapshot registerSocialMember(MemberSocialAccess memberSocialAccess, SocialUser socialUser) {
        MemberSnapshot memberSnapshot = registerMemberService.registerSocial(buildMemberRegisterRequest(socialUser));
        memberSocialAccess.setMemberId(memberSnapshot.getId());
        return memberSnapshot;
    }

    private MemberRegisterRequest buildMemberRegisterRequest(SocialUser socialUser) {
        return MemberRegisterRequest.builder()
                .email(socialUser.getEmail())
                .name(socialUser.getName())
                .surname(socialUser.getSurname())
                .avatarUrl(socialUser.getAvatarUrl())
                .build();
    }

    private SocialAccessSnapshot buildSocialAccess(MemberSnapshot memberSnapshot) {
        return SocialAccessSnapshot.builder()
                .memberId(memberSnapshot.getId())
                .name(memberSnapshot.getName())
                .surname(memberSnapshot.getSurname())
                .email(memberSnapshot.getEmail())
                .avatarUrl(memberSnapshot.getAvatarUrl())
                .phone(memberSnapshot.getPhone())
                .registrationCompleted(memberSnapshot.getStatus() == ACTIVE)
                .build();
    }

}
