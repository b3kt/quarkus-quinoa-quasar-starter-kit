package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_penjualan_detail")
public class TbPenjualanDetailEntity {

    @EmbeddedId
    private TbPenjualanDetailId id;

    @Column(name = "kategori", length = 10)
    private String kategori;

    @Column(name = "harga_jual", nullable = false)
    private Integer hargaJual;

    @Column(name = "kuantiti", nullable = false)
    private Integer kuantiti;

    @Column(name = "total")
    private Integer total;

    @Column(name = "diskon", precision = 18, scale = 2)
    private BigDecimal diskon;

    @Column(name = "keterangan", length = 255)
    private String keterangan;

    @Column(name = "id_barang")
    private Long barangId;

    @Column(name = "id_jasa")
    private Long jasaId;

    @Column(name = "id_sparepart")
    private Long sparepartId;

    public TbPenjualanDetailId getId() {
        return id;
    }

    public void setId(TbPenjualanDetailId id) {
        this.id = id;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public Integer getHargaJual() {
        return hargaJual;
    }

    public void setHargaJual(Integer hargaJual) {
        this.hargaJual = hargaJual;
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

    public BigDecimal getDiskon() {
        return diskon;
    }

    public void setDiskon(BigDecimal diskon) {
        this.diskon = diskon;
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

    public Long getJasaId() {
        return jasaId;
    }

    public void setJasaId(Long jasaId) {
        this.jasaId = jasaId;
    }

    public Long getSparepartId() {
        return sparepartId;
    }

    public void setSparepartId(Long sparepartId) {
        this.sparepartId = sparepartId;
    }
}

