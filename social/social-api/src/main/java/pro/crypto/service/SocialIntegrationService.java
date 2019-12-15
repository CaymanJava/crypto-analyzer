package pro.crypto.service;

import pro.crypto.request.SocialUserAccessRequest;
import pro.crypto.snapshot.SocialAccessSnapshot;

public interface SocialIntegrationService {

    SocialAccessSnapshot processSocialAccess(SocialUserAccessRequest request);

}
