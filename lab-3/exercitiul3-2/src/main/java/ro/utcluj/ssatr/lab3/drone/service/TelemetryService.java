package ro.utcluj.ssatr.lab3.drone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcluj.ssatr.lab3.drone.model.Drone;
import ro.utcluj.ssatr.lab3.drone.model.TelemetrySnapshot;
import ro.utcluj.ssatr.lab3.drone.repository.DroneRepository;
import ro.utcluj.ssatr.lab3.drone.repository.TelemetryRepository;

import java.util.List;

/**
 * Service pentru business logic legată de telemetrie.
 */
@Service
@Transactional
public class TelemetryService {

    @Autowired
    private TelemetryRepository telemetryRepository;

    @Autowired
    private DroneRepository droneRepository;

    /**
     * Salvează un snapshot de telemetrie.
     */
    public TelemetrySnapshot saveTelemetry(TelemetrySnapshot telemetry) {
        return telemetryRepository.save(telemetry);
    }

    /**
     * Obține telemetria recentă pentru o dronă (ultimele N înregistrări).
     */
    public List<TelemetrySnapshot> getRecentTelemetry(String droneId, int limit) {
        return telemetryRepository.findByDroneIdOrderByTimestampDesc(
                droneId,
                PageRequest.of(0, limit)
        );
    }

    /**
     * Obține telemetria mai recentă decât un timestamp.
     */
    public List<TelemetrySnapshot> getTelemetrySince(String droneId, Long sinceTimestamp) {
        return telemetryRepository.findRecentTelemetry(droneId, sinceTimestamp);
    }

    /**
     * Obține toată telemetria pentru o dronă.
     */
    public List<TelemetrySnapshot> getAllTelemetryForDrone(String droneId) {
        return telemetryRepository.findByDroneId(droneId);
    }

    /**
     * Obține ultima telemetrie pentru o dronă (cea mai recentă înregistrare).
     * Folosit pentru a obține poziția curentă a dronei.
     */
    public TelemetrySnapshot getLatestTelemetry(String droneId) {
        List<TelemetrySnapshot> recent = getRecentTelemetry(droneId, 1);
        return recent.isEmpty() ? null : recent.get(0);
    }

    /**
     * Actualizează statusul dronei bazat pe telemetrie.
     * Această metodă este apelată de Kafka listener.
     */
    public void updateDroneFromTelemetry(String droneId, TelemetrySnapshot telemetry) {
        Drone drone = droneRepository.findById(droneId).orElse(null);
        if (drone != null) {
            drone.setBatteryLevel(telemetry.getBatteryLevel());
            drone.setStatus(telemetry.getStatus());
            drone.setLastSeen(telemetry.getTimestamp());
            droneRepository.save(drone);
        }
    }

    /**
     * TODO: Studenții pot adăuga:
     * - getAverageStatistics(String droneId) - statistici medii
     * - detectAnomalies(String droneId) - detectare anomalii în istoric
     * - exportToCsv(String droneId) - export telemetrie în CSV
     */
}
