package pro.crypto.service;

import pro.crypto.SettingKey;
import pro.crypto.SettingType;
import pro.crypto.request.BatchSettingUpdateRequest;
import pro.crypto.request.SettingUpdateRequest;
import pro.crypto.snapshot.SettingSnapshot;

import java.util.Map;

public interface SettingService {

    Map<SettingKey, String> getSettings(SettingType type);

    SettingSnapshot getSetting(SettingKey key);

    void updateSetting(SettingUpdateRequest request);

    void batchUpdateSettings(BatchSettingUpdateRequest request);

}
