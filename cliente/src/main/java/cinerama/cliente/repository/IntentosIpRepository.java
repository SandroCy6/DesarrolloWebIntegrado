package cinerama.cliente.repository;

import cinerama.cliente.model.IntentosIp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntentosIpRepository extends JpaRepository<IntentosIp, Long> {
    Optional<IntentosIp> findByIp(String ip);

    boolean existsByIp(String ip);

}
