package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import com.github.b3kt.infrastructure.persistence.entity.BaseEntity;

@Entity
@Table(name = "tb_jasa")
@Getter
@Setter
public class TbJasaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tb_jasa_id_seq")
    @SequenceGenerator(name = "tb_jasa_id_seq", sequenceName = "tb_jasa_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nama_jasa", length = 100, nullable = false)
    private String namaJasa;

    @Column(name = "harga_jasa")
    private Integer hargaJasa;

    @Column(name = "deskripsi", length = 500)
    private String deskripsi;

    @Column(name = "estimasi_waktu")
    private Integer estimasiWaktu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaJasa() {
        return namaJasa;
    }

    public void setNamaJasa(String namaJasa) {
        this.namaJasa = namaJasa;
    }

    public Integer getHargaJasa() {
        return hargaJasa;
    }

    public void setHargaJasa(Integer hargaJasa) {
        this.hargaJasa = hargaJasa;
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

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Integer getEstimasiWaktu() {
        return estimasiWaktu;
    }

    public void setEstimasiWaktu(Integer estimasiWaktu) {
        this.estimasiWaktu = estimasiWaktu;
    }
}
