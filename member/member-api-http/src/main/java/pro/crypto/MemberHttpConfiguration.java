package pro.crypto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.proxy.HttpMemberProxy;
import pro.crypto.proxy.HttpRegisterMemberProxy;
import pro.crypto.service.HttpMemberService;
import pro.crypto.service.HttpRegisterMemberService;
import pro.crypto.service.MemberService;
import pro.crypto.service.RegisterMemberService;

@Configuration
public class MemberHttpConfiguration {

    @ConditionalOnMissingBean(MemberService.class)
    @Import(HttpMemberService.class)
    @EnableFeignClients(clients = HttpMemberProxy.class)
    @Configuration
    public static class HttpMemberServiceConfiguration {
    }

    @ConditionalOnMissingBean(RegisterMemberService.class)
    @Import(HttpRegisterMemberService.class)
    @EnableFeignClients(clients = HttpRegisterMemberProxy.class)
    @Configuration
    public static class MarketSynchronizationHttpServiceConfiguration {
    }

}
