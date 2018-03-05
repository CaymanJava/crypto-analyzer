package pro.crypto.configuration;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import pro.crypto.supplier.BittrexSpiderClient;

@EnableFeignClients(clients = {BittrexSpiderClient.class})
@ComponentScan(value = {"pro.crypto.data.supplier.**"})
@Configuration
public class SupplierConfiguration {
}
