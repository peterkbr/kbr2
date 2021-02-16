package hu.flexisys.kbr.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Peter on 2014.07.02..
 */
public class Biralat implements Serializable {

    private Long id;
    private String AZONO;
    private String TENAZ;
    private String ORSKO;
    private Date BIRDA;
    private Integer BIRTI;
    private String KULAZ;
    private Integer AKAKO;
    private Boolean FELTOLTETLEN;
    private Boolean EXPORTALT;
    private Boolean LETOLTOTT;
    private String MEGJEGYZES;
    private String KOD01;
    private String ERT01;
    private String KOD02;
    private String ERT02;
    private String KOD03;
    private String ERT03;
    private String KOD04;
    private String ERT04;
    private String KOD05;
    private String ERT05;
    private String KOD06;
    private String ERT06;
    private String KOD07;
    private String ERT07;
    private String KOD08;
    private String ERT08;
    private String KOD09;
    private String ERT09;
    private String KOD10;
    private String ERT10;
    private String KOD11;
    private String ERT11;
    private String KOD12;
    private String ERT12;
    private String KOD13;
    private String ERT13;
    private String KOD14;
    private String ERT14;
    private String KOD15;
    private String ERT15;
    private String KOD16;
    private String ERT16;
    private String KOD17;
    private String ERT17;
    private String KOD18;
    private String ERT18;
    private String KOD19;
    private String ERT19;
    private String KOD20;
    private String ERT20;
    private String KOD21;
    private String ERT21;
    private String KOD22;
    private String ERT22;
    private String KOD23;
    private String ERT23;
    private String KOD24;
    private String ERT24;
    private String KOD25;
    private String ERT25;
    private String KOD26;
    private String ERT26;
    private String KOD27;
    private String ERT27;
    private String KOD28;
    private String ERT28;
    private String KOD29;
    private String ERT29;
    private String KOD30;
    private String ERT30;

    public Biralat() {
        MEGJEGYZES = null;
    }

    public void setKodErtMap(Map<String, String> map) {
        for (String kod : map.keySet()) {
            setKodErtPair(kod, map.get(kod));
        }
    }

    public void setKodErtPair(String KOD, String ERT) {
        if (this.KOD01 == null) {
            this.KOD01 = KOD;
            this.ERT01 = ERT;
        } else if (this.KOD02 == null) {
            this.KOD02 = KOD;
            this.ERT02 = ERT;
        } else if (this.KOD03 == null) {
            this.KOD03 = KOD;
            this.ERT03 = ERT;
        } else if (this.KOD04 == null) {
            this.KOD04 = KOD;
            this.ERT04 = ERT;
        } else if (this.KOD05 == null) {
            this.KOD05 = KOD;
            this.ERT05 = ERT;
        } else if (this.KOD06 == null) {
            this.KOD06 = KOD;
            this.ERT06 = ERT;
        } else if (this.KOD07 == null) {
            this.KOD07 = KOD;
            this.ERT07 = ERT;
        } else if (this.KOD08 == null) {
            this.KOD08 = KOD;
            this.ERT08 = ERT;
        } else if (this.KOD09 == null) {
            this.KOD09 = KOD;
            this.ERT09 = ERT;
        } else if (this.KOD10 == null) {
            this.KOD10 = KOD;
            this.ERT10 = ERT;
        } else if (this.KOD11 == null) {
            this.KOD11 = KOD;
            this.ERT11 = ERT;
        } else if (this.KOD12 == null) {
            this.KOD12 = KOD;
            this.ERT12 = ERT;
        } else if (this.KOD13 == null) {
            this.KOD13 = KOD;
            this.ERT13 = ERT;
        } else if (this.KOD14 == null) {
            this.KOD14 = KOD;
            this.ERT14 = ERT;
        } else if (this.KOD15 == null) {
            this.KOD15 = KOD;
            this.ERT15 = ERT;
        } else if (this.KOD16 == null) {
            this.KOD16 = KOD;
            this.ERT16 = ERT;
        } else if (this.KOD17 == null) {
            this.KOD17 = KOD;
            this.ERT17 = ERT;
        } else if (this.KOD18 == null) {
            this.KOD18 = KOD;
            this.ERT18 = ERT;
        } else if (this.KOD19 == null) {
            this.KOD19 = KOD;
            this.ERT19 = ERT;
        } else if (this.KOD20 == null) {
            this.KOD20 = KOD;
            this.ERT20 = ERT;
        } else if (this.KOD21 == null) {
            this.KOD21 = KOD;
            this.ERT21 = ERT;
        } else if (this.KOD22 == null) {
            this.KOD22 = KOD;
            this.ERT22 = ERT;
        } else if (this.KOD23 == null) {
            this.KOD23 = KOD;
            this.ERT23 = ERT;
        } else if (this.KOD24 == null) {
            this.KOD24 = KOD;
            this.ERT24 = ERT;
        } else if (this.KOD25 == null) {
            this.KOD25 = KOD;
            this.ERT25 = ERT;
        } else if (this.KOD26 == null) {
            this.KOD26 = KOD;
            this.ERT26 = ERT;
        } else if (this.KOD27 == null) {
            this.KOD27 = KOD;
            this.ERT27 = ERT;
        } else if (this.KOD28 == null) {
            this.KOD28 = KOD;
            this.ERT28 = ERT;
        } else if (this.KOD29 == null) {
            this.KOD29 = KOD;
            this.ERT29 = ERT;
        } else if (this.KOD30 == null) {
            this.KOD30 = KOD;
            this.ERT30 = ERT;
        }
    }

    public String getErtByKod(String KOD) {
        String ERT = null;
        if (KOD.equals(KOD01)) {
            ERT = ERT01;
        } else if (KOD.equals(KOD02)) {
            ERT = ERT02;
        } else if (KOD.equals(KOD03)) {
            ERT = ERT03;
        } else if (KOD.equals(KOD04)) {
            ERT = ERT04;
        } else if (KOD.equals(KOD05)) {
            ERT = ERT05;
        } else if (KOD.equals(KOD06)) {
            ERT = ERT06;
        } else if (KOD.equals(KOD07)) {
            ERT = ERT07;
        } else if (KOD.equals(KOD08)) {
            ERT = ERT08;
        } else if (KOD.equals(KOD09)) {
            ERT = ERT09;
        } else if (KOD.equals(KOD10)) {
            ERT = ERT10;
        } else if (KOD.equals(KOD11)) {
            ERT = ERT11;
        } else if (KOD.equals(KOD12)) {
            ERT = ERT12;
        } else if (KOD.equals(KOD13)) {
            ERT = ERT13;
        } else if (KOD.equals(KOD14)) {
            ERT = ERT14;
        } else if (KOD.equals(KOD15)) {
            ERT = ERT15;
        } else if (KOD.equals(KOD16)) {
            ERT = ERT16;
        } else if (KOD.equals(KOD17)) {
            ERT = ERT17;
        } else if (KOD.equals(KOD18)) {
            ERT = ERT18;
        } else if (KOD.equals(KOD19)) {
            ERT = ERT19;
        } else if (KOD.equals(KOD20)) {
            ERT = ERT20;
        } else if (KOD.equals(KOD21)) {
            ERT = ERT21;
        } else if (KOD.equals(KOD22)) {
            ERT = ERT22;
        } else if (KOD.equals(KOD23)) {
            ERT = ERT23;
        } else if (KOD.equals(KOD24)) {
            ERT = ERT24;
        } else if (KOD.equals(KOD25)) {
            ERT = ERT25;
        } else if (KOD.equals(KOD26)) {
            ERT = ERT26;
        } else if (KOD.equals(KOD27)) {
            ERT = ERT27;
        } else if (KOD.equals(KOD28)) {
            ERT = ERT28;
        } else if (KOD.equals(KOD29)) {
            ERT = ERT29;
        } else if (KOD.equals(KOD30)) {
            ERT = ERT30;
        }
        return ERT;
    }

    // GETTERS, SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getBIRDA() {
        return BIRDA;
    }

    public void setBIRDA(Date BIRDA) {
        this.BIRDA = BIRDA;
    }

    public Integer getBIRTI() {
        return BIRTI;
    }

    public void setBIRTI(Integer BIRTI) {
        this.BIRTI = BIRTI;
    }

    public String getKULAZ() {
        return KULAZ;
    }

    public void setKULAZ(String KULAZ) {
        this.KULAZ = KULAZ;
    }

    public Integer getAKAKO() {
        return AKAKO;
    }

    public void setAKAKO(Integer AKAKO) {
        this.AKAKO = AKAKO;
    }

    public Boolean getFELTOLTETLEN() {
        return FELTOLTETLEN;
    }

    public void setFELTOLTETLEN(Boolean FELTOLTETLEN) {
        this.FELTOLTETLEN = FELTOLTETLEN;
    }

    public Boolean getEXPORTALT() {
        return EXPORTALT;
    }

    public void setEXPORTALT(Boolean EXPORTALT) {
        this.EXPORTALT = EXPORTALT;
    }

    public Boolean getLETOLTOTT() {
        return LETOLTOTT;
    }

    public void setLETOLTOTT(Boolean LETOLTOTT) {
        this.LETOLTOTT = LETOLTOTT;
    }

    public String getMEGJEGYZES() {
        return MEGJEGYZES;
    }

    public void setMEGJEGYZES(String MEGJEGYZES) {
        this.MEGJEGYZES = MEGJEGYZES;
    }

    public String getKOD01() {
        return KOD01;
    }

    public void setKOD01(String KOD01) {
        this.KOD01 = KOD01;
    }

    public String getERT01() {
        return ERT01;
    }

    public void setERT01(String ERT01) {
        this.ERT01 = ERT01;
    }

    public String getKOD02() {
        return KOD02;
    }

    public void setKOD02(String KOD02) {
        this.KOD02 = KOD02;
    }

    public String getERT02() {
        return ERT02;
    }

    public void setERT02(String ERT02) {
        this.ERT02 = ERT02;
    }

    public String getKOD03() {
        return KOD03;
    }

    public void setKOD03(String KOD03) {
        this.KOD03 = KOD03;
    }

    public String getERT03() {
        return ERT03;
    }

    public void setERT03(String ERT03) {
        this.ERT03 = ERT03;
    }

    public String getKOD04() {
        return KOD04;
    }

    public void setKOD04(String KOD04) {
        this.KOD04 = KOD04;
    }

    public String getERT04() {
        return ERT04;
    }

    public void setERT04(String ERT04) {
        this.ERT04 = ERT04;
    }

    public String getKOD05() {
        return KOD05;
    }

    public void setKOD05(String KOD05) {
        this.KOD05 = KOD05;
    }

    public String getERT05() {
        return ERT05;
    }

    public void setERT05(String ERT05) {
        this.ERT05 = ERT05;
    }

    public String getKOD06() {
        return KOD06;
    }

    public void setKOD06(String KOD06) {
        this.KOD06 = KOD06;
    }

    public String getERT06() {
        return ERT06;
    }

    public void setERT06(String ERT06) {
        this.ERT06 = ERT06;
    }

    public String getKOD07() {
        return KOD07;
    }

    public void setKOD07(String KOD07) {
        this.KOD07 = KOD07;
    }

    public String getERT07() {
        return ERT07;
    }

    public void setERT07(String ERT07) {
        this.ERT07 = ERT07;
    }

    public String getKOD08() {
        return KOD08;
    }

    public void setKOD08(String KOD08) {
        this.KOD08 = KOD08;
    }

    public String getERT08() {
        return ERT08;
    }

    public void setERT08(String ERT08) {
        this.ERT08 = ERT08;
    }

    public String getKOD09() {
        return KOD09;
    }

    public void setKOD09(String KOD09) {
        this.KOD09 = KOD09;
    }

    public String getERT09() {
        return ERT09;
    }

    public void setERT09(String ERT09) {
        this.ERT09 = ERT09;
    }

    public String getKOD10() {
        return KOD10;
    }

    public void setKOD10(String KOD10) {
        this.KOD10 = KOD10;
    }

    public String getERT10() {
        return ERT10;
    }

    public void setERT10(String ERT10) {
        this.ERT10 = ERT10;
    }

    public String getKOD11() {
        return KOD11;
    }

    public void setKOD11(String KOD11) {
        this.KOD11 = KOD11;
    }

    public String getERT11() {
        return ERT11;
    }

    public void setERT11(String ERT11) {
        this.ERT11 = ERT11;
    }

    public String getKOD12() {
        return KOD12;
    }

    public void setKOD12(String KOD12) {
        this.KOD12 = KOD12;
    }

    public String getERT12() {
        return ERT12;
    }

    public void setERT12(String ERT12) {
        this.ERT12 = ERT12;
    }

    public String getKOD13() {
        return KOD13;
    }

    public void setKOD13(String KOD13) {
        this.KOD13 = KOD13;
    }

    public String getERT13() {
        return ERT13;
    }

    public void setERT13(String ERT13) {
        this.ERT13 = ERT13;
    }

    public String getKOD14() {
        return KOD14;
    }

    public void setKOD14(String KOD14) {
        this.KOD14 = KOD14;
    }

    public String getERT14() {
        return ERT14;
    }

    public void setERT14(String ERT14) {
        this.ERT14 = ERT14;
    }

    public String getKOD15() {
        return KOD15;
    }

    public void setKOD15(String KOD15) {
        this.KOD15 = KOD15;
    }

    public String getERT15() {
        return ERT15;
    }

    public void setERT15(String ERT15) {
        this.ERT15 = ERT15;
    }

    public String getKOD16() {
        return KOD16;
    }

    public void setKOD16(String KOD16) {
        this.KOD16 = KOD16;
    }

    public String getERT16() {
        return ERT16;
    }

    public void setERT16(String ERT16) {
        this.ERT16 = ERT16;
    }

    public String getKOD17() {
        return KOD17;
    }

    public void setKOD17(String KOD17) {
        this.KOD17 = KOD17;
    }

    public String getERT17() {
        return ERT17;
    }

    public void setERT17(String ERT17) {
        this.ERT17 = ERT17;
    }

    public String getKOD18() {
        return KOD18;
    }

    public void setKOD18(String KOD18) {
        this.KOD18 = KOD18;
    }

    public String getERT18() {
        return ERT18;
    }

    public void setERT18(String ERT18) {
        this.ERT18 = ERT18;
    }

    public String getKOD19() {
        return KOD19;
    }

    public void setKOD19(String KOD19) {
        this.KOD19 = KOD19;
    }

    public String getERT19() {
        return ERT19;
    }

    public void setERT19(String ERT19) {
        this.ERT19 = ERT19;
    }

    public String getKOD20() {
        return KOD20;
    }

    public void setKOD20(String KOD20) {
        this.KOD20 = KOD20;
    }

    public String getERT20() {
        return ERT20;
    }

    public void setERT20(String ERT20) {
        this.ERT20 = ERT20;
    }

    public String getKOD21() {
        return KOD21;
    }

    public void setKOD21(String KOD21) {
        this.KOD21 = KOD21;
    }

    public String getERT21() {
        return ERT21;
    }

    public void setERT21(String ERT21) {
        this.ERT21 = ERT21;
    }

    public String getKOD22() {
        return KOD22;
    }

    public void setKOD22(String KOD22) {
        this.KOD22 = KOD22;
    }

    public String getERT22() {
        return ERT22;
    }

    public void setERT22(String ERT22) {
        this.ERT22 = ERT22;
    }

    public String getKOD23() {
        return KOD23;
    }

    public void setKOD23(String KOD23) {
        this.KOD23 = KOD23;
    }

    public String getERT23() {
        return ERT23;
    }

    public void setERT23(String ERT23) {
        this.ERT23 = ERT23;
    }

    public String getKOD24() {
        return KOD24;
    }

    public void setKOD24(String KOD24) {
        this.KOD24 = KOD24;
    }

    public String getERT24() {
        return ERT24;
    }

    public void setERT24(String ERT24) {
        this.ERT24 = ERT24;
    }

    public String getKOD25() {
        return KOD25;
    }

    public void setKOD25(String KOD25) {
        this.KOD25 = KOD25;
    }

    public String getERT25() {
        return ERT25;
    }

    public void setERT25(String ERT25) {
        this.ERT25 = ERT25;
    }

    public String getKOD26() {
        return KOD26;
    }

    public void setKOD26(String KOD26) {
        this.KOD26 = KOD26;
    }

    public String getERT26() {
        return ERT26;
    }

    public void setERT26(String ERT26) {
        this.ERT26 = ERT26;
    }

    public String getKOD27() {
        return KOD27;
    }

    public void setKOD27(String KOD27) {
        this.KOD27 = KOD27;
    }

    public String getERT27() {
        return ERT27;
    }

    public void setERT27(String ERT27) {
        this.ERT27 = ERT27;
    }

    public String getKOD28() {
        return KOD28;
    }

    public void setKOD28(String KOD28) {
        this.KOD28 = KOD28;
    }

    public String getERT28() {
        return ERT28;
    }

    public void setERT28(String ERT28) {
        this.ERT28 = ERT28;
    }

    public String getKOD29() {
        return KOD29;
    }

    public void setKOD29(String KOD29) {
        this.KOD29 = KOD29;
    }

    public String getERT29() {
        return ERT29;
    }

    public void setERT29(String ERT29) {
        this.ERT29 = ERT29;
    }

    public String getKOD30() {
        return KOD30;
    }

    public void setKOD30(String KOD30) {
        this.KOD30 = KOD30;
    }

    public String getERT30() {
        return ERT30;
    }

    public void setERT30(String ERT30) {
        this.ERT30 = ERT30;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Biralat biralat = (Biralat) o;
        return Objects.equals(id, biralat.id) &&
                Objects.equals(AZONO, biralat.AZONO) &&
                Objects.equals(TENAZ, biralat.TENAZ) &&
                Objects.equals(ORSKO, biralat.ORSKO) &&
                Objects.equals(BIRDA, biralat.BIRDA) &&
                Objects.equals(BIRTI, biralat.BIRTI) &&
                Objects.equals(KULAZ, biralat.KULAZ) &&
                Objects.equals(AKAKO, biralat.AKAKO) &&
                Objects.equals(FELTOLTETLEN, biralat.FELTOLTETLEN) &&
                Objects.equals(EXPORTALT, biralat.EXPORTALT) &&
                Objects.equals(LETOLTOTT, biralat.LETOLTOTT) &&
                Objects.equals(MEGJEGYZES, biralat.MEGJEGYZES) &&
                Objects.equals(KOD01, biralat.KOD01) &&
                Objects.equals(ERT01, biralat.ERT01) &&
                Objects.equals(KOD02, biralat.KOD02) &&
                Objects.equals(ERT02, biralat.ERT02) &&
                Objects.equals(KOD03, biralat.KOD03) &&
                Objects.equals(ERT03, biralat.ERT03) &&
                Objects.equals(KOD04, biralat.KOD04) &&
                Objects.equals(ERT04, biralat.ERT04) &&
                Objects.equals(KOD05, biralat.KOD05) &&
                Objects.equals(ERT05, biralat.ERT05) &&
                Objects.equals(KOD06, biralat.KOD06) &&
                Objects.equals(ERT06, biralat.ERT06) &&
                Objects.equals(KOD07, biralat.KOD07) &&
                Objects.equals(ERT07, biralat.ERT07) &&
                Objects.equals(KOD08, biralat.KOD08) &&
                Objects.equals(ERT08, biralat.ERT08) &&
                Objects.equals(KOD09, biralat.KOD09) &&
                Objects.equals(ERT09, biralat.ERT09) &&
                Objects.equals(KOD10, biralat.KOD10) &&
                Objects.equals(ERT10, biralat.ERT10) &&
                Objects.equals(KOD11, biralat.KOD11) &&
                Objects.equals(ERT11, biralat.ERT11) &&
                Objects.equals(KOD12, biralat.KOD12) &&
                Objects.equals(ERT12, biralat.ERT12) &&
                Objects.equals(KOD13, biralat.KOD13) &&
                Objects.equals(ERT13, biralat.ERT13) &&
                Objects.equals(KOD14, biralat.KOD14) &&
                Objects.equals(ERT14, biralat.ERT14) &&
                Objects.equals(KOD15, biralat.KOD15) &&
                Objects.equals(ERT15, biralat.ERT15) &&
                Objects.equals(KOD16, biralat.KOD16) &&
                Objects.equals(ERT16, biralat.ERT16) &&
                Objects.equals(KOD17, biralat.KOD17) &&
                Objects.equals(ERT17, biralat.ERT17) &&
                Objects.equals(KOD18, biralat.KOD18) &&
                Objects.equals(ERT18, biralat.ERT18) &&
                Objects.equals(KOD19, biralat.KOD19) &&
                Objects.equals(ERT19, biralat.ERT19) &&
                Objects.equals(KOD20, biralat.KOD20) &&
                Objects.equals(ERT20, biralat.ERT20) &&
                Objects.equals(KOD21, biralat.KOD21) &&
                Objects.equals(ERT21, biralat.ERT21) &&
                Objects.equals(KOD22, biralat.KOD22) &&
                Objects.equals(ERT22, biralat.ERT22) &&
                Objects.equals(KOD23, biralat.KOD23) &&
                Objects.equals(ERT23, biralat.ERT23) &&
                Objects.equals(KOD24, biralat.KOD24) &&
                Objects.equals(ERT24, biralat.ERT24) &&
                Objects.equals(KOD25, biralat.KOD25) &&
                Objects.equals(ERT25, biralat.ERT25) &&
                Objects.equals(KOD26, biralat.KOD26) &&
                Objects.equals(ERT26, biralat.ERT26) &&
                Objects.equals(KOD27, biralat.KOD27) &&
                Objects.equals(ERT27, biralat.ERT27) &&
                Objects.equals(KOD28, biralat.KOD28) &&
                Objects.equals(ERT28, biralat.ERT28) &&
                Objects.equals(KOD29, biralat.KOD29) &&
                Objects.equals(ERT29, biralat.ERT29) &&
                Objects.equals(KOD30, biralat.KOD30) &&
                Objects.equals(ERT30, biralat.ERT30);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, AZONO, TENAZ, ORSKO, BIRDA, BIRTI, KULAZ, AKAKO, FELTOLTETLEN, EXPORTALT, LETOLTOTT, MEGJEGYZES, KOD01, ERT01, KOD02, ERT02, KOD03, ERT03, KOD04, ERT04, KOD05, ERT05, KOD06, ERT06, KOD07, ERT07, KOD08, ERT08, KOD09, ERT09, KOD10, ERT10, KOD11, ERT11, KOD12, ERT12, KOD13, ERT13, KOD14, ERT14, KOD15, ERT15, KOD16, ERT16, KOD17, ERT17, KOD18, ERT18, KOD19, ERT19, KOD20, ERT20, KOD21, ERT21, KOD22, ERT22, KOD23, ERT23, KOD24, ERT24, KOD25, ERT25, KOD26, ERT26, KOD27, ERT27, KOD28, ERT28, KOD29, ERT29, KOD30, ERT30);
    }
}
