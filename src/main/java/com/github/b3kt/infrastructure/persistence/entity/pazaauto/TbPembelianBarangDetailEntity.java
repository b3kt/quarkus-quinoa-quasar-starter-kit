package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_pembelian_barang_detail")
public class TbPembelianBarangDetailEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "no_pembelian", length = 15, nullable = false)
    private String noPembelian;

    @Column(name = "nama_barang", length = 50, nullable = false)
    private String namaBarang;

    @Column(name = "harga_barang", nullable = false)
    private Integer hargaBarang;

    @Column(name = "kuantiti", nullable = false)
    private Integer kuantiti;

    @Column(name = "total")
    private Integer total;

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

    @Column(name = "keterangan", length = 255)
    private String keterangan;

    @Column(name = "id_barang")
    private Long barangId;

    @Column(name = "id_sparepart")
    private Long sparepartId;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNoPembelian() {
        return noPembelian;
    }

    public void setNoPembelian(String noPembelian) {
        this.noPembelian = noPembelian;
    }

    public String getNamaBarang() {
        return namaBarang;
    }

    public void setNamaBarang(String namaBarang) {
        this.namaBarang = namaBarang;
    }

    public Integer getHargaBarang() {
        return hargaBarang;
    }

    public void setHargaBarang(Integer hargaBarang) {
        this.hargaBarang = hargaBarang;
    }

    public Integer getKuantiti() {
        return kuantiti;
    }

    public void setKuantiti(Integer kuantiti) {
        this.kuantiti = kuantiti;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
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

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public Long getBarangId() {
        return barangId;
    }

    public void setBarangId(Long barangId) {
        this.barangId = barangId;
    }

    public Long getSparepartId() {
        return sparepartId;
    }

    public void setSparepartId(Long sparepartId) {
        this.sparepartId = sparepartId;
    }
}

