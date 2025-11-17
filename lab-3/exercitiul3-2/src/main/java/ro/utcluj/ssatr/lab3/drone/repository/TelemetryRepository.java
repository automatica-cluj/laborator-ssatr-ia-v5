package ro.utcluj.ssatr.lab3.drone.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.utcluj.ssatr.lab3.drone.model.TelemetrySnapshot;

import java.util.List;

/**
 * Repository Spring Data JPA pentru TelemetrySnapshot.
 */
@Repository
public interface TelemetryRepository extends JpaRepository<TelemetrySnapshot, Long> {

    /**
     * Găsește telemetria recentă pentru o dronă, sortată descrescător după timestamp.
     * Folosește Pageable pentru limitare rezultate.
     */
    List<TelemetrySnapshot> findByDroneIdOrderByTimestampDesc(String droneId, Pageable pageable);

    /**
     * Găsește telemetria mai recentă decât un timestamp dat.
     */
    @Query("SELECT t FROM TelemetrySnapshot t WHERE t.drone.id = :droneId AND t.timestamp > :since ORDER BY t.timestamp DESC")
    List<TelemetrySnapshot> findRecentTelemetry(@Param("droneId") String droneId, @Param("since") Long since);

    /**
     * Găsește toate snapshoturile de telemetrie pentru o dronă.
     */
    List<TelemetrySnapshot> findByDroneId(String droneId);

    /**
     * TODO: Studenții pot adăuga:
     * - Query pentru statistici (AVG battery, MAX altitude, etc.)
     * - Query pentru detectare anomalii
     */
}
