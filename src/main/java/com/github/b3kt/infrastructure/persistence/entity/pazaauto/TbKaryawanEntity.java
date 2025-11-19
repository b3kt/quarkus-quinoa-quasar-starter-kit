package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_karyawan")
public class TbKaryawanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nama_karyawan", length = 30, nullable = false)
    private String namaKaryawan;

    @Column(name = "alamat", length = 500)
    private String alamat;

    @Column(name = "no_tlpn", length = 15)
    private String noTlpn;

    @Column(name = "bergabung", length = 10)
    private String bergabung;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "version")
    private Integer version;

    @Column(name = "posisi_id")
    private Integer posisiId;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "jenis_kelamin", length = 1)
    private String jenisKelamin;

    @Column(name = "no_telepon", length = 20)
    private String noTelepon;

    @Column(name = "tanggal_bergabung")
    private LocalDate tanggalBergabung;

    @Column(name = "tanggal_lahir")
    private LocalDate tanggalLahir;

    @Column(name = "id_posisi")
    private Long idPosisi;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    public void setNamaKaryawan(String namaKaryawan) {
        this.namaKaryawan = namaKaryawan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoTlpn() {
        return noTlpn;
    }

    public void setNoTlpn(String noTlpn) {
        this.noTlpn = noTlpn;
    }

    public String getBergabung() {
        return bergabung;
    }

    public void setBergabung(String bergabung) {
        this.bergabung = bergabung;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getPosisiId() {
        return posisiId;
    }

    public void setPosisiId(Integer posisiId) {
        this.posisiId = posisiId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getNoTelepon() {
        return noTelepon;
    }

    public void setNoTelepon(String noTelepon) {
        this.noTelepon = noTelepon;
    }

    public LocalDate getTanggalBergabung() {
        return tanggalBergabung;
    }

    public void setTanggalBergabung(LocalDate tanggalBergabung) {
        this.tanggalBergabung = tanggalBergabung;
    }

    public LocalDate getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(LocalDate tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public Long getIdPosisi() {
        return idPosisi;
    }

    public void setIdPosisi(Long idPosisi) {
        this.idPosisi = idPosisi;
    }
}

