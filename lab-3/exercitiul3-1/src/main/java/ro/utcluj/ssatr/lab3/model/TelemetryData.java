package ro.utcluj.ssatr.lab3.model;

/**
 * Model pentru datele de telemetrie primite de la drone.
 */
public class TelemetryData {
    private String droneId;
    private long timestamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private int heading;
    private double batteryLevel;
    private String status;
    private double temperature;
    private double vibration;

    public TelemetryData() {
    }

    public TelemetryData(String droneId, long timestamp, double latitude, double longitude,
                         double altitude, double speed, int heading, double batteryLevel,
                         String status, double temperature, double vibration) {
        this.droneId = droneId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speed = speed;
        this.heading = heading;
        this.batteryLevel = batteryLevel;
        this.status = status;
        this.temperature = temperature;
        this.vibration = vibration;
    }

    // Getters and Setters
    public String getDroneId() {
        return droneId;
    }

    public void setDroneId(String droneId) {
        this.droneId = droneId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getVibration() {
        return vibration;
    }

    public void setVibration(double vibration) {
        this.vibration = vibration;
    }

    @Override
    public String toString() {
        return "TelemetryData{" +
                "droneId='" + droneId + '\'' +
                ", timestamp=" + timestamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", heading=" + heading +
                ", batteryLevel=" + batteryLevel +
                ", status='" + status + '\'' +
                ", temperature=" + temperature +
                ", vibration=" + vibration +
                '}';
    }
}
