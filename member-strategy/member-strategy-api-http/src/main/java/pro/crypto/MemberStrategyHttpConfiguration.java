package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.MemberStrategyControlProxy;
import pro.crypto.proxy.MemberStrategyProxy;
import pro.crypto.service.HttpMemberStrategyControlService;
import pro.crypto.service.HttpMemberStrategyService;
import pro.crypto.service.MemberStrategyControlService;
import pro.crypto.service.MemberStrategyService;

@Configuration
public class MemberStrategyHttpConfiguration {

    @ConditionalOnMissingBean(MemberStrategyService.class)
    @Import(HttpMemberStrategyService.class)
    @EnableFeignClients(clients = MemberStrategyProxy.class)
    @Configuration
    public static class HttpMemberStrategyServiceConfiguration {
    }

    @ConditionalOnMissingBean(MemberStrategyControlService.class)
    @Import(HttpMemberStrategyControlService.class)
    @EnableFeignClients(clients = MemberStrategyControlProxy.class)
    @Configuration
    public static class HttpMemberStrategyControlServiceConfiguration {
    }

}
