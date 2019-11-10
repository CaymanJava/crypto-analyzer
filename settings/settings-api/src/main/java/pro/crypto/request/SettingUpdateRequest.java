package pro.crypto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import pro.crypto.SettingKey;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@Value
@Builder
public class SettingUpdateRequest {

    @NotNull
    private SettingKey key;
    @NotBlank
    private String value;

}
