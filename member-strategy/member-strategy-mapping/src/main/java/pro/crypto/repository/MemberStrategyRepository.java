package pro.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pro.crypto.model.MemberStrategy;
import pro.crypto.model.strategy.MemberStrategyStatus;

import java.time.LocalDateTime;
import java.util.Set;

public interface MemberStrategyRepository extends JpaRepository<MemberStrategy, Long>, JpaSpecificationExecutor<MemberStrategy> {

    @Query("SELECT ms.id FROM MemberStrategy ms WHERE ms.status = :status AND ms.nextExecutionTime <= :executionTime")
    Set<Long> findIdsForMonitoring(@Param("status") MemberStrategyStatus status, @Param("executionTime") LocalDateTime executionTime);

}
