package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.request.MemberCreationRequest;
import pro.crypto.request.MemberFindRequest;
import pro.crypto.request.MemberUpdateRequest;
import pro.crypto.request.PinActivationRequest;
import pro.crypto.request.TokenActivationRequest;
import pro.crypto.service.MemberService;
import pro.crypto.snapshot.MemberSnapshot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<MemberSnapshot> findAll(@Valid @NotNull MemberFindRequest request, Pageable pageable) {
        return memberService.find(request, pageable);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberSnapshot findById(@PathVariable("id") Long id) {
        return memberService.findById(id);
    }

    @PostMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Long create(@RequestBody MemberCreationRequest request) {
        return memberService.create(request);
    }

    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") Long id, @RequestBody MemberUpdateRequest request) {
        memberService.update(id, request);
    }

    @PutMapping(value = "/activate/token")
    @ResponseStatus(HttpStatus.OK)
    public Long activateByToken(@RequestBody TokenActivationRequest request) {
        return memberService.activateByToken(request);
    }

    @PutMapping(value = "/activate/pin")
    @ResponseStatus(HttpStatus.OK)
    public Long activateByPin(@RequestBody PinActivationRequest request) {
        return memberService.activateByPin(request);
    }

}
