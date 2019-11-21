package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.model.market.Stock;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.snapshot.SignalSnapshot;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Validated
@FeignClient(name = "api-signals", url = "${pro.crypto.entry.point.api.url}", path = "/signals")
public interface SignalProxy {

    @RequestMapping(value = "/{memberId}", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Page<SignalSnapshot> findAll(@PathVariable("memberId") Long memberId,
                                 @RequestParam("query") String query,
                                 @RequestParam("stock") Stock stock,
                                 @RequestParam("type") StrategyType type,
                                 @RequestParam("timeFrame") TimeFrame timeFrame,
                                 Pageable pageable);

}
