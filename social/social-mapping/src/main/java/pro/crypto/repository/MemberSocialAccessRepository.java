package pro.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.crypto.Provider;
import pro.crypto.model.MemberSocialAccess;

import java.util.Optional;

public interface MemberSocialAccessRepository extends JpaRepository<MemberSocialAccess, Long> {

    Optional<MemberSocialAccess> findFirstBySocialIdAndProvider(String socialId, Provider provider);

}
