package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.service.DataProvider;
import pro.crypto.service.DataSupplier;
import pro.crypto.service.MonitorSupervisor;
import pro.crypto.service.StockMonitoringService;
import pro.crypto.stock.BittrexClient;
import pro.crypto.web.StockMonitoringController;

@Configuration
public class SupplierConfiguration {

    @Configuration
    @ConditionalOnMissingBean(DataSupplier.class)
    @Import({DataProvider.class})
    public static class DataSupplierServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(StockMonitoringService.class)
    @Import({MonitorSupervisor.class})
    public static class StockMonitoringServiceConfiguration {
    }

    @Configuration
    @EnableFeignClients(clients = {BittrexClient.class})
    public static class StockClientConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({StockMonitoringController.class})
    @Configuration
    public static class SupplierWebConfiguration {
    }

}
