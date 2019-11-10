package pro.crypto.web;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pro.crypto.SettingKey;
import pro.crypto.SettingType;
import pro.crypto.request.BatchSettingUpdateRequest;
import pro.crypto.request.SettingUpdateRequest;
import pro.crypto.service.SettingService;
import pro.crypto.snapshot.SettingSnapshot;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@RestController
@RequestMapping("/settings")
@AllArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @GetMapping(value = "/type/{type}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<SettingKey, String> getSettings(@PathVariable("type") SettingType type) {
        return settingService.getSettings(type);
    }

    @GetMapping(value = "/key/{key}", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SettingSnapshot getSetting(@PathVariable("key") SettingKey key) {
        return settingService.getSetting(key);
    }

    @PutMapping(produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void updateSetting(@RequestBody SettingUpdateRequest request) {
        settingService.updateSetting(request);
    }

    @PutMapping(value = "batch", produces = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void batchUpdateSettings(@RequestBody BatchSettingUpdateRequest request) {
        settingService.batchUpdateSettings(request);
    }

}
