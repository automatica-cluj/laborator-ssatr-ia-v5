package ro.utcluj.ssatr.lab3.drone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        // TODO: Studenții pot adăuga mai multe statistici
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
     * Pagina pentru crearea unei misiuni noi.
     * TODO: Studenții vor implementa formularul.
     */
    @GetMapping("/missions/create")
    public String createMission(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "mission-create";
    }

    /**
     * Pagina de monitorizare telemetrie în timp real.
     * TODO: Studenții vor implementa WebSocket client.
     */
    @GetMapping("/monitor")
    public String telemetryMonitor(Model model) {
        model.addAttribute("drones", droneService.getAllDrones());
        return "telemetry-monitor";
    }

    /**
     * TODO: Studenții pot adăuga:
     * - /analytics - pagină cu grafice și statistici
     * - /drones/create - formular creare dronă nouă
     * - /missions/{id} - detalii misiune
     */
}
