package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pro.crypto.model.market.Stock;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Validated
@FeignClient(name = "api-stock-monitoring", url = "${pro.crypto.entry.point.api.url}", path = "/stock/monitoring")
public interface HttpStockMonitoringProxy {

    @RequestMapping(method = GET, value = "/start/{stock}/market/{marketId}")
    void startMonitorStockMarket(@PathVariable("stock") Stock stock, @PathVariable("marketId") Long marketId);

    @RequestMapping(method = GET, value = "/start/{stock}")
    void startAllStockMonitors(@PathVariable("stock") Stock stock);

    @RequestMapping(method = GET, value = "/stop/{stock}/market/{marketId}")
    void stopMonitorStockMarket(@PathVariable("stock") Stock stock, @PathVariable("marketId") Long marketId);

    @RequestMapping(method = GET, value = "/stop/{stock}")
    void stopAllStockMonitors(@PathVariable("stock") Stock stock);

}
