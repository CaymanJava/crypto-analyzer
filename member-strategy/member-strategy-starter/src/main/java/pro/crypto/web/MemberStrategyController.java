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
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.service.MemberStrategyService;
import pro.crypto.snapshot.MemberStrategySnapshot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/member")
@AllArgsConstructor
public class MemberStrategyController {

    private final MemberStrategyService memberStrategyService;

    @GetMapping(value = "/{memberId}/strategies", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<MemberStrategySnapshot> findAll(@PathVariable("memberId") Long memberId,
                                                @Valid @NotNull MemberStrategyFindRequest request, Pageable pageable) {
        return memberStrategyService.find(memberId, request, pageable);
    }

    @GetMapping(value = "/strategy/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberStrategySnapshot findById(@PathVariable("id") Long id) {
        return memberStrategyService.findOne(id);
    }

    @PostMapping(value = "/{memberId}/strategy", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Long create(@PathVariable("memberId") Long memberId, @RequestBody MemberStrategyCreateRequest request) {
        return memberStrategyService.create(memberId, request);
    }

    @PutMapping(value = "/strategy/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberStrategySnapshot update(@PathVariable("id") Long id, @RequestBody MemberStrategyUpdateRequest request) {
        return memberStrategyService.update(id, request);
    }

}
