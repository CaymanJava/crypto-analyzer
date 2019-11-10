package pro.crypto.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pro.crypto.SettingKey;
import pro.crypto.SettingType;
import pro.crypto.request.BatchSettingUpdateRequest;
import pro.crypto.request.SettingUpdateRequest;
import pro.crypto.snapshot.SettingSnapshot;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@Validated
@FeignClient(name = "api-settings", url = "${pro.crypto.entry.point.api.url}", path = "/settings")
public interface SettingProxy {

    @RequestMapping(value = "/type/{type}", method = GET)
    @ResponseBody
    Map<SettingKey, String> getSettings(@PathVariable("type") SettingType type);

    @RequestMapping(value = "/key/{key}", method = GET)
    @ResponseBody
    SettingSnapshot getSetting(@PathVariable("key") SettingKey key);

    @RequestMapping(method = PUT)
    void updateSetting(@RequestBody SettingUpdateRequest request);

    @RequestMapping(value = "batch", method = PUT)
    void batchUpdateSettings(@RequestBody BatchSettingUpdateRequest request);

}
