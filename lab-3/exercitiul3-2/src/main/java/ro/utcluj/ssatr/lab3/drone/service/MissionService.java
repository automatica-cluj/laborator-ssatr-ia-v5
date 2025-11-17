package ro.utcluj.ssatr.lab3.drone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.ssatr.lab3.drone.model.Mission;
import ro.utcluj.ssatr.lab3.drone.model.MissionStatus;
import ro.utcluj.ssatr.lab3.drone.repository.MissionRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Service pentru business logic legată de misiuni.
 */
@Service
@Transactional
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    /**
     * Obține toate misiunile.
     */
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    /**
     * Obține o misiune după ID.
     */
    public Mission getMissionById(Long id) {
        return missionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mission not found: " + id));
    }

    /**
     * Creează o misiune nouă.
     * TODO: Studenții vor implementa adăugarea waypoint-urilor.
     */
    public Mission createMission(Mission mission) {
        mission.setStatus(MissionStatus.PLANNED);
        return missionRepository.save(mission);
    }

    /**
     * Actualizează o misiune.
     */
    public Mission updateMission(Long id, Mission missionDetails) {
        Mission mission = getMissionById(id);
        mission.setName(missionDetails.getName());
        mission.setDescription(missionDetails.getDescription());
        mission.setStatus(missionDetails.getStatus());
        return missionRepository.save(mission);
    }

    /**
     * Șterge o misiune.
     */
    public void deleteMission(Long id) {
        missionRepository.deleteById(id);
    }

    /**
     * Obține misiunile unei drone.
     */
    public List<Mission> getMissionsByDroneId(String droneId) {
        return missionRepository.findByDroneId(droneId);
    }

    /**
     * Obține misiunile active.
     */
    public List<Mission> getActiveMissions() {
        return missionRepository.findByStatus(MissionStatus.ACTIVE);
    }

    /**
     * Obține misiunile planificate sau active.
     */
    public List<Mission> getPendingMissions() {
        return missionRepository.findByStatusIn(
                Arrays.asList(MissionStatus.PLANNED, MissionStatus.ACTIVE)
        );
    }

    /**
     * Începe o misiune.
     * TODO: Studenții vor implementa logica de start (trimitere comenzi către dronă).
     */
    public Mission startMission(Long id) {
        Mission mission = getMissionById(id);
        mission.setStatus(MissionStatus.ACTIVE);
        mission.setStartTime(System.currentTimeMillis());
        return missionRepository.save(mission);
    }

    /**
     * Completează o misiune cu succes.
     */
    public Mission completeMission(Long id) {
        Mission mission = getMissionById(id);
        mission.setStatus(MissionStatus.COMPLETED);
        mission.setEndTime(System.currentTimeMillis());
        return missionRepository.save(mission);
    }

    /**
     * Marchează o misiune ca eșuată.
     */
    public Mission failMission(Long id) {
        Mission mission = getMissionById(id);
        mission.setStatus(MissionStatus.FAILED);
        mission.setEndTime(System.currentTimeMillis());
        return missionRepository.save(mission);
    }

    /**
     * TODO: Studenții pot adăuga:
     * - assignDrone(Long missionId, String droneId)
     * - calculateProgress(Long missionId) - pe baza waypoint-urilor atinse
     * - cancelMission(Long id)
     */
}
