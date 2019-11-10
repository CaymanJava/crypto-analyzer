package pro.crypto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pro.crypto.SettingKey;
import pro.crypto.snapshot.SettingSnapshot;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@Entity
@Table(schema = "crypto_settings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Setting {

    @Id
    @Enumerated(value = EnumType.STRING)
    private SettingKey key;

    @NotBlank
    private String value;

    public SettingSnapshot toSnapshot() {
        return SettingSnapshot.builder()
                .key(key)
                .value(value)
                .build();
    }

}
