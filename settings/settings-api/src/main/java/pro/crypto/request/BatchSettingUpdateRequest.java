package pro.crypto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pro.crypto.SettingKey;

import javax.validation.constraints.NotNull;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Value
@Builder
public class BatchSettingUpdateRequest {

    @NotNull
    private Map<SettingKey, String> updatedSettings;

}
