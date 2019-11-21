package pro.crypto.front.office.api.web;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.front.office.api.Identity;
import pro.crypto.front.office.api.exception.InvalidMemberStrategyException;
import pro.crypto.front.office.api.response.MemberStrategyResponse;
import pro.crypto.front.office.service.strategy.MemberStrategyProcessingService;
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.request.MemberStrategyFindRequest;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.service.MemberStrategyService;
import pro.crypto.snapshot.MemberStrategySnapshot;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static java.lang.String.format;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/me/strategies")
@AllArgsConstructor
public class MyStrategyController {

    private final MemberStrategyService memberStrategyService;
    private final MemberStrategyProcessingService memberStrategyProcessingService;

    @GetMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<MemberStrategyResponse> findAll(@AuthenticationPrincipal Identity identity,
                                                @Valid @NotNull MemberStrategyFindRequest request,
                                                Pageable pageable) {
        return memberStrategyProcessingService.find(identity.getMemberId(), request, pageable);
    }

    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberStrategySnapshot findById(@AuthenticationPrincipal Identity identity,
                                           @PathVariable("id") Long id) {
        MemberStrategySnapshot memberStrategy = memberStrategyService.findOne(id);
        checkMemberStrategyAffiliation(identity.getMemberId(), memberStrategy);
        return memberStrategy;
    }

    @PostMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Long create(@AuthenticationPrincipal Identity identity,
                       @RequestBody MemberStrategyCreateRequest request) {
        return memberStrategyService.create(identity.getMemberId(), request);
    }

    @PutMapping(value = "/{id}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MemberStrategySnapshot update(@AuthenticationPrincipal Identity identity,
                                         @PathVariable("id") Long id,
                                         @RequestBody MemberStrategyUpdateRequest request) {
        MemberStrategySnapshot memberStrategy = memberStrategyService.findOne(id);
        checkMemberStrategyAffiliation(identity.getMemberId(), memberStrategy);
        return memberStrategyService.update(id, request);
    }

    private void checkMemberStrategyAffiliation(Long memberId, MemberStrategySnapshot memberStrategy) {
        if (!memberStrategy.getMemberId().equals(memberId)) {
            throw new InvalidMemberStrategyException(format("Member strategy (id: %d) is not belongs to member (id: %d)", memberStrategy.getId(), memberId));
        }
    }

}
