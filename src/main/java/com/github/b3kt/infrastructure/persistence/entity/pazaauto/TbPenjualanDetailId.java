package com.github.b3kt.infrastructure.persistence.entity.pazaauto;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TbPenjualanDetailId implements Serializable {

    @Column(name = "no_penjualan", length = 15)
    private String noPenjualan;

    @Column(name = "nama_jasa_barang", length = 40)
    private String namaJasaBarang;

    public TbPenjualanDetailId() {
    }

    public TbPenjualanDetailId(String noPenjualan, String namaJasaBarang) {
        this.noPenjualan = noPenjualan;
        this.namaJasaBarang = namaJasaBarang;
    }

    public String getNoPenjualan() {
        return noPenjualan;
    }

    public void setNoPenjualan(String noPenjualan) {
        this.noPenjualan = noPenjualan;
    }

    public String getNamaJasaBarang() {
        return namaJasaBarang;
    }

    public void setNamaJasaBarang(String namaJasaBarang) {
        this.namaJasaBarang = namaJasaBarang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TbPenjualanDetailId that = (TbPenjualanDetailId) o;
        return Objects.equals(noPenjualan, that.noPenjualan) &&
                Objects.equals(namaJasaBarang, that.namaJasaBarang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noPenjualan, namaJasaBarang);
    }
}

