CREATE TABLE IF NOT EXISTS tb_barang (
    nama_barang varchar(100) NOT NULL DEFAULT ''::character varying,
    harga_jual numeric(18, 2),
    harga_beli numeric(18, 2),
    stock integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id SERIAL NOT NULL,
    keterangan varchar(500),
    satuan varchar(20),
    stok integer,
    stok_minimal integer,
    id_supplier bigint,
    is_active boolean,
    kode_barang varchar(20),
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS tb_barang_unique ON public.tb_barang USING btree (nama_barang);

CREATE TABLE IF NOT EXISTS tb_jasa (
    nama_jasa varchar(100) NOT NULL DEFAULT ''::character varying,
    harga_jasa integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id SERIAL NOT NULL,
    deskripsi varchar(500),
    estimasi_waktu integer,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_karyawan_posisi (
    posisi varchar(20) NOT NULL DEFAULT ''::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone DEFAULT now(),
    updated_by varchar,
    version integer,
    id SERIAL NOT NULL,
    keterangan varchar(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_karyawan (
    nama_karyawan varchar(30) NOT NULL DEFAULT ''::character varying,
    alamat varchar(500) DEFAULT NULL::character varying,
    no_tlpn varchar(15) DEFAULT NULL::character varying,
    bergabung varchar(10) DEFAULT NULL::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id SERIAL NOT NULL,
    posisi_id integer,
    email varchar(100),
    jenis_kelamin varchar(1),
    no_telepon varchar(20),
    tanggal_bergabung date,
    tanggal_lahir date,
    id_posisi bigint,
    PRIMARY KEY (id),
    CONSTRAINT fk9wntita4ehswbx769s7t8x9ew FOREIGN key (id_posisi) REFERENCES tb_karyawan_posisi (id)
);

CREATE TABLE IF NOT EXISTS tb_pelanggan (
    nopol varchar(10) NOT NULL DEFAULT ''::character varying,
    nama_pelanggan varchar(100) NOT NULL,
    alamat varchar(500) DEFAULT NULL::character varying,
    contact_person varchar(30) DEFAULT NULL::character varying,
    tlpn varchar(15) DEFAULT NULL::character varying,
    merk varchar(50) NOT NULL,
    jenis varchar(50) DEFAULT NULL::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id SERIAL NOT NULL,
    email varchar(100),
    jenis_kelamin varchar(1),
    keterangan varchar(500),
    kode_pos varchar(10),
    kota varchar(100),
    no_hp varchar(20),
    no_telepon varchar(20),
    tanggal_lahir date,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS tb_kendaraan (
    jenis varchar(50) NOT NULL DEFAULT ''::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id SERIAL NOT NULL,
    merk_id integer,
    keterangan varchar(500),
    model varchar(50),
    nomor_mesin varchar(50),
    nomor_rangka varchar(50),
    tahun_pembuatan varchar(4),
    warna varchar(30),
    id_pelanggan bigint,
    PRIMARY KEY (id),
    CONSTRAINT fkkck60xhnpch5ewpf6xqhvy7wa FOREIGN key (id_pelanggan) REFERENCES tb_pelanggan (id)
);

CREATE TABLE IF NOT EXISTS tb_pembelian_barang (
    no_pembelian varchar(15) NOT NULL,
    no_urut integer,
    tgl_pembelian varchar(25) DEFAULT NULL::character varying,
    nama_supplier varchar(30) NOT NULL,
    grand_total integer,
    jenis_pembayaran varchar(20) DEFAULT NULL::character varying,
    keterangan varchar(500) DEFAULT NULL::character varying,
    status_pembayaran varchar(20) DEFAULT NULL::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id uuid NOT NULL DEFAULT gen_random_uuid (),
    diskon numeric(18, 2),
    ppn numeric(18, 2),
    id_karyawan bigint,
    metode_pembayaran varchar(20),
    PRIMARY KEY (id),
    CONSTRAINT fkkpqjbdavx1jgfnba8508r4q1v FOREIGN key (id_karyawan) REFERENCES tb_karyawan (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS tb_pembelian_barang_unique ON public.tb_pembelian_barang USING btree (no_pembelian);

CREATE TABLE IF NOT EXISTS tb_pembelian_barang_detail (
    no_pembelian varchar(15) NOT NULL,
    nama_barang varchar(50) NOT NULL DEFAULT ''::character varying,
    harga_barang integer NOT NULL,
    kuantiti integer NOT NULL,
    total integer,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id uuid NOT NULL DEFAULT gen_random_uuid (),
    keterangan varchar(255),
    id_barang bigint,
    id_sparepart bigint,
    PRIMARY KEY (id),
    CONSTRAINT fk2ljbbkn57nav1owblfxlhsore FOREIGN key (id_barang) REFERENCES tb_barang (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS tb_pembelian_barang_detail_unique ON public.tb_pembelian_barang_detail USING btree (no_pembelian, nama_barang);

CREATE TABLE IF NOT EXISTS tb_penjualan (
    no_penjualan varchar(15) NOT NULL DEFAULT '0'::character varying,
    tgl_jam_penjualan varchar(25) DEFAULT NULL::character varying,
    no_spk varchar(11) DEFAULT NULL::character varying,
    grand_total integer,
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    diskon numeric(18, 2),
    kembalian numeric(18, 2),
    keterangan varchar(500),
    ppn numeric(18, 2),
    status_pembayaran varchar(20),
    uang_dibayar numeric(18, 2),
    id_karyawan bigint,
    id_kendaraan bigint,
    id_pelanggan bigint,
    metode_pembayaran varchar(20),
    PRIMARY KEY (no_penjualan),
    CONSTRAINT fk9lijudeqjcv7c4n5v0qekblfr FOREIGN key (id_karyawan) REFERENCES tb_karyawan (id),
    CONSTRAINT fksqr2fhquss0ushr24w310o639 FOREIGN key (id_kendaraan) REFERENCES tb_kendaraan (id),
    CONSTRAINT fkflou93np9aawcueagf56l7jwe FOREIGN key (id_pelanggan) REFERENCES tb_pelanggan (id)
);

CREATE TABLE IF NOT EXISTS tb_penjualan_detail (
    no_penjualan varchar(15) NOT NULL DEFAULT ''::character varying,
    kategori varchar(10) DEFAULT NULL::character varying,
    nama_jasa_barang varchar(40) NOT NULL DEFAULT ''::character varying,
    harga_jual integer NOT NULL,
    kuantiti integer NOT NULL,
    total integer,
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    diskon numeric(18, 2),
    keterangan varchar(255),
    id_barang bigint,
    id_jasa bigint,
    id_sparepart bigint,
    PRIMARY KEY (
        no_penjualan,
        nama_jasa_barang
    ),
    CONSTRAINT fkptcgvku6hqik9b87ymn2pm0ft FOREIGN key (id_barang) REFERENCES tb_barang (id),
    CONSTRAINT fkgaoscqcq4j14rcn9frbqm9mu1 FOREIGN key (id_jasa) REFERENCES tb_jasa (id)
);

CREATE TABLE IF NOT EXISTS tb_sparepart (
    kd_barang varchar(9) NOT NULL DEFAULT ''::character varying,
    nama_barang varchar(40) DEFAULT NULL::character varying,
    harga_beli numeric(18, 2),
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    is_active boolean NOT NULL,
    harga_jual numeric(18, 2),
    keterangan varchar(500),
    kode_sparepart varchar(20) NOT NULL,
    merek varchar(50),
    nama_sparepart varchar(100) NOT NULL,
    satuan varchar(20),
    stok integer,
    stok_minimal integer,
    tipe_kendaraan varchar(50),
    id_supplier bigint,
    PRIMARY KEY (kd_barang)
);

CREATE UNIQUE INDEX IF NOT EXISTS ukdaaw2mydabnpcnt6inochoidi ON public.tb_sparepart USING btree (kode_sparepart);

CREATE TABLE IF NOT EXISTS tb_spk (
    no_spk varchar(30) NOT NULL DEFAULT ''::character varying,
    no_antrian integer,
    tgl_jam_spk varchar(25) DEFAULT NULL::character varying,
    nopol varchar(10) DEFAULT NULL::character varying,
    nama_karyawan varchar(300) DEFAULT NULL::character varying,
    km integer,
    status_spk varchar(20) DEFAULT NULL::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    id SERIAL NOT NULL,
    diskon numeric(18, 2),
    keluhan varchar(1000),
    keterangan varchar(1000),
    km_saat_ini integer,
    ppn numeric(18, 2),
    status varchar(20),
    id_cs bigint,
    id_mekanik bigint,
    PRIMARY KEY (id),
    CONSTRAINT fkiq2mee88mqk33ql22i3mridnx FOREIGN key (id_cs) REFERENCES tb_karyawan (id),
    CONSTRAINT fkpo0b0fbr6jcrinyiwjtgt6dww FOREIGN key (id_mekanik) REFERENCES tb_karyawan (id)
);

CREATE TABLE IF NOT EXISTS tb_spk_detail (
    no_spk varchar(11) NOT NULL DEFAULT ''::character varying,
    nama_jasa varchar(40) NOT NULL DEFAULT ''::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    id bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    jumlah integer,
    keterangan varchar(500),
    id_jasa bigint,
    id_sparepart bigint,
    PRIMARY KEY (no_spk, nama_jasa),
    CONSTRAINT fk3l1ktvfgho91lpgd9qimcn1oh FOREIGN key (id_jasa) REFERENCES tb_jasa (id)
);

CREATE TABLE IF NOT EXISTS tb_supplier (
    nama_supplier varchar(100) NOT NULL DEFAULT ''::character varying,
    alamat varchar(500) DEFAULT NULL::character varying,
    tlpn varchar(13) DEFAULT NULL::character varying,
    detail_supplier varchar(100) DEFAULT NULL::character varying,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    created_by varchar,
    updated_at timestamp without time zone,
    updated_by varchar,
    version integer,
    id uuid NOT NULL DEFAULT gen_random_uuid (),
    email varchar(100),
    keterangan varchar(500),
    kode_pos varchar(10),
    kontak_person varchar(100),
    kota varchar(100),
    no_hp_kontak varchar(20),
    no_telepon varchar(20),
    PRIMARY KEY (id)
);