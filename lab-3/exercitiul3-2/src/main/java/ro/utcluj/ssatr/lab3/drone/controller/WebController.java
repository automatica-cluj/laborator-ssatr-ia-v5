package ro.utcluj.ssatr.lab3.drone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.model.Mission;
import ro.utcluj.ssatr.lab3.drone.model.Waypoint;
import java.util.List;

import ro.utcluj.ssatr.lab3.drone.service.DroneService;
import ro.utcluj.ssatr.lab3.drone.service.MissionService;
import ro.utcluj.ssatr.lab3.drone.service.TelemetryService;

/**
 * Controller pentru pagini Thymeleaf (UI web).
 */
@Controller
public class WebController {

    @Autowired
    private DroneService droneService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private TelemetryService telemetryService;

    /**
     * Dashboard principal.
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalDrones", droneService.getAllDrones().size());
        model.addAttribute("activeDrones", droneService.getActiveDrones().size());
        model.addAttribute("lowBatteryDrones", droneService.getLowBatteryDrones().size());
        model.addAttribute("activeMissions", missionService.getActiveMissions().size());
        model.addAttribute("totalMissions", missionService.getAllMissions().size());
        return "index";
    }

    /**
     * Pagina cu lista de drone.
     */
    @GetMapping("/drones")
    public String drones(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "drones";
    }

    /**
     * Pagina cu detalii despre o dronă.
     */
    @GetMapping("/drones/{id}")
    public String droneDetail(@PathVariable String id, Model model) {
        model.addAttribute("drone", droneService.getDroneById(id));
        model.addAttribute("recentTelemetry", telemetryService.getRecentTelemetry(id, 50));
        model.addAttribute("missions", missionService.getMissionsByDroneId(id));
        return "drone-detail";
    }

    /**
     * Pagina cu lista de misiuni.
     */
    @GetMapping("/missions")
    public String missions(Model model) {
        model.addAttribute("missions", missionService.getAllMissions());
        model.addAttribute("activeMissions", missionService.getActiveMissions());
        model.addAttribute("pendingMissions", missionService.getPendingMissions());
        return "missions";
    }

    /**
     * Pagina cu detalii despre o misiune.
     */
    @GetMapping("/missions/{id}")
    public String missionDetail(@PathVariable Integer id, Model model) {
        model.addAttribute("mission", missionService.getMissionById(id));
        return "mission-details";
    }

    /**
     * Pagina pentru crearea unei misiuni noi.
     */
    @GetMapping("/missions/create")
    public String createMission(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "mission-create";
    }

    /**
     * Handle-uiește crearea unei misiuni noi.
     */
    @PostMapping("/missions/create")
    public String createMission(@RequestParam String name,
                                @RequestParam String description,
                                @RequestParam(required = false) String droneId,
                                @RequestParam(required = false, name = "waypoints.latitude") List<Double> latitudes,
                                @RequestParam(required = false, name = "waypoints.longitude") List<Double> longitudes,
                                @RequestParam(required = false, name = "waypoints.altitude") List<Double> altitudes) {
        Mission mission = new Mission();
        mission.setName(name);
        mission.setDescription(description);

        if (droneId != null && !droneId.isEmpty()) {
            Drone drone = droneService.getDroneById(droneId);
            mission.setDrone(drone);
        }

        if (latitudes != null) {
            for (int i = 0; i < latitudes.size(); i++) {
                Waypoint waypoint = new Waypoint();
                waypoint.setLatitude(new java.math.BigDecimal(latitudes.get(i)));
                waypoint.setLongitude(new java.math.BigDecimal(longitudes.get(i)));
                waypoint.setAltitude(new java.math.BigDecimal(altitudes.get(i)));
                waypoint.setSequenceNumber(i + 1);
                waypoint.setMission(mission);
                mission.getWaypoints().add(waypoint);
            }
        }

        missionService.createMission(mission);
        return "redirect:/missions";
    }

    /**
     * Handle-uiește ștergerea unei misiuni.
     */
    @GetMapping("/missions/delete/{id}")
    public String deleteMission(@PathVariable Integer id) {
        missionService.deleteMission(id);
        return "redirect:/missions";
    }

    /**
     * Pagina pentru actualizarea unei misiuni.
     */
    @GetMapping("/missions/update/{id}")
    public String showUpdateMissionForm(@PathVariable Integer id, Model model) {
        model.addAttribute("mission", missionService.getMissionById(id));
        model.addAttribute("drones", droneService.getAllDrones());
        return "mission-update";
    }

    /**
     * Handle-uiește actualizarea unei misiuni.
     */
    @PostMapping("/missions/update/{id}")
    public String updateMission(@PathVariable Integer id, @ModelAttribute("mission") Mission missionDetails, 
                                @RequestParam(required = false) String droneId,
                                @RequestParam(required = false, name = "waypoints.latitude") List<Double> latitudes,
                                @RequestParam(required = false, name = "waypoints.longitude") List<Double> longitudes,
                                @RequestParam(required = false, name = "waypoints.altitude") List<Double> altitudes) {
        Mission mission = missionService.getMissionById(id);
        mission.setName(missionDetails.getName());
        mission.setDescription(missionDetails.getDescription());
        mission.setStatus(missionDetails.getStatus());

        if (droneId != null && !droneId.isEmpty()) {
            Drone drone = droneService.getDroneById(droneId);
            mission.setDrone(drone);
        } else {
            mission.setDrone(null);
        }

        mission.getWaypoints().clear();
        if (latitudes != null) {
            for (int i = 0; i < latitudes.size(); i++) {
                Waypoint waypoint = new Waypoint();
                waypoint.setLatitude(new java.math.BigDecimal(latitudes.get(i)));
                waypoint.setLongitude(new java.math.BigDecimal(longitudes.get(i)));
                waypoint.setAltitude(new java.math.BigDecimal(altitudes.get(i)));
                waypoint.setSequenceNumber(i + 1);
                waypoint.setMission(mission);
                mission.getWaypoints().add(waypoint);
            }
        }

        missionService.updateMission(id, mission);
        return "redirect:/missions/{id}";
    }

    /**
     * Pagina de monitorizare live cu hartă interactivă.
     * Afișează toate dronele pe hartă folosind Leaflet.js.
     */
    @GetMapping("/monitor")
    public String liveMonitor() {
        return "monitor";
    }
}