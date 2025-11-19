package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_sparepart")
public class TbSparepartEntity {

    @Id
    @Column(name = "kd_barang", length = 9)
    private String kodeBarang;

    @Column(name = "nama_barang", length = 40)
    private String namaBarang;

    @Column(name = "harga_beli", precision = 18, scale = 2)
    private BigDecimal hargaBeli;

    @Column(name = "id")
    private Long id;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "harga_jual", precision = 18, scale = 2)
    private BigDecimal hargaJual;

    @Column(name = "keterangan", length = 500)
    private String keterangan;

    @Column(name = "kode_sparepart", length = 20, nullable = false)
    private String kodeSparepart;

    @Column(name = "merek", length = 50)
    private String merek;

    @Column(name = "nama_sparepart", length = 100, nullable = false)
    private String namaSparepart;

    @Column(name = "satuan", length = 20)
    private String satuan;

    @Column(name = "stok")
    private Integer stok;

    @Column(name = "stok_minimal")
    private Integer stokMinimal;

    @Column(name = "tipe_kendaraan", length = 50)
    private String tipeKendaraan;

    @Column(name = "id_supplier")
    private Long supplierId;

    public String getKodeBarang() {
        return kodeBarang;
    }

    public void setKodeBarang(String kodeBarang) {
        this.kodeBarang = kodeBarang;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public BigDecimal getHargaBeli() {
        return hargaBeli;
    }

    public void setHargaBeli(BigDecimal hargaBeli) {
        this.hargaBeli = hargaBeli;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public BigDecimal getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(BigDecimal hargaJual) {
        this.hargaJual = hargaJual;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getKodeSparepart() {
        return kodeSparepart;
    }

    public void setKodeSparepart(String kodeSparepart) {
        this.kodeSparepart = kodeSparepart;
    }

    public String getMerek() {
        return merek;
    }

    public void setMerek(String merek) {
        this.merek = merek;
    }

    public String getNamaSparepart() {
        return namaSparepart;
    }

    public void setNamaSparepart(String namaSparepart) {
        this.namaSparepart = namaSparepart;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public Integer getStok() {
        return stok;
    }

    public void setStok(Integer stok) {
        this.stok = stok;
    }

    public Integer getStokMinimal() {
        return stokMinimal;
    }

    public void setStokMinimal(Integer stokMinimal) {
        this.stokMinimal = stokMinimal;
    }

    public String getTipeKendaraan() {
        return tipeKendaraan;
    }

    public void setTipeKendaraan(String tipeKendaraan) {
        this.tipeKendaraan = tipeKendaraan;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }
}

