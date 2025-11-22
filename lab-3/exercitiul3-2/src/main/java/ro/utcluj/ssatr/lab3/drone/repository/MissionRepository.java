package ro.utcluj.ssatr.lab3.drone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcluj.ssatr.lab3.drone.model.Mission;
import ro.utcluj.ssatr.lab3.drone.model.MissionStatus;

import java.util.List;

/**
 * Repository Spring Data JPA pentru Mission.
 */
@Repository
public interface MissionRepository extends JpaRepository<Mission, Integer> {

    /**
     * Găsește misiunile unei drone.
     */
    List<Mission> findByDroneId(String droneId);

    /**
     * Găsește misiuni după status.
     */
    List<Mission> findByStatus(MissionStatus status);

    /**
     * Găsește misiunile active sau planificate.
     */
    List<Mission> findByStatusIn(List<MissionStatus> statuses);

    /**
     * TODO: Studenții pot adăuga:
     * - findByDroneIdAndStatus(String droneId, MissionStatus status)
     * - Query pentru misiuni terminate în ultimele X zile
     */
}
