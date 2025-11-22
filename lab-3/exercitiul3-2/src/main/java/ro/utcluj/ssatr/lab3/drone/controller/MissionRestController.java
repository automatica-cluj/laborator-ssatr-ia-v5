package ro.utcluj.ssatr.lab3.drone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.utcluj.ssatr.lab3.drone.model.Mission;
import ro.utcluj.ssatr.lab3.drone.service.MissionService;

import java.util.List;

/**
 * REST Controller pentru operații CRUD pe misiuni.
 */
@RestController
@RequestMapping("/api/missions")
public class MissionRestController {

    @Autowired
    private MissionService missionService;

    /**
     * GET /api/missions - Obține toate misiunile.
     */
    @GetMapping
    public List<Mission> getAllMissions() {
        return missionService.getAllMissions();
    }

    /**
     * GET /api/missions/{id} - Obține o misiune după ID.
     */
    @GetMapping("/{id}")
    public Mission getMission(@PathVariable Integer id) {
        return missionService.getMissionById(id);
    }

    /**
     * POST /api/missions - Creează o misiune nouă.
     * TODO: Studenții vor extinde pentru a gestiona waypoint-urile.
     */
    @PostMapping
    public Mission createMission(@RequestBody Mission mission) {
        return missionService.createMission(mission);
    }

    /**
     * PUT /api/missions/{id} - Actualizează o misiune.
     */
    @PutMapping("/{id}")
    public Mission updateMission(@PathVariable Integer id, @RequestBody Mission mission) {
        return missionService.updateMission(id, mission);
    }

    /**
     * DELETE /api/missions/{id} - Șterge o misiune.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMission(@PathVariable Integer id) {
        missionService.deleteMission(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/missions/active - Obține misiunile active.
     */
    @GetMapping("/active")
    public List<Mission> getActiveMissions() {
        return missionService.getActiveMissions();
    }

    /**
     * GET /api/missions/pending - Obține misiunile planificate sau active.
     */
    @GetMapping("/pending")
    public List<Mission> getPendingMissions() {
        return missionService.getPendingMissions();
    }

    /**
     * POST /api/missions/{id}/start - Pornește o misiune.
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<String> startMission(@PathVariable Integer id) {
        missionService.startMission(id);
        return ResponseEntity.ok("Mission " + id + " started");
    }

    /**
     * POST /api/missions/{id}/complete - Marchează misiunea ca finalizată.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<String> completeMission(@PathVariable Integer id) {
        missionService.completeMission(id);
        return ResponseEntity.ok("Mission " + id + " completed");
    }

    /**
     * POST /api/missions/{id}/fail - Marchează misiunea ca eșuată.
     */
    @PostMapping("/{id}/fail")
    public ResponseEntity<String> failMission(@PathVariable Integer id) {
        missionService.failMission(id);
        return ResponseEntity.ok("Mission " + id + " failed");
    }

    /**
     * TODO: Studenții pot adăuga:
     * - GET /api/missions/drone/{droneId} - misiuni pentru o dronă
     * - POST /api/missions/{id}/cancel - anulare misiune
     * - GET /api/missions/{id}/progress - progres misiune
     */
}
