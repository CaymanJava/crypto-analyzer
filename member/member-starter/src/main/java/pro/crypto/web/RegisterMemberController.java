package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.service.RegisterMemberService;
import pro.crypto.snapshot.MemberSnapshot;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/member/register")
@AllArgsConstructor
public class RegisterMemberController {

    private final RegisterMemberService registerMemberService;

    @PostMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public MemberSnapshot register(@RequestBody MemberRegisterRequest request) {
        return registerMemberService.register(request);
    }

}
