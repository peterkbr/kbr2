package hu.flexisys.kbr.view.tenyeszet;

import hu.flexisys.kbr.model.Tenyeszet;

import java.util.Date;

/**
 * Created by Peter on 2014.07.08..
 */
public class TenyeszetListModel {

    private Tenyeszet tenyeszet;
    private String telepules;
    private Integer egyedCount;
    private Integer selectedEgyedCount;
    private Integer biralatWaitingForUpload;

    public TenyeszetListModel(Tenyeszet tenyeszet) {
        this.tenyeszet = tenyeszet;
    }

    public TenyeszetListModel(Long TENAZ) {
        tenyeszet = new Tenyeszet(TENAZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TenyeszetListModel)) {
            return false;
        }
        TenyeszetListModel that = (TenyeszetListModel) o;
        if (tenyeszet != null ? !tenyeszet.equals(that.tenyeszet) : that.tenyeszet != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return tenyeszet != null ? tenyeszet.hashCode() : 0;
    }

    // GETTERS, SETTERS

    public Tenyeszet getTenyeszet() {
        return tenyeszet;
    }

    public void setTenyeszet(Tenyeszet tenyeszet) {
        this.tenyeszet = tenyeszet;
    }

    public Long getTENAZ() {
        return tenyeszet.getTENAZ();
    }

    public String getTARTO() {
        return tenyeszet.getTARTO();
    }

    public String getTECIM() {
        return tenyeszet.getTECIM();
    }

    public Date getLEDAT() {
        return tenyeszet.getLEDAT();
    }

    public Boolean getERVENYES() {
        return tenyeszet.getERVENYES();
    }

    public String getTelepules() {
        return telepules;
    }

    public void setTelepules(String telepules) {
        this.telepules = telepules;
    }

    public Integer getEgyedCount() {
        return egyedCount;
    }

    public void setEgyedCount(Integer egyedCount) {
        this.egyedCount = egyedCount;
    }

    public Integer getSelectedEgyedCount() {
        return selectedEgyedCount;
    }

    public void setSelectedEgyedCount(Integer selectedEgyedCount) {
        this.selectedEgyedCount = selectedEgyedCount;
    }

    public Integer getBiralatWaitingForUpload() {
        return biralatWaitingForUpload;
    }

    public void setBiralatWaitingForUpload(Integer biralatWaitingForUpload) {
        this.biralatWaitingForUpload = biralatWaitingForUpload;
    }
}
