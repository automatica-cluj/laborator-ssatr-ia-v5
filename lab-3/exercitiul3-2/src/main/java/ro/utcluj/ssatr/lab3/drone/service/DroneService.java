package ro.utcluj.ssatr.lab3.drone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.model.DroneStatus;
import ro.utcluj.ssatr.lab3.drone.repository.DroneRepository;

import java.util.List;

/**
 * Service pentru business logic legată de drone.
 */
@Service
@Transactional
public class DroneService {

    @Autowired
    private DroneRepository droneRepository;

    /**
     * Obține toate drone-urile.
     */
    public List<Drone> getAllDrones() {
        return droneRepository.findAll();
    }

    /**
     * Obține o dronă după ID.
     */
    public Drone getDroneById(String id) {
        return droneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Drone not found: " + id));
    }

    /**
     * Creează o dronă nouă.
     */
    public Drone createDrone(Drone drone) {
        // TODO: Studenții pot adăuga validări
        return droneRepository.save(drone);
    }

    /**
     * Actualizează o dronă.
     */
    public Drone updateDrone(String id, Drone droneDetails) {
        Drone drone = getDroneById(id);
        drone.setName(droneDetails.getName());
        drone.setModel(droneDetails.getModel());
        drone.setStatus(droneDetails.getStatus());
        drone.setBatteryLevel(droneDetails.getBatteryLevel());
        return droneRepository.save(drone);
    }

    /**
     * Șterge o dronă.
     */
    public void deleteDrone(String id) {
        droneRepository.deleteById(id);
    }

    /**
     * Obține drone-urile active (FLYING).
     */
    public List<Drone> getActiveDrones() {
        return droneRepository.findByStatus(DroneStatus.FLYING);
    }

    /**
     * Obține drone-urile cu baterie scăzută (< 20%).
     */
    public List<Drone> getLowBatteryDrones() {
        return droneRepository.findLowBatteryDrones(20.0);
    }

    /**
     * Actualizează status-ul unei drone.
     */
    public void updateDroneStatus(String droneId, DroneStatus status) {
        Drone drone = getDroneById(droneId);
        drone.setStatus(status);
        droneRepository.save(drone);
    }

    /**
     * Actualizează nivelul bateriei.
     */
    public void updateBatteryLevel(String droneId, Double batteryLevel) {
        Drone drone = getDroneById(droneId);
        drone.setBatteryLevel(batteryLevel);
        drone.setLastSeen(System.currentTimeMillis());
        droneRepository.save(drone);
    }

    /**
     * TODO: Studenții pot adăuga:
     * - sendCommand(String droneId, String commandType) - trimite comandă prin Kafka
     * - getDetailedStatistics(String droneId) - statistici detaliate
     */
}
