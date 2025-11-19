package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_spk")
public class TbSpkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "no_spk", length = 30, nullable = false)
    private String noSpk;

    @Column(name = "no_antrian")
    private Integer noAntrian;

    @Column(name = "tgl_jam_spk", length = 25)
    private String tanggalJamSpk;

    @Column(name = "nopol", length = 10)
    private String nopol;

    @Column(name = "nama_karyawan", length = 300)
    private String namaKaryawan;

    @Column(name = "km")
    private Integer km;

    @Column(name = "status_spk", length = 20)
    private String statusSpk;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "diskon", precision = 18, scale = 2)
    private BigDecimal diskon;

    @Column(name = "keluhan", length = 1000)
    private String keluhan;

    @Column(name = "keterangan", length = 1000)
    private String keterangan;

    @Column(name = "km_saat_ini")
    private Integer kmSaatIni;

    @Column(name = "ppn", precision = 18, scale = 2)
    private BigDecimal ppn;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "id_cs")
    private Long csId;

    @Column(name = "id_mekanik")
    private Long mekanikId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoSpk() {
        return noSpk;
    }

    public void setNoSpk(String noSpk) {
        this.noSpk = noSpk;
    }

    public Integer getNoAntrian() {
        return noAntrian;
    }

    public void setNoAntrian(Integer noAntrian) {
        this.noAntrian = noAntrian;
    }

    public String getTanggalJamSpk() {
        return tanggalJamSpk;
    }

    public void setTanggalJamSpk(String tanggalJamSpk) {
        this.tanggalJamSpk = tanggalJamSpk;
    }

    public String getNopol() {
        return nopol;
    }

    public void setNopol(String nopol) {
        this.nopol = nopol;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public Integer getKm() {
        return km;
    }

    public void setKm(Integer km) {
        this.km = km;
    }

    public String getStatusSpk() {
        return statusSpk;
    }

    public void setStatusSpk(String statusSpk) {
        this.statusSpk = statusSpk;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public BigDecimal getDiskon() {
        return diskon;
    }

    public void setDiskon(BigDecimal diskon) {
        this.diskon = diskon;
    }

    public String getKeluhan() {
        return keluhan;
    }

    public void setKeluhan(String keluhan) {
        this.keluhan = keluhan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Integer getKmSaatIni() {
        return kmSaatIni;
    }

    public void setKmSaatIni(Integer kmSaatIni) {
        this.kmSaatIni = kmSaatIni;
    }

    public BigDecimal getPpn() {
        return ppn;
    }

    public void setPpn(BigDecimal ppn) {
        this.ppn = ppn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCsId() {
        return csId;
    }

    public void setCsId(Long csId) {
        this.csId = csId;
    }

    public Long getMekanikId() {
        return mekanikId;
    }

    public void setMekanikId(Long mekanikId) {
        this.mekanikId = mekanikId;
    }
}

