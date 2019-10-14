package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.SignalProxy;
import pro.crypto.service.HttpSignalService;
import pro.crypto.service.SignalService;

@Configuration
public class SignalHttpConfiguration {

    @ConditionalOnMissingBean(SignalService.class)
    @Import(HttpSignalService.class)
    @EnableFeignClients(clients = SignalProxy.class)
    @Configuration
    public static class HttpSignalServiceConfiguration {
    }

}
