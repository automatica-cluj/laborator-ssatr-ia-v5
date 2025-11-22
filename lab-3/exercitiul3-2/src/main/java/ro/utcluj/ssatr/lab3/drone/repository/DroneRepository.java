package ro.utcluj.ssatr.lab3.drone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.model.DroneStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository Spring Data JPA pentru Drone.
 * Spring Data generează automat implementarea metodelor.
 */
@Repository
public interface DroneRepository extends JpaRepository<Drone, String> {

    /**
     * Găsește drone după status.
     * Spring Data generează automat implementarea bazată pe numele metodei.
     */
    List<Drone> findByStatus(DroneStatus status);

    /**
     * Găsește drone cu baterie sub un anumit prag.
     * Exemplu de query JPQL custom cu @Query annotation.
     */
    @Query("SELECT d FROM Drone d WHERE d.batteryLevel < :threshold")
    List<Drone> findLowBatteryDrones(@Param("threshold") BigDecimal threshold);

    /**
     * Găsește drone cu baterie sub un prag, sortate crescător după baterie.
     */
    List<Drone> findByBatteryLevelLessThanOrderByBatteryLevelAsc(BigDecimal threshold);

    /**
     * TODO: Studenții pot adăuga mai multe query methods:
     * - findByStatusAndBatteryLevelGreaterThan(DroneStatus status, Double battery)
     * - countByStatus(DroneStatus status)
     * - Query-uri custom cu @Query pentru statistici complexe
     */
}
