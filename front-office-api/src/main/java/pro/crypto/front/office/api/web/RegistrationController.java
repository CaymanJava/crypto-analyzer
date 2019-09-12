package pro.crypto.front.office.api.web;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.MemberRegisterRequest;
import pro.crypto.service.RegisterMemberService;
import pro.crypto.snapshot.MemberSnapshot;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegisterMemberService registerMemberService;

    @PostMapping(consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public MemberSnapshot register(@Valid @RequestBody MemberRegisterRequest request) {
        return registerMemberService.register(request);
    }

}
