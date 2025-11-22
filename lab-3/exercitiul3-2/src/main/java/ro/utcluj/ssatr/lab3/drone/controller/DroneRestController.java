package ro.utcluj.ssatr.lab3.drone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.ssatr.lab3.drone.dto.DronePositionDTO;
import ro.utcluj.ssatr.lab3.drone.kafka.CommandKafkaProducer;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.model.TelemetrySnapshot;
import ro.utcluj.ssatr.lab3.drone.service.DroneService;
import ro.utcluj.ssatr.lab3.drone.service.TelemetryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller pentru operații CRUD pe drone.
 */
@RestController
@RequestMapping("/api/drones")
public class DroneRestController {

    @Autowired
    private DroneService droneService;

    @Autowired
    private TelemetryService telemetryService;

    @Autowired
    private CommandKafkaProducer commandKafkaProducer;

    /**
     * GET /api/drones - Obține toate drone-urile.
     */
    @GetMapping
    public List<Drone> getAllDrones() {
        return droneService.getAllDrones();
    }

    /**
     * GET /api/drones/{id} - Obține o dronă după ID.
     */
    @GetMapping("/{id}")
    public Drone getDrone(@PathVariable String id) {
        return droneService.getDroneById(id);
    }

    /**
     * POST /api/drones - Creează o dronă nouă.
     */
    @PostMapping
    public Drone createDrone(@RequestBody Drone drone) {
        return droneService.createDrone(drone);
    }

    /**
     * PUT /api/drones/{id} - Actualizează o dronă.
     */
    @PutMapping("/{id}")
    public Drone updateDrone(@PathVariable String id, @RequestBody Drone drone) {
        return droneService.updateDrone(id, drone);
    }

    /**
     * DELETE /api/drones/{id} - Șterge o dronă.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDrone(@PathVariable String id) {
        droneService.deleteDrone(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/drones/active - Obține drone-urile active.
     */
    @GetMapping("/active")
    public List<Drone> getActiveDrones() {
        return droneService.getActiveDrones();
    }

    /**
     * GET /api/drones/low-battery - Obține drone-urile cu baterie scăzută.
     */
    @GetMapping("/low-battery")
    public List<Drone> getLowBatteryDrones() {
        return droneService.getLowBatteryDrones();
    }

    /**
     * POST /api/drones/{id}/takeoff - Comandă de decolare.
     * Trimite comanda prin Kafka către dronă.
     */
    @PostMapping("/{id}/takeoff")
    public ResponseEntity<String> takeoff(@PathVariable String id) {
        // Verifică dacă drona există
        Drone drone = droneService.getDroneById(id);

        // Trimite comandă prin Kafka
        commandKafkaProducer.sendTakeOffCommand(id);

        return ResponseEntity.ok("Takeoff command sent to drone " + id);
    }

    /**
     * POST /api/drones/{id}/land - Comandă de aterizare.
     * Trimite comanda prin Kafka către dronă.
     */
    @PostMapping("/{id}/land")
    public ResponseEntity<String> land(@PathVariable String id) {
        // Verifică dacă drona există
        Drone drone = droneService.getDroneById(id);

        // Trimite comandă prin Kafka
        commandKafkaProducer.sendLandCommand(id);

        return ResponseEntity.ok("Land command sent to drone " + id);
    }

    /**
     * POST /api/drones/{id}/return-home - Comandă return to home.
     * Trimite comanda prin Kafka către dronă.
     */
    @PostMapping("/{id}/return-home")
    public ResponseEntity<String> returnHome(@PathVariable String id) {
        // Verifică dacă drona există
        Drone drone = droneService.getDroneById(id);

        // Trimite comandă prin Kafka
        commandKafkaProducer.sendReturnHomeCommand(id);

        return ResponseEntity.ok("Return home command sent to drone " + id);
    }

    /**
     * GET /api/drones/positions - Obține pozițiile curente ale tuturor dronelor.
     * Combină datele din tabela drones cu telemetria recentă pentru poziție.
     * Folosit de pagina de monitorizare live pentru a afișa drone-urile pe hartă.
     */
    @GetMapping("/positions")
    public List<DronePositionDTO> getAllDronePositions() {
        List<Drone> drones = droneService.getAllDrones();

        return drones.stream()
                .map(drone -> {
                    DronePositionDTO dto = new DronePositionDTO();
                    dto.setId(drone.getId());
                    dto.setName(drone.getName());
                    dto.setModel(drone.getModel());
                    dto.setStatus(drone.getStatus());
                    dto.setBatteryLevel(drone.getBatteryLevel());
                    dto.setLastSeen(drone.getLastSeen());

                    // Obține ultima telemetrie pentru poziția curentă
                    TelemetrySnapshot latest = telemetryService.getLatestTelemetry(drone.getId());
                    if (latest != null) {
                        dto.setLatitude(latest.getLatitude());
                        dto.setLongitude(latest.getLongitude());
                        dto.setAltitude(latest.getAltitude());
                        dto.setSpeed(latest.getSpeed());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * TODO: Studenții pot adăuga:
     * - /api/drones/{id}/goto - comandă GOTO waypoint
     * - /api/drones/{id}/emergency - comandă emergency land
     * - /api/drones/{id}/telemetry - telemetrie recentă
     * - /api/drones/{id}/statistics - statistici detaliate
     */
}
