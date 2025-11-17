package ro.utcluj.ssatr.lab3.drone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.service.DroneService;

import java.util.List;

/**
 * REST Controller pentru operații CRUD pe drone.
 */
@RestController
@RequestMapping("/api/drones")
public class DroneRestController {

    @Autowired
    private DroneService droneService;

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
     * TODO: Studenții vor implementa trimiterea comenzii prin Kafka.
     */
    @PostMapping("/{id}/takeoff")
    public ResponseEntity<String> takeoff(@PathVariable String id) {
        // TODO: Trimite comandă prin Kafka
        return ResponseEntity.ok("Takeoff command sent to drone " + id);
    }

    /**
     * POST /api/drones/{id}/land - Comandă de aterizare.
     * TODO: Studenții vor implementa trimiterea comenzii prin Kafka.
     */
    @PostMapping("/{id}/land")
    public ResponseEntity<String> land(@PathVariable String id) {
        // TODO: Trimite comandă prin Kafka
        return ResponseEntity.ok("Land command sent to drone " + id);
    }

    /**
     * POST /api/drones/{id}/return-home - Comandă return to home.
     * TODO: Studenții vor implementa.
     */
    @PostMapping("/{id}/return-home")
    public ResponseEntity<String> returnHome(@PathVariable String id) {
        // TODO: Trimite comandă prin Kafka
        return ResponseEntity.ok("Return home command sent to drone " + id);
    }

    /**
     * TODO: Studenții pot adăuga:
     * - /api/drones/{id}/goto - comandă GOTO waypoint
     * - /api/drones/{id}/emergency - comandă emergency land
     * - /api/drones/{id}/telemetry - telemetrie recentă
     * - /api/drones/{id}/statistics - statistici detaliate
     */
}
