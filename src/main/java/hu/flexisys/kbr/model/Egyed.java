package hu.flexisys.kbr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class Egyed {

    private Long AZONO;
    private Long TENAZ;
    private String ORSKO;
    private Integer ELLSO;
    private Date ELLDA;
    private Date SZULD;
    private Integer FAJKO;
    private Integer KONSK;
    private Integer SZINE;
    private String ITVJE;
    private Boolean KIVALASZTOTT;

    private List<Biralat> biralatList;

    public Egyed() {
        biralatList = new ArrayList<Biralat>();
    }

    public void addBiralat(Biralat biralat) {
        biralatList.add(biralat);
    }

    // GETTERS, SETTERS

    public Long getTENAZ() {
        return TENAZ;
    }

    public void setTENAZ(Long TENAZ) {
        this.TENAZ = TENAZ;
    }

    public String getORSKO() {
        return ORSKO;
    }

    public void setORSKO(String ORSKO) {
        this.ORSKO = ORSKO;
    }

    public Long getAZONO() {
        return AZONO;
    }

    public void setAZONO(Long AZONO) {
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

    public String getITVJE() {
        return ITVJE;
    }

    public void setITVJE(String ITVJE) {
        this.ITVJE = ITVJE;
    }

    public Boolean getKIVALASZTOTT() {
        return KIVALASZTOTT;
    }

    public void setKIVALASZTOTT(Boolean KIVALASZTOTT) {
        this.KIVALASZTOTT = KIVALASZTOTT;
    }

    public List<Biralat> getBiralatList() {
        return biralatList;
    }

    public void setBiralatList(List<Biralat> biralatList) {
        this.biralatList = biralatList;
    }
}
