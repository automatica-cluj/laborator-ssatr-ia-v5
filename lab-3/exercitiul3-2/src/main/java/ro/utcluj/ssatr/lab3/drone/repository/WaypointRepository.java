package ro.utcluj.ssatr.lab3.drone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcluj.ssatr.lab3.drone.model.Waypoint;

import java.util.List;

/**
 * Repository Spring Data JPA pentru Waypoint.
 */
@Repository
public interface WaypointRepository extends JpaRepository<Waypoint, Long> {

    /**
     * Găsește waypoint-urile unei misiuni, sortate după secvență.
     */
    List<Waypoint> findByMissionIdOrderBySequenceNumberAsc(Long missionId);

    /**
     * Găsește waypoint-urile neatinse pentru o misiune.
     */
    List<Waypoint> findByMissionIdAndReachedFalse(Long missionId);

    /**
     * TODO: Studenții pot adăuga query-uri pentru:
     * - Next waypoint to reach
     * - Progress percentage
     */
}
