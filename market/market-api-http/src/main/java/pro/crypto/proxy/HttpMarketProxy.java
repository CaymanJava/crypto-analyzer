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
import pro.crypto.model.market.Status;
import pro.crypto.model.market.Stock;
import pro.crypto.snapshot.MarketSnapshot;

import java.util.Set;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Validated
@FeignClient(name = "api-market", url = "${pro.crypto.entry.point.api.url}", path = "/market")
public interface HttpMarketProxy {

    @RequestMapping(value = "/{marketId}", method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    MarketSnapshot findById(@PathVariable("marketId") Long marketId);

    @RequestMapping(method = GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    Page<MarketSnapshot> findAll(@RequestParam("query") String query,
                                 @RequestParam("stock") Stock stock,
                                 @RequestParam("active") Boolean active,
                                 @RequestParam("status") Status status,
                                 @RequestParam("ids") Set<Long> ids,
                                 Pageable pageable);

    @RequestMapping(value = "/synchronize", method = GET)
    @ResponseStatus(HttpStatus.OK)
    void synchronize();

}
