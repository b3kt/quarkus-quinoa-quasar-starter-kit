-- Create tb_absensi table for employee attendance tracking
CREATE TABLE IF NOT EXISTS tb_absensi (
    id BIGSERIAL PRIMARY KEY,
    karyawan_id BIGINT NOT NULL,
    tanggal DATE NOT NULL,
    jam_masuk TIME,
    jam_keluar TIME,
    status VARCHAR(20) NOT NULL DEFAULT 'HADIR',
    keterangan VARCHAR(500),
    terlambat BOOLEAN DEFAULT FALSE,
    pulang_cepat BOOLEAN DEFAULT FALSE,
    lembur INTEGER DEFAULT 0,
    lokasi_masuk VARCHAR(200),
    lokasi_keluar VARCHAR(200),
    ip_masuk VARCHAR(50),
    ip_keluar VARCHAR(50),
    device_info VARCHAR(500),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR,
    version INTEGER,
    CONSTRAINT fk_absensi_karyawan FOREIGN KEY (karyawan_id) REFERENCES tb_karyawan (id) ON DELETE CASCADE,
    CONSTRAINT unique_karyawan_tanggal UNIQUE (karyawan_id, tanggal)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_absensi_karyawan ON tb_absensi (karyawan_id);

CREATE INDEX IF NOT EXISTS idx_absensi_tanggal ON tb_absensi (tanggal);

CREATE INDEX IF NOT EXISTS idx_absensi_status ON tb_absensi (status);

-- Create tb_absensi_config table for system configuration
CREATE TABLE IF NOT EXISTS tb_absensi_config (
    id BIGSERIAL PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500),
    description VARCHAR(500),
    config_type VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR,
    version INTEGER
);

-- Insert default configuration values
INSERT INTO
    tb_absensi_config (
        config_key,
        config_value,
        description,
        config_type
    )
VALUES (
        'work.start.time',
        '08:00',
        'Work start time',
        'TIME'
    ),
    (
        'work.end.time',
        '17:00',
        'Work end time',
        'TIME'
    ),
    (
        'late.threshold.minutes',
        '15',
        'Late threshold in minutes',
        'INTEGER'
    ),
    (
        'early.leave.threshold.minutes',
        '15',
        'Early leave threshold in minutes',
        'INTEGER'
    ),
    (
        'ip.restriction.enabled',
        'false',
        'Enable IP restriction for clock-in/out',
        'BOOLEAN'
    ),
    (
        'allowed.ips',
        '',
        'Comma-separated list of allowed IP addresses',
        'IP_LIST'
    )
ON CONFLICT (config_key) DO NOTHING;

-- Add comments to tables
COMMENT ON TABLE tb_absensi IS 'Employee attendance records with clock-in/out times and status tracking';

COMMENT ON TABLE tb_absensi_config IS 'Attendance system configuration settings';