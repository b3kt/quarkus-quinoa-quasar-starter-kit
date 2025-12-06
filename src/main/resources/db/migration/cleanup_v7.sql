-- Cleanup script for V7 migration
-- Run this if you need to reset the pembelian tables

DROP TABLE IF EXISTS tb_pembelian_detail CASCADE;

DROP TABLE IF EXISTS tb_pembelian CASCADE;

DROP SEQUENCE IF EXISTS tb_pembelian_id_seq CASCADE;

DROP SEQUENCE IF EXISTS tb_pembelian_detail_id_seq CASCADE;

-- Remove the migration record from Flyway
DELETE FROM flyway_schema_history WHERE version = '7';