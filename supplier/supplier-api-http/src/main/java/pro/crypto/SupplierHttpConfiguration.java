package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpStockMonitoringProxy;
import pro.crypto.service.HttpStockMonitoringService;
import pro.crypto.service.StockMonitoringService;

@Configuration
public class SupplierHttpConfiguration {

    @ConditionalOnMissingBean(StockMonitoringService.class)
    @Import(HttpStockMonitoringService.class)
    @EnableFeignClients(clients = HttpStockMonitoringProxy.class)
    @Configuration
    public static class StrategyHttpServiceConfiguration {
    }

}
