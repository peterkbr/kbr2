package hu.flexisys.kbr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class Egyed implements Serializable{

    private String AZONO;
    private String TENAZ;
    private String ORSKO;
    private Integer ELLSO;
    private Date ELLDA;
    private Date SZULD;
    private Integer FAJKO;
    private Integer KONSK;
    private Integer SZINE;
    private Boolean ITVJE;
    private Boolean KIVALASZTOTT;
    private Boolean UJ;

    private List<Biralat> biralatList;

    public Egyed() {
        biralatList = new ArrayList<Biralat>();
    }

    public void addBiralat(Biralat biralat) {
        biralatList.add(biralat);
    }

    // GETTERS, SETTERS

    public String getTENAZ() {
        return TENAZ;
    }

    public void setTENAZ(String TENAZ) {
        this.TENAZ = TENAZ;
    }

    public String getORSKO() {
        return ORSKO;
    }

    public void setORSKO(String ORSKO) {
        this.ORSKO = ORSKO;
    }

    public String getAZONO() {
        return AZONO;
    }

    public void setAZONO(String AZONO) {
        this.AZONO = AZONO;
    }

    public Integer getELLSO() {
        return ELLSO;
    }

    public void setELLSO(Integer ELLSO) {
        this.ELLSO = ELLSO;
    }

    public Date getELLDA() {
        return ELLDA;
    }

    public void setELLDA(Date ELLDA) {
        this.ELLDA = ELLDA;
    }

    public Date getSZULD() {
        return SZULD;
    }

    public void setSZULD(Date SZULD) {
        this.SZULD = SZULD;
    }

    public Integer getFAJKO() {
        return FAJKO;
    }

    public void setFAJKO(Integer FAJKO) {
        this.FAJKO = FAJKO;
    }

    public Integer getKONSK() {
        return KONSK;
    }

    public void setKONSK(Integer KONSK) {
        this.KONSK = KONSK;
    }

    public Integer getSZINE() {
        return SZINE;
    }

    public void setSZINE(Integer SZINE) {
        this.SZINE = SZINE;
    }

    public Boolean getITVJE() {
        return ITVJE;
    }

    public void setITVJE(Boolean ITVJE) {
        this.ITVJE = ITVJE;
    }

    public Boolean getKIVALASZTOTT() {
        return KIVALASZTOTT;
    }

    public void setKIVALASZTOTT(Boolean KIVALASZTOTT) {
        this.KIVALASZTOTT = KIVALASZTOTT;
    }

    public Boolean getUJ() {
        return UJ;
    }

    public void setUJ(Boolean UJ) {
        this.UJ = UJ;
    }

    public List<Biralat> getBiralatList() {
        return biralatList;
    }

    public void setBiralatList(List<Biralat> biralatList) {
        this.biralatList = biralatList;
    }
}
