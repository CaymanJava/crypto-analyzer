package pro.crypto.configuration;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.crypto.supplier.BittrexClient;

@EnableFeignClients(clients = {BittrexClient.class})
@ComponentScan(value = {"pro.crypto.supplier"})
@Configuration
public class SupplierConfiguration {
}
