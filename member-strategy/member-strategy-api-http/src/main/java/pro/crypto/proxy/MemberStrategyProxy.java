package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.MemberStrategyStatus;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.request.MemberStrategyCreateRequest;
import pro.crypto.request.MemberStrategyUpdateRequest;
import pro.crypto.snapshot.MemberStrategySnapshot;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Validated
@FeignClient(name = "api-member-strategy", url = "${pro.crypto.entry.point.api.url}", path = "/member")
public interface MemberStrategyProxy {

    @RequestMapping(value = "/{memberId}/strategy", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Page<MemberStrategySnapshot> findAll(@PathVariable("memberId") Long memberId,
                                         @RequestParam("query") String query,
                                         @RequestParam("stock") Stock stock,
                                         @RequestParam("status") MemberStrategyStatus status,
                                         @RequestParam("type") StrategyType type,
                                         @RequestParam("timeFrame") TimeFrame timeFrame,
                                         Pageable pageable);

    @RequestMapping(value = "/strategy/{id}", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    MemberStrategySnapshot findById(@PathVariable("id") Long id);

    @RequestMapping(value = "/{memberId}/strategy", method = POST)
    @ResponseStatus(HttpStatus.OK)
    Long create(@PathVariable("memberId") Long memberId, @RequestBody MemberStrategyCreateRequest request);

    @RequestMapping(value = "/strategy/{id}", method = PUT)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    MemberStrategySnapshot update(@PathVariable("id") Long id, @RequestBody MemberStrategyUpdateRequest request);

}
