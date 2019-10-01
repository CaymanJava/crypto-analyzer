package pro.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pro.crypto.model.MemberStrategy;

public interface MemberStrategyRepository extends JpaRepository<MemberStrategy, Long>, JpaSpecificationExecutor<MemberStrategy> {
}
