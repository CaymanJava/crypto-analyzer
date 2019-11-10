package pro.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.crypto.SettingKey;
import pro.crypto.model.Setting;

import java.util.List;
import java.util.Set;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    List<Setting> findAllByKeyIn(Set<SettingKey> keys);

    Setting findFirstByKey(SettingKey key);

}
