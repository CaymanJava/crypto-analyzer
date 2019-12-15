package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.request.SocialUserAccessRequest;
import pro.crypto.snapshot.SocialAccessSnapshot;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@FeignClient(name = "api-social", url = "${pro.crypto.entry.point.api.url}", path = "/social")
public interface SocialIntegrationProxy {

    @RequestMapping(value = "/access", method = POST)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    SocialAccessSnapshot processSocialAccess(@RequestBody SocialUserAccessRequest request);

}
