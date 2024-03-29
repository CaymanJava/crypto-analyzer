package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pro.crypto.model.tick.TimeFrame;
import pro.crypto.response.TickDataSnapshot;

import java.time.LocalDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Validated
@FeignClient(name = "api-ticks", url = "${pro.crypto.entry.point.api.url}", path = "/ticks")
public interface HttpTickProxy {

    @RequestMapping(value = "/time", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    TickDataSnapshot getTicksByTime(@RequestParam("marketId") Long marketId,
                               @RequestParam("timeFrame") TimeFrame timeFrame,
                               @RequestParam("from") LocalDateTime from,
                               @RequestParam("to") LocalDateTime to);

    @RequestMapping(value = "/period", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    TickDataSnapshot getTicksByPeriod(@RequestParam("marketId") Long marketId,
                                    @RequestParam("timeFrame") TimeFrame timeFrame,
                                    @RequestParam("period") Integer period);

}
