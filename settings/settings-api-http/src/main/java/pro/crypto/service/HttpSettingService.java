package pro.crypto.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pro.crypto.SettingKey;
import pro.crypto.SettingType;
import pro.crypto.proxy.HttpSettingProxy;
import pro.crypto.request.BatchSettingUpdateRequest;
import pro.crypto.request.SettingUpdateRequest;
import pro.crypto.snapshot.SettingSnapshot;

import java.util.Map;

@Service
@AllArgsConstructor
public class HttpSettingService implements SettingService {

    private final HttpSettingProxy settingProxy;

    @Override
    public Map<SettingKey, String> getSettings(SettingType type) {
        return settingProxy.getSettings(type);
    }

    @Override
    public SettingSnapshot getSetting(SettingKey key) {
        return settingProxy.getSetting(key);
    }

    @Override
    public void updateSetting(SettingUpdateRequest request) {
        settingProxy.updateSetting(request);
    }

    @Override
    public void batchUpdateSettings(BatchSettingUpdateRequest request) {
        settingProxy.batchUpdateSettings(request);
    }

}
