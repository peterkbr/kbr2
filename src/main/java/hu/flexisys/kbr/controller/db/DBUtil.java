package hu.flexisys.kbr.controller.db;

import android.content.ContentValues;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;

/**
 * Created by Peter on 2014.07.02..
 */
public class DBUtil {

    public static ContentValues mapTenyeszetToContentValues(Tenyeszet tenyeszet) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_TENYESZET_TENAZ, tenyeszet.getTENAZ());
        values.put(DBScripts.COLUMN_TENYESZET_TARTO, tenyeszet.getTARTO());
        values.put(DBScripts.COLUMN_TENYESZET_TECIM, tenyeszet.getTECIM());
        if (tenyeszet.getLEDAT() != null) {
            values.put(DBScripts.COLUMN_TENYESZET_LEDAT, tenyeszet.getLEDAT().getTime());
        }
        values.put(DBScripts.COLUMN_TENYESZET_ERVENYES, tenyeszet.getERVENYES());
        return values;
    }

    public static ContentValues mapEgyedToContentValues(Egyed egyed) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_EGYED_TENAZ, egyed.getTENAZ());
        values.put(DBScripts.COLUMN_EGYED_ORSKO, egyed.getORSKO());
        values.put(DBScripts.COLUMN_EGYED_AZONO, egyed.getAZONO());
        values.put(DBScripts.COLUMN_EGYED_ELLSO, egyed.getELLSO());
        if (egyed.getELLDA() != null) {
            values.put(DBScripts.COLUMN_EGYED_ELLDA, egyed.getELLDA().getTime());
        }
        if (egyed.getSZULD() != null) {
            values.put(DBScripts.COLUMN_EGYED_SZULD, egyed.getSZULD().getTime());
        }
        values.put(DBScripts.COLUMN_EGYED_FAJKO, egyed.getFAJKO());
        values.put(DBScripts.COLUMN_EGYED_KONSK, egyed.getKONSK());
        values.put(DBScripts.COLUMN_EGYED_SZINE, egyed.getSZINE());
        values.put(DBScripts.COLUMN_EGYED_ITVJE, egyed.getITVJE());
        values.put(DBScripts.COLUMN_EGYED_KIVALASZTOTT, egyed.getKIVALASZTOTT());
        return values;
    }

    public static ContentValues mapBiralatToContentValues(Biralat biralat) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_BIRALAT_TENAZ, biralat.getTENAZ());
        values.put(DBScripts.COLUMN_BIRALAT_ORSKO, biralat.getORSKO());
        values.put(DBScripts.COLUMN_BIRALAT_AZONO, biralat.getAZONO());
        values.put(DBScripts.COLUMN_BIRALAT_BIRDA, biralat.getBIRDA().getTime());
        values.put(DBScripts.COLUMN_BIRALAT_BIRTI, biralat.getBIRTI());
        values.put(DBScripts.COLUMN_BIRALAT_KULAZ, biralat.getKULAZ());
        values.put(DBScripts.COLUMN_BIRALAT_AKAKO, biralat.getAKAKO());
        values.put(DBScripts.COLUMN_BIRALAT_FELTOLTETLEN, biralat.getFELTOLTETLEN());
        values.put(DBScripts.COLUMN_BIRALAT_KOD01, biralat.getKOD01());
        values.put(DBScripts.COLUMN_BIRALAT_ERT01, biralat.getERT01());
        values.put(DBScripts.COLUMN_BIRALAT_KOD02, biralat.getKOD02());
        values.put(DBScripts.COLUMN_BIRALAT_ERT02, biralat.getERT02());
        values.put(DBScripts.COLUMN_BIRALAT_KOD03, biralat.getKOD03());
        values.put(DBScripts.COLUMN_BIRALAT_ERT03, biralat.getERT03());
        values.put(DBScripts.COLUMN_BIRALAT_KOD04, biralat.getKOD04());
        values.put(DBScripts.COLUMN_BIRALAT_ERT04, biralat.getERT04());
        values.put(DBScripts.COLUMN_BIRALAT_KOD05, biralat.getKOD05());
        values.put(DBScripts.COLUMN_BIRALAT_ERT05, biralat.getERT05());
        values.put(DBScripts.COLUMN_BIRALAT_KOD06, biralat.getKOD06());
        values.put(DBScripts.COLUMN_BIRALAT_ERT06, biralat.getERT06());
        values.put(DBScripts.COLUMN_BIRALAT_KOD07, biralat.getKOD07());
        values.put(DBScripts.COLUMN_BIRALAT_ERT07, biralat.getERT07());
        values.put(DBScripts.COLUMN_BIRALAT_KOD08, biralat.getKOD08());
        values.put(DBScripts.COLUMN_BIRALAT_ERT08, biralat.getERT08());
        values.put(DBScripts.COLUMN_BIRALAT_KOD09, biralat.getKOD09());
        values.put(DBScripts.COLUMN_BIRALAT_ERT09, biralat.getERT09());
        values.put(DBScripts.COLUMN_BIRALAT_KOD10, biralat.getKOD10());
        values.put(DBScripts.COLUMN_BIRALAT_ERT10, biralat.getERT10());
        values.put(DBScripts.COLUMN_BIRALAT_KOD11, biralat.getKOD11());
        values.put(DBScripts.COLUMN_BIRALAT_ERT11, biralat.getERT11());
        values.put(DBScripts.COLUMN_BIRALAT_KOD12, biralat.getKOD12());
        values.put(DBScripts.COLUMN_BIRALAT_ERT12, biralat.getERT12());
        values.put(DBScripts.COLUMN_BIRALAT_KOD13, biralat.getKOD13());
        values.put(DBScripts.COLUMN_BIRALAT_ERT13, biralat.getERT13());
        values.put(DBScripts.COLUMN_BIRALAT_KOD14, biralat.getKOD14());
        values.put(DBScripts.COLUMN_BIRALAT_ERT14, biralat.getERT14());
        values.put(DBScripts.COLUMN_BIRALAT_KOD15, biralat.getKOD15());
        values.put(DBScripts.COLUMN_BIRALAT_ERT15, biralat.getERT15());
        values.put(DBScripts.COLUMN_BIRALAT_KOD16, biralat.getKOD16());
        values.put(DBScripts.COLUMN_BIRALAT_ERT16, biralat.getERT16());
        values.put(DBScripts.COLUMN_BIRALAT_KOD17, biralat.getKOD17());
        values.put(DBScripts.COLUMN_BIRALAT_ERT17, biralat.getERT17());
        values.put(DBScripts.COLUMN_BIRALAT_KOD18, biralat.getKOD18());
        values.put(DBScripts.COLUMN_BIRALAT_ERT18, biralat.getERT18());
        values.put(DBScripts.COLUMN_BIRALAT_KOD19, biralat.getKOD19());
        values.put(DBScripts.COLUMN_BIRALAT_ERT19, biralat.getERT19());
        values.put(DBScripts.COLUMN_BIRALAT_KOD20, biralat.getKOD20());
        values.put(DBScripts.COLUMN_BIRALAT_ERT20, biralat.getERT20());
        values.put(DBScripts.COLUMN_BIRALAT_KOD21, biralat.getKOD21());
        values.put(DBScripts.COLUMN_BIRALAT_ERT21, biralat.getERT21());
        values.put(DBScripts.COLUMN_BIRALAT_KOD22, biralat.getKOD22());
        values.put(DBScripts.COLUMN_BIRALAT_ERT22, biralat.getERT22());
        values.put(DBScripts.COLUMN_BIRALAT_KOD23, biralat.getKOD23());
        values.put(DBScripts.COLUMN_BIRALAT_ERT23, biralat.getERT23());
        values.put(DBScripts.COLUMN_BIRALAT_KOD24, biralat.getKOD24());
        values.put(DBScripts.COLUMN_BIRALAT_ERT24, biralat.getERT24());
        values.put(DBScripts.COLUMN_BIRALAT_KOD25, biralat.getKOD25());
        values.put(DBScripts.COLUMN_BIRALAT_ERT25, biralat.getERT25());
        values.put(DBScripts.COLUMN_BIRALAT_KOD26, biralat.getKOD26());
        values.put(DBScripts.COLUMN_BIRALAT_ERT26, biralat.getERT26());
        values.put(DBScripts.COLUMN_BIRALAT_KOD27, biralat.getKOD27());
        values.put(DBScripts.COLUMN_BIRALAT_ERT27, biralat.getERT27());
        values.put(DBScripts.COLUMN_BIRALAT_KOD28, biralat.getKOD28());
        values.put(DBScripts.COLUMN_BIRALAT_ERT28, biralat.getERT28());
        values.put(DBScripts.COLUMN_BIRALAT_KOD29, biralat.getKOD29());
        values.put(DBScripts.COLUMN_BIRALAT_ERT29, biralat.getERT29());
        values.put(DBScripts.COLUMN_BIRALAT_KOD30, biralat.getKOD30());
        values.put(DBScripts.COLUMN_BIRALAT_ERT30, biralat.getERT30());
        return values;
    }

}
