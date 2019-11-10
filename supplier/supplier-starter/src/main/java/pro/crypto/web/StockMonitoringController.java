package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.model.market.Stock;
import pro.crypto.service.StockMonitoringService;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/stock/monitoring")
@AllArgsConstructor
public class StockMonitoringController {

    private final StockMonitoringService stockMonitoringService;

    @GetMapping(value = "/start/{stock}/market/{marketId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void startMonitorStockMarket(@PathVariable("stock") Stock stock, @PathVariable("marketId") Long marketId) {
        stockMonitoringService.startMonitorStockMarket(stock, marketId);
    }

    @GetMapping(value = "/start/{stock}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void startAllStockMonitors(@PathVariable("stock") Stock stock) {
        stockMonitoringService.startAllStockMonitors(stock);
    }

    @GetMapping(value = "/stop/{stock}/market/{marketId}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void stopMonitorStockMarket(@PathVariable("stock") Stock stock, @PathVariable("marketId") Long marketId) {
        stockMonitoringService.stopMonitorStockMarket(stock, marketId);
    }

    @GetMapping(value = "/stop/{stock}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void stopAllStockMonitors(@PathVariable("stock") Stock stock) {
        stockMonitoringService.stopAllStockMonitors(stock);
    }

}
