package pro.crypto.front.office.api.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.front.office.api.Identity;
import pro.crypto.service.MemberService;
import pro.crypto.snapshot.MemberSnapshot;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/me")
@AllArgsConstructor
public class MeController {

    private final MemberService memberService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberSnapshot me(@AuthenticationPrincipal Identity identity) {
        return memberService.findById(identity.getMemberId());
    }

}
