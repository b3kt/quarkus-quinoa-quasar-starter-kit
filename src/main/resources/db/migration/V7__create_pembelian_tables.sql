-- Migration: Create tb_pembelian and tb_pembelian_detail tables for garage expenses management
-- Version: V7
-- Description: Creates new tables for managing both sparepart purchases and operational expenses
-- Author: System
-- Date: 2025-12-06

-- Create tb_pembelian table (main expense/purchase table)
CREATE SEQUENCE IF NOT EXISTS tb_pembelian_id_seq START
WITH
    1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS tb_pembelian (
    id BIGINT NOT NULL DEFAULT nextval('tb_pembelian_id_seq'),
    no_pembelian VARCHAR(15) NOT NULL,
    no_urut INTEGER,
    tgl_pembelian TIMESTAMP WITHOUT TIME ZONE,
    jenis_pembelian VARCHAR(20) NOT NULL,
    jenis_operasional VARCHAR(50),
    kategori_operasional VARCHAR(20),
    id_supplier BIGINT,
    grand_total NUMERIC(18, 2),
    jenis_pembayaran VARCHAR(20),
    status_pembayaran VARCHAR(20),
    metode_pembayaran VARCHAR(20),
    diskon NUMERIC(18, 2),
    ppn NUMERIC(18, 2),
    keterangan VARCHAR(500),
    id_karyawan BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR,
    version INTEGER,
    PRIMARY KEY (id),
    CONSTRAINT fk_pembelian_karyawan FOREIGN KEY (id_karyawan) REFERENCES tb_karyawan (id) ON DELETE SET NULL,
    CONSTRAINT chk_jenis_pembelian CHECK (
        jenis_pembelian IN ('SPAREPART', 'OPERASIONAL')
    ),
    CONSTRAINT chk_kategori_operasional CHECK (
        kategori_operasional IN (
            'DAILY',
            'WEEKLY',
            'MONTHLY',
            'YEARLY',
            'ON_DEMAND'
        )
    )
);

-- Create unique index on no_pembelian
CREATE UNIQUE INDEX IF NOT EXISTS idx_pembelian_no_pembelian ON tb_pembelian USING btree (no_pembelian);

-- Create indexes for filtering
CREATE INDEX IF NOT EXISTS idx_pembelian_jenis_pembelian ON tb_pembelian USING btree (jenis_pembelian);

CREATE INDEX IF NOT EXISTS idx_pembelian_kategori_operasional ON tb_pembelian USING btree (kategori_operasional);

CREATE INDEX IF NOT EXISTS idx_pembelian_jenis_operasional ON tb_pembelian USING btree (jenis_operasional);

CREATE INDEX IF NOT EXISTS idx_pembelian_tgl_pembelian ON tb_pembelian USING btree (tgl_pembelian);

-- Add comment to table
COMMENT ON TABLE tb_pembelian IS 'Main table for garage expenses including sparepart purchases and operational expenses';

COMMENT ON COLUMN tb_pembelian.jenis_pembelian IS 'Type of purchase: SPAREPART or OPERASIONAL';

COMMENT ON COLUMN tb_pembelian.jenis_operasional IS 'Description/type of operational expense (e.g., Electricity, Rent, Maintenance)';

COMMENT ON COLUMN tb_pembelian.kategori_operasional IS 'Frequency category for operational expenses: DAILY, WEEKLY, MONTHLY, YEARLY, ON_DEMAND';

COMMENT ON COLUMN tb_pembelian.id_supplier IS 'Foreign key to tb_supplier - required for SPAREPART, optional for OPERASIONAL';

-- Create tb_pembelian_detail table (line items table)
CREATE SEQUENCE IF NOT EXISTS tb_pembelian_detail_id_seq START
WITH
    1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS tb_pembelian_detail (
    id BIGINT NOT NULL DEFAULT nextval('tb_pembelian_detail_id_seq'),
    id_pembelian BIGINT NOT NULL,
    nama_item VARCHAR(100) NOT NULL,
    kategori_item VARCHAR(20),
    harga NUMERIC(18, 2) NOT NULL,
    kuantiti INTEGER NOT NULL,
    total NUMERIC(18, 2),
    keterangan VARCHAR(255),
    id_barang BIGINT,
    id_sparepart BIGINT,
    id_supplier BIGINT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR,
    version INTEGER,
    PRIMARY KEY (id),
    CONSTRAINT fk_pembelian_detail_pembelian FOREIGN KEY (id_pembelian) REFERENCES tb_pembelian (id) ON DELETE CASCADE,
    CONSTRAINT fk_pembelian_detail_barang FOREIGN KEY (id_barang) REFERENCES tb_barang (kode_barang) ON DELETE SET NULL,
    CONSTRAINT fk_pembelian_detail_sparepart FOREIGN KEY (id_sparepart) REFERENCES tb_sparepart (kode_barang) ON DELETE SET NULL,
    CONSTRAINT chk_kategori_item CHECK (
        kategori_item IN (
            'SPAREPART',
            'BARANG',
            'OPERASIONAL'
        )
    )
);

-- Create index on id_pembelian for faster joins
CREATE INDEX IF NOT EXISTS idx_pembelian_detail_id_pembelian ON tb_pembelian_detail USING btree (id_pembelian);

-- Add comments to detail table
COMMENT ON TABLE tb_pembelian_detail IS 'Detail line items for each purchase/expense in tb_pembelian';

COMMENT ON COLUMN tb_pembelian_detail.kategori_item IS 'Category of item: SPAREPART, BARANG, or OPERASIONAL';

COMMENT ON COLUMN tb_pembelian_detail.id_barang IS 'Foreign key to tb_barang - used when purchasing general goods';

COMMENT ON COLUMN tb_pembelian_detail.id_sparepart IS 'Foreign key to tb_sparepart - used when purchasing spareparts';