package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.MemberRegisterProperties;
import pro.crypto.service.MemberMapper;
import pro.crypto.service.MemberService;
import pro.crypto.service.RegisterMemberService;
import pro.crypto.service.RepositoryMemberService;
import pro.crypto.service.RepositoryRegisterMemberService;
import pro.crypto.web.MemberController;
import pro.crypto.web.RegisterMemberController;

@Configuration
@EnableConfigurationProperties({MemberRegisterProperties.class})
public class MemberConfiguration {

    @Configuration
    @ConditionalOnMissingBean(MemberService.class)
    @Import({RepositoryMemberService.class, MemberMapper.class})
    public static class MemberServiceConfiguration {
    }

    @Configuration
    @ConditionalOnMissingBean(RegisterMemberService.class)
    @Import({RepositoryRegisterMemberService.class})
    public static class RegisterMemberServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({MemberController.class, RegisterMemberController.class})
    @Configuration
    public static class MarketWebConfiguration {
    }

}
