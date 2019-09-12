package pro.crypto.front.office.api.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.PinActivationRequest;
import pro.crypto.request.TokenActivationRequest;
import pro.crypto.response.ActivationResult;
import pro.crypto.service.MemberService;

@RestController
@RequestMapping("/activate")
@AllArgsConstructor
public class ActivationController {

    private final MemberService memberService;

    @PutMapping(value = "/token")
    @ResponseStatus(HttpStatus.OK)
    public ActivationResult activateByToken(@RequestBody TokenActivationRequest request) {
        return memberService.activateByToken(request);
    }

    @PutMapping(value = "/pin")
    @ResponseStatus(HttpStatus.OK)
    public ActivationResult activateByPin(@RequestBody PinActivationRequest request) {
        return memberService.activateByPin(request);
    }

}
