package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.SocialUserAccessRequest;
import pro.crypto.service.SocialIntegrationService;
import pro.crypto.snapshot.SocialAccessSnapshot;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/social")
@AllArgsConstructor
public class SocialIntegrationController {

    private SocialIntegrationService socialIntegrationService;

    @PostMapping(value = "/access", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SocialAccessSnapshot processSocialAccess(@RequestBody SocialUserAccessRequest request) {
        return socialIntegrationService.processSocialAccess(request);
    }

}
