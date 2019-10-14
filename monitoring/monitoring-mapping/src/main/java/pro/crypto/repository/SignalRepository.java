package pro.crypto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import pro.crypto.model.Signal;

public interface SignalRepository extends JpaRepository<Signal, Long>, JpaSpecificationExecutor<Signal> {
}
