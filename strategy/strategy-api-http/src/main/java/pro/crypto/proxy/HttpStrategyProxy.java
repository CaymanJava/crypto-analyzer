package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.model.strategy.StrategyType;
import pro.crypto.model.tick.TimeFrame;

import java.time.LocalDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Validated
@FeignClient(name = "api-strategy", url = "${pro.crypto.entry.point.api.url}", path = "/strategies")
public interface HttpStrategyProxy {

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Object[] calculate(@RequestParam("marketId") Long marketId,
                               @RequestParam("timeFrame") TimeFrame timeFrame,
                               @RequestParam("from") LocalDateTime from,
                               @RequestParam("to") LocalDateTime to,
                               @RequestParam("strategyType") StrategyType strategyType,
                               @RequestParam("configuration") String configuration);

}
