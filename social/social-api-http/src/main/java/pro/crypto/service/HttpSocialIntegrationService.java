package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.proxy.SocialIntegrationProxy;
import pro.crypto.request.SocialUserAccessRequest;
import pro.crypto.snapshot.SocialAccessSnapshot;

@Service
@AllArgsConstructor
public class HttpSocialIntegrationService implements SocialIntegrationService {

    private final SocialIntegrationProxy socialIntegrationProxy;

    @Override
    public SocialAccessSnapshot processSocialAccess(SocialUserAccessRequest request) {
        return socialIntegrationProxy.processSocialAccess(request);
    }

}
