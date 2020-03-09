package hu.flexisys.kbr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class Tenyeszet {

    private String TENAZ;
    private String TARTO;
    private String TECIM;
    private Date LEDAT;
    private Boolean ERVENYES;

    private List<Egyed> egyedList;

    public Tenyeszet() {
        egyedList = new ArrayList<Egyed>();
        ERVENYES = true;
    }

    public Tenyeszet(String TENAZ) {
        this();
        this.TENAZ = TENAZ;
    }

    public void addEgyed(Egyed egyed) {
        egyedList.add(egyed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tenyeszet)) {
            return false;
        }
        Tenyeszet tenyeszet = (Tenyeszet) o;
        if (!TENAZ.equals(tenyeszet.TENAZ)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return TENAZ.hashCode();
    }

    // GETTERS, SETTERS

    public String getTENAZ() {
        return TENAZ;
    }

    public void setTENAZ(String TENAZ) {
        this.TENAZ = TENAZ;
    }

    public String getTARTO() {
        return TARTO;
    }

    public void setTARTO(String TARTO) {
        this.TARTO = TARTO;
    }

    public String getTECIM() {
        return TECIM;
    }

    public void setTECIM(String TECIM) {
        this.TECIM = TECIM;
    }

    public Date getLEDAT() {
        return LEDAT;
    }

    public void setLEDAT(Date LEDAT) {
        this.LEDAT = LEDAT;
    }

    public Boolean getERVENYES() {
        return ERVENYES;
    }

    public void setERVENYES(Boolean ERVENYES) {
        this.ERVENYES = ERVENYES;
    }

    public List<Egyed> getEgyedList() {
        return egyedList;
    }

    public void setEgyedList(List<Egyed> egyedList) {
        this.egyedList = egyedList;
    }
}
