package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_penjualan")
public class TbPenjualanEntity {

    @Id
    @Column(name = "no_penjualan", length = 15)
    private String noPenjualan;

    @Column(name = "tgl_jam_penjualan", length = 25)
    private String tanggalJamPenjualan;

    @Column(name = "no_spk", length = 11)
    private String noSpk;

    @Column(name = "grand_total")
    private Integer grandTotal;

    @Column(name = "diskon", precision = 18, scale = 2)
    private BigDecimal diskon;

    @Column(name = "kembalian", precision = 18, scale = 2)
    private BigDecimal kembalian;

    @Column(name = "keterangan", length = 500)
    private String keterangan;

    @Column(name = "ppn", precision = 18, scale = 2)
    private BigDecimal ppn;

    @Column(name = "status_pembayaran", length = 20)
    private String statusPembayaran;

    @Column(name = "uang_dibayar", precision = 18, scale = 2)
    private BigDecimal uangDibayar;

    @Column(name = "id_karyawan")
    private Long karyawanId;

    @Column(name = "id_kendaraan")
    private Long kendaraanId;

    @Column(name = "id_pelanggan")
    private Long pelangganId;

    @Column(name = "metode_pembayaran", length = 20)
    private String metodePembayaran;

    public String getNoPenjualan() {
        return noPenjualan;
    }

    public void setNoPenjualan(String noPenjualan) {
        this.noPenjualan = noPenjualan;
    }

    public String getTanggalJamPenjualan() {
        return tanggalJamPenjualan;
    }

    public void setTanggalJamPenjualan(String tanggalJamPenjualan) {
        this.tanggalJamPenjualan = tanggalJamPenjualan;
    }

    public String getNoSpk() {
        return noSpk;
    }

    public void setNoSpk(String noSpk) {
        this.noSpk = noSpk;
    }

    public Integer getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Integer grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getDiskon() {
        return diskon;
    }

    public void setDiskon(BigDecimal diskon) {
        this.diskon = diskon;
    }

    public BigDecimal getKembalian() {
        return kembalian;
    }

    public void setKembalian(BigDecimal kembalian) {
        this.kembalian = kembalian;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public BigDecimal getPpn() {
        return ppn;
    }

    public void setPpn(BigDecimal ppn) {
        this.ppn = ppn;
    }

    public String getStatusPembayaran() {
        return statusPembayaran;
    }

    public void setStatusPembayaran(String statusPembayaran) {
        this.statusPembayaran = statusPembayaran;
    }

    public BigDecimal getUangDibayar() {
        return uangDibayar;
    }

    public void setUangDibayar(BigDecimal uangDibayar) {
        this.uangDibayar = uangDibayar;
    }

    public Long getKaryawanId() {
        return karyawanId;
    }

    public void setKaryawanId(Long karyawanId) {
        this.karyawanId = karyawanId;
    }

    public Long getKendaraanId() {
        return kendaraanId;
    }

    public void setKendaraanId(Long kendaraanId) {
        this.kendaraanId = kendaraanId;
    }

    public Long getPelangganId() {
        return pelangganId;
    }

    public void setPelangganId(Long pelangganId) {
        this.pelangganId = pelangganId;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public void setMetodePembayaran(String metodePembayaran) {
        this.metodePembayaran = metodePembayaran;
    }
}

