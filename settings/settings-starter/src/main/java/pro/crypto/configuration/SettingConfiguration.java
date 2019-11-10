package pro.crypto.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pro.crypto.service.RepositorySettingService;
import pro.crypto.service.SettingService;
import pro.crypto.web.SettingController;

@Configuration
public class SettingConfiguration {

    @Configuration
    @ConditionalOnMissingBean(SettingService.class)
    @Import({RepositorySettingService.class})
    public static class SettingsServiceConfiguration {
    }

    @ConditionalOnWebApplication
    @Import({SettingController.class})
    @Configuration
    public static class SettingsWebConfiguration {
    }

}
