UPDATE tb_absensi set version = 0 WHERE version IS null;

UPDATE tb_absensi_config set version = 0 WHERE version IS null;

UPDATE tb_barang set version = 0 WHERE version IS null;

UPDATE tb_jasa set version = 0 WHERE version IS null;

UPDATE tb_karyawan set version = 0 WHERE version IS null;

UPDATE tb_karyawan_posisi set version = 0 WHERE version IS null;

UPDATE tb_kendaraan set version = 0 WHERE version IS null;

UPDATE tb_pelanggan set version = 0 WHERE version IS null;

UPDATE tb_pembelian set version = 0 WHERE version IS null;

UPDATE tb_pembelian_barang_detail
set
    version = 0
WHERE
    version IS null;

UPDATE tb_pembelian_detail set version = 0 WHERE version IS null;

UPDATE tb_penjualan set version = 0 WHERE version IS null;

UPDATE tb_penjualan_detail set version = 0 WHERE version IS null;

UPDATE tb_sparepart set version = 0 WHERE version IS null;

UPDATE tb_spk set version = 0 WHERE version IS null;

UPDATE tb_spk_detail set version = 0 WHERE version IS null;

UPDATE tb_supplier set version = 0 WHERE version IS null;