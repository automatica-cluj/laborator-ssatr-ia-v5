-- Schema pentru Laboratorul 3 - Sistem Management Drone
-- PostgreSQL Database: dronedb

-- =============================================================================
-- Tabelul: drones
-- Descriere: Stochează informații despre drone-urile din flotă
-- =============================================================================
CREATE TABLE IF NOT EXISTS drones (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    model VARCHAR(100),
    status VARCHAR(20) DEFAULT 'IDLE' CHECK (status IN ('IDLE', 'FLYING', 'CHARGING', 'EMERGENCY', 'LANDED')),
    battery_level DECIMAL(5,2) DEFAULT 100.0 CHECK (battery_level >= 0 AND battery_level <= 100),
    last_seen BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Index pentru căutări după status
CREATE INDEX IF NOT EXISTS idx_drones_status ON drones(status);

-- Index pentru drone cu baterie scăzută
CREATE INDEX IF NOT EXISTS idx_drones_battery ON drones(battery_level);

-- =============================================================================
-- Tabelul: telemetry_logs
-- Descriere: Stochează datele de telemetrie primite de la drone
-- =============================================================================
CREATE TABLE IF NOT EXISTS telemetry_logs (
    id SERIAL PRIMARY KEY,
    drone_id VARCHAR(50) NOT NULL,
    timestamp BIGINT NOT NULL,
    latitude DECIMAL(10,7),
    longitude DECIMAL(10,7),
    altitude DECIMAL(8,2),
    speed DECIMAL(6,2),
    heading INTEGER CHECK (heading >= 0 AND heading < 360),
    battery_level DECIMAL(5,2) CHECK (battery_level >= 0 AND battery_level <= 100),
    temperature DECIMAL(5,2),
    vibration DECIMAL(6,4),
    status VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_telemetry_drone FOREIGN KEY (drone_id) REFERENCES drones(id) ON DELETE CASCADE
);

-- Index compus pentru query-uri frecvente (drone + timestamp)
CREATE INDEX IF NOT EXISTS idx_telemetry_drone_timestamp ON telemetry_logs(drone_id, timestamp DESC);

-- Index pentru query-uri pe interval de timp
CREATE INDEX IF NOT EXISTS idx_telemetry_timestamp ON telemetry_logs(timestamp DESC);

-- Index pentru căutări după status
CREATE INDEX IF NOT EXISTS idx_telemetry_status ON telemetry_logs(status);

-- =============================================================================
-- Tabelul: missions
-- Descriere: Stochează informații despre misiunile planificate/active
-- =============================================================================
CREATE TABLE IF NOT EXISTS missions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    drone_id VARCHAR(50),
    status VARCHAR(20) DEFAULT 'PLANNED' CHECK (status IN ('PLANNED', 'ACTIVE', 'COMPLETED', 'FAILED', 'CANCELLED')),
    start_time BIGINT,
    end_time BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mission_drone FOREIGN KEY (drone_id) REFERENCES drones(id) ON DELETE SET NULL
);

-- Index pentru query-uri pe dronă
CREATE INDEX IF NOT EXISTS idx_missions_drone ON missions(drone_id);

-- Index pentru query-uri pe status
CREATE INDEX IF NOT EXISTS idx_missions_status ON missions(status);

-- =============================================================================
-- Tabelul: waypoints
-- Descriere: Waypoint-uri pentru misiuni (fiecare misiune poate avea multiple waypoints)
-- =============================================================================
CREATE TABLE IF NOT EXISTS waypoints (
    id SERIAL PRIMARY KEY,
    mission_id INTEGER NOT NULL,
    sequence_number INTEGER NOT NULL,
    latitude DECIMAL(10,7) NOT NULL,
    longitude DECIMAL(10,7) NOT NULL,
    altitude DECIMAL(8,2) DEFAULT 100.0,
    reached BOOLEAN DEFAULT FALSE,
    reached_at BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_waypoint_mission FOREIGN KEY (mission_id) REFERENCES missions(id) ON DELETE CASCADE,
    CONSTRAINT unique_mission_sequence UNIQUE (mission_id, sequence_number)
);

-- Index pentru query-uri pe misiune
CREATE INDEX IF NOT EXISTS idx_waypoints_mission ON waypoints(mission_id, sequence_number);

-- =============================================================================
-- Tabelul: drone_events
-- Descriere: Stochează evenimente importante (alerte, erori, warning-uri)
-- =============================================================================
CREATE TABLE IF NOT EXISTS drone_events (
    id SERIAL PRIMARY KEY,
    drone_id VARCHAR(50) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) DEFAULT 'INFO' CHECK (severity IN ('INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    message TEXT,
    timestamp BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_drone FOREIGN KEY (drone_id) REFERENCES drones(id) ON DELETE CASCADE
);

-- Index pentru query-uri pe dronă
CREATE INDEX IF NOT EXISTS idx_events_drone ON drone_events(drone_id);

-- Index pentru query-uri pe severitate
CREATE INDEX IF NOT EXISTS idx_events_severity ON drone_events(severity);

-- Index pentru query-uri pe timestamp
CREATE INDEX IF NOT EXISTS idx_events_timestamp ON drone_events(timestamp DESC);

-- =============================================================================
-- Trigger pentru actualizare automată updated_at
-- =============================================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger pentru tabela drones
CREATE TRIGGER update_drones_updated_at BEFORE UPDATE ON drones
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger pentru tabela missions
CREATE TRIGGER update_missions_updated_at BEFORE UPDATE ON missions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- =============================================================================
-- View-uri utile pentru raportare
-- =============================================================================

-- View: Statistici generale per dronă
CREATE OR REPLACE VIEW drone_statistics AS
SELECT
    d.id,
    d.name,
    d.model,
    d.status,
    d.battery_level,
    COUNT(t.id) as total_telemetry_records,
    AVG(t.altitude) as avg_altitude,
    AVG(t.speed) as avg_speed,
    MAX(t.timestamp) as last_telemetry_timestamp,
    COUNT(DISTINCT m.id) as total_missions
FROM drones d
LEFT JOIN telemetry_logs t ON d.id = t.drone_id
LEFT JOIN missions m ON d.id = m.drone_id
GROUP BY d.id, d.name, d.model, d.status, d.battery_level;

-- View: Misiuni active cu detalii
CREATE OR REPLACE VIEW active_missions_view AS
SELECT
    m.id as mission_id,
    m.name as mission_name,
    m.description,
    m.drone_id,
    d.name as drone_name,
    m.status,
    m.start_time,
    COUNT(w.id) as total_waypoints,
    COUNT(w.id) FILTER (WHERE w.reached = TRUE) as completed_waypoints
FROM missions m
JOIN drones d ON m.drone_id = d.id
LEFT JOIN waypoints w ON m.id = w.mission_id
WHERE m.status IN ('PLANNED', 'ACTIVE')
GROUP BY m.id, m.name, m.description, m.drone_id, d.name, m.status, m.start_time;

-- View: Evenimente critice recente
CREATE OR REPLACE VIEW recent_critical_events AS
SELECT
    e.id,
    e.drone_id,
    d.name as drone_name,
    e.event_type,
    e.severity,
    e.message,
    e.timestamp,
    to_timestamp(e.timestamp / 1000) as event_time
FROM drone_events e
JOIN drones d ON e.drone_id = d.id
WHERE e.severity IN ('ERROR', 'CRITICAL')
ORDER BY e.timestamp DESC
LIMIT 50;

-- =============================================================================
-- Date inițiale de test (opțional)
-- =============================================================================
-- NOTĂ: Comentați secțiunea de mai jos dacă nu doriți date inițiale

-- Inserare drone de test
-- INSERT INTO drones (id, name, model, status, battery_level, last_seen) VALUES
--     ('DRONE-001', 'Alpha', 'DJI Mavic Pro', 'IDLE', 85.5, extract(epoch from now()) * 1000),
--     ('DRONE-002', 'Beta', 'DJI Phantom 4', 'IDLE', 92.0, extract(epoch from now()) * 1000),
--     ('DRONE-003', 'Gamma', 'Parrot Anafi', 'CHARGING', 45.0, extract(epoch from now()) * 1000),
--     ('DRONE-004', 'Delta', 'Autel EVO', 'IDLE', 78.0, extract(epoch from now()) * 1000),
--     ('DRONE-005', 'Epsilon', 'DJI Mini 3', 'IDLE', 100.0, extract(epoch from now()) * 1000)
-- ON CONFLICT (id) DO NOTHING;

-- Inserare misiune de test
-- INSERT INTO missions (name, description, drone_id, status, start_time) VALUES
--     ('Test Mission 1', 'Patrol zone A', 'DRONE-001', 'PLANNED', extract(epoch from now()) * 1000)
-- ON CONFLICT DO NOTHING;

-- Inserare waypoints pentru misiunea de test
-- INSERT INTO waypoints (mission_id, sequence_number, latitude, longitude, altitude) VALUES
--     (1, 0, 46.7712, 23.6236, 100.0),
--     (1, 1, 46.7750, 23.6250, 120.0),
--     (1, 2, 46.7780, 23.6280, 150.0),
--     (1, 3, 46.7712, 23.6236, 100.0)
-- ON CONFLICT (mission_id, sequence_number) DO NOTHING;

-- =============================================================================
-- Comentarii și documentație
-- =============================================================================

COMMENT ON TABLE drones IS 'Tabel principal pentru stocarea informațiilor despre drone-urile din flotă';
COMMENT ON TABLE telemetry_logs IS 'Telemetrie brută primită de la drone (GPS, baterie, senzori)';
COMMENT ON TABLE missions IS 'Misiuni planificate sau în desfășurare pentru drone';
COMMENT ON TABLE waypoints IS 'Puncte de navigație pentru fiecare misiune';
COMMENT ON TABLE drone_events IS 'Evenimente și alerte importante (low battery, erori, etc.)';

COMMENT ON COLUMN drones.last_seen IS 'Unix timestamp (milliseconds) al ultimei telemetrii primite';
COMMENT ON COLUMN telemetry_logs.timestamp IS 'Unix timestamp (milliseconds) când a fost generată telemetria';
COMMENT ON COLUMN missions.start_time IS 'Unix timestamp (milliseconds) când a început misiunea';
COMMENT ON COLUMN missions.end_time IS 'Unix timestamp (milliseconds) când s-a terminat misiunea';

-- =============================================================================
-- Query-uri utile pentru testare și debugging
-- =============================================================================

-- Verificare număr înregistrări per tabel
-- SELECT 'drones' as table_name, COUNT(*) as count FROM drones
-- UNION ALL
-- SELECT 'telemetry_logs', COUNT(*) FROM telemetry_logs
-- UNION ALL
-- SELECT 'missions', COUNT(*) FROM missions
-- UNION ALL
-- SELECT 'waypoints', COUNT(*) FROM waypoints
-- UNION ALL
-- SELECT 'drone_events', COUNT(*) FROM drone_events;

-- Query telemetrie recentă pentru toate drone-urile
-- SELECT
--     d.name,
--     t.timestamp,
--     t.latitude,
--     t.longitude,
--     t.altitude,
--     t.battery_level,
--     t.status
-- FROM telemetry_logs t
-- JOIN drones d ON t.drone_id = d.id
-- ORDER BY t.timestamp DESC
-- LIMIT 20;

-- Query drone cu baterie scăzută
-- SELECT id, name, battery_level, status
-- FROM drones
-- WHERE battery_level < 30
-- ORDER BY battery_level ASC;
