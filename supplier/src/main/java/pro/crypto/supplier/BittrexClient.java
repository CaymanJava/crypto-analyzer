package pro.crypto.supplier;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.crypto.model.market.MarketData;
import pro.crypto.model.market.Stock;
import pro.crypto.model.tick.TickData;
import pro.crypto.model.tick.TimeFrame;

import java.time.LocalDateTime;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(name = "bittrex", url = "${bittrex.spider.url}")
public interface BittrexClient extends StockClient {

    @Override
    @RequestMapping(method = GET, value = "/market")
    @ResponseBody
    MarketData getAllMarkets();

    @Override
    @RequestMapping(method = GET, value = "/market/{marketName}")
    @ResponseBody
    MarketData getMarketByName(@PathVariable("marketName") String name);

    @Override
    @RequestMapping(method = GET, value = "/market/monitored")
    @ResponseBody
    MarketData getAllMonitoredMarkets();

    @Override
    @RequestMapping(method = POST, value = "/market/monitor/start/{marketId}")
    void startMonitorMarket(@PathVariable("marketId") long marketId);

    @Override
    @RequestMapping(method = POST, value = "/market/monitor/all/start")
    void startAllMonitors();

    @Override
    @RequestMapping(method = POST, value = "/market/monitor/stop/{marketId}")
    void stopMonitor(@PathVariable("marketId") long marketId);

    @Override
    @RequestMapping(method = POST, value = "/market/monitor/all/stop")
    void stopAllMonitors();

    @Override
    @RequestMapping(method = GET, value = "/tick/period/{marketId}/{timeFrame}/{period}")
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "60000")
    })
    @ResponseBody
    TickData getTicksByPeriod(@PathVariable("marketId") long marketId,
                              @PathVariable("timeFrame") TimeFrame timeFrame,
                              @PathVariable("period") int period);

    @Override
    @RequestMapping(method = GET, value = "/tick/time/{marketId}/{timeFrame}/{from}/{to}")
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "60000")
    })
    @ResponseBody
    TickData getTicksByTime(@PathVariable("marketId") long marketId,
                            @PathVariable("timeFrame") TimeFrame timeFrame,
                            @PathVariable("from") LocalDateTime from,
                            @PathVariable("to") LocalDateTime to);

    @Override
    default Stock getStock() {
        return Stock.BITTREX;
    }

}
