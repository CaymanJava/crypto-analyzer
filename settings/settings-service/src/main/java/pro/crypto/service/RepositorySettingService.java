package pro.crypto.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import pro.crypto.SettingKey;
import pro.crypto.SettingType;
import pro.crypto.model.Setting;
import pro.crypto.repository.SettingRepository;
import pro.crypto.request.BatchSettingUpdateRequest;
import pro.crypto.request.SettingUpdateRequest;
import pro.crypto.snapshot.SettingSnapshot;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Transactional
@AllArgsConstructor
public class RepositorySettingService implements SettingService {

    private final SettingRepository repository;

    @Override
    public Map<SettingKey, String> getSettings(SettingType type) {
        log.trace("Getting settings {type: {}}", type);
        Map<SettingKey, String> settingsMap = repository.findAllByKeyIn(type.getSettingKeys()).stream()
                .collect(toMap(Setting::getKey, Setting::getValue));
        log.info("Got settings {type: {}}", type);
        return settingsMap;
    }

    @Override
    public SettingSnapshot getSetting(SettingKey key) {
        log.trace("Getting setting {key: {}}", key);
        SettingSnapshot settingSnapshot = repository.findFirstByKey(key).toSnapshot();
        log.info("Got setting {setting: {}}", settingSnapshot);
        return settingSnapshot;
    }

    @Override
    public void updateSetting(SettingUpdateRequest request) {
        log.trace("Updating setting {request: {}}", request);
        update(request.getKey(), request.getValue());
        log.info("Updated setting {request: {}}", request);
    }

    @Override
    public void batchUpdateSettings(BatchSettingUpdateRequest request) {
        log.trace("Updating settings");
        request.getUpdatedSettings().forEach(this::update);
        log.info("Updated settings");
    }

    private void update(SettingKey key, String value) {
        Setting setting = repository.findFirstByKey(key);
        setting.setValue(value);
    }

}


