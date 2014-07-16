package hu.flexisys.kbr.controller.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class DBConnector {

    private DBOpenHelper openHelper;
    private SQLiteDatabase database;

    public DBConnector(Context context, String path, int version) {
        openHelper = new DBOpenHelper(context, path, version);
        database = openHelper.getWritableDatabase();
    }

    // TODO log
    public long addTenyeszet(Tenyeszet tenyeszet) {
        ContentValues values = DBUtil.mapTenyeszetToContentValues(tenyeszet);
        long insertId = database.insert(DBScripts.TABLE_TENYESZET, null, values);
        return insertId;
    }

    // TODO log
    public int removeTenyeszet(Long TENAZ) {
        int removedCount = database.delete(DBScripts.TABLE_TENYESZET, DBScripts.COLUMN_TENYESZET_TENAZ + " = " + TENAZ, null);
        return removedCount;
    }

    public int removeSelectionFromTenyeszet(Long TENAZ) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_EGYED_KIVALASZTOTT, false);
        int count = database.update(DBScripts.TABLE_EGYED, values, DBScripts.COLUMN_EGYED_TENAZ + " = " + TENAZ, null);
        return count;
    }

    public int updateTenyeszet(Tenyeszet tenyeszet) {
        ContentValues values = DBUtil.mapTenyeszetToContentValues(tenyeszet);
        int count = database.update(DBScripts.TABLE_TENYESZET, values, DBScripts.COLUMN_TENYESZET_TENAZ + " = " + tenyeszet.getTENAZ(), null);
        return count;
    }

    public int updateTenyeszetByTENAZWithERVENYES(Long TENAZ, Boolean ERVENYES) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_TENYESZET_ERVENYES, ERVENYES);
        int count = database.update(DBScripts.TABLE_TENYESZET, values, DBScripts.COLUMN_TENYESZET_TENAZ + " = " + TENAZ, null);
        return count;
    }

    public List<Tenyeszet> getTenyeszetAll() {
        List<Tenyeszet> tenyeszetList = new ArrayList<Tenyeszet>();
        Cursor cursor = database.query(DBScripts.TABLE_TENYESZET, DBScripts.COLUMNS_TENYESZET, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tenyeszet comment = getTenyeszetFromCursor(cursor);
            tenyeszetList.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return tenyeszetList;
    }

    private Tenyeszet getTenyeszetFromCursor(Cursor cursor) {
        Tenyeszet tenyeszet = new Tenyeszet();
        tenyeszet.setTENAZ(cursor.getLong(0));
        tenyeszet.setTARTO(cursor.getString(1));
        tenyeszet.setTECIM(cursor.getString(2));
        tenyeszet.setLEDAT(new Date(cursor.getLong(3)));
        tenyeszet.setERVENYES(cursor.getInt(4) != 0);
        return tenyeszet;
    }

    // TODO log
    public long addEgyed(Egyed egyed) {
        ContentValues values = DBUtil.mapEgyedToContentValues(egyed);
        long id = database.insert(DBScripts.TABLE_EGYED, null, values);
        return id;
    }

    // TODO log
    public int removeEgyed(Egyed egyed) {
        int removedCount = database.delete(DBScripts.TABLE_EGYED, DBScripts.COLUMN_EGYED_AZONO + " = " + egyed.getAZONO(), null);
        return removedCount;
    }

    public int removeEgyedByTENAZ(Long TENAZ) {
        int removedCount = database.delete(DBScripts.TABLE_EGYED, DBScripts.COLUMN_EGYED_TENAZ + " = " + TENAZ, null);
        return removedCount;
    }

    public List<Egyed> getEgyedAll() {
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, null, null, null, null, null);
        return getEgyedListFromCursor(cursor);
    }

    public List<Egyed> getEgyedByTENAZ(Long TENAZ) {
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, DBScripts.COLUMN_EGYED_TENAZ + "=?",
                new String[]{String.valueOf(TENAZ)}, null, null, null);
        return getEgyedListFromCursor(cursor);
    }

    public List<Egyed> getEgyedByTENAZAndKIVALASZTOTT(Long TENAZ, Boolean KIVALASZTOTT) {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(DBScripts.COLUMN_EGYED_TENAZ).append("=").append(TENAZ);
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_EGYED_KIVALASZTOTT).append(KIVALASZTOTT ? "=1" : "=0");
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, selectBuilder.toString(), null, null, null, null);
        return getEgyedListFromCursor(cursor);
    }

    private List<Egyed> getEgyedListFromCursor(Cursor cursor) {
        List<Egyed> egyedList = new ArrayList<Egyed>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Egyed egyed = getEgyedFromCursor(cursor);
            egyedList.add(egyed);
            cursor.moveToNext();
        }
        cursor.close();
        return egyedList;
    }

    private Egyed getEgyedFromCursor(Cursor cursor) {
        Egyed egyed = new Egyed();
        egyed.setAZONO(cursor.getLong(0));
        egyed.setTENAZ(cursor.getLong(1));
        egyed.setORSKO(cursor.getString(2));
        egyed.setELLSO(cursor.getInt(3));
        egyed.setELLDA(new Date(cursor.getLong(4)));
        egyed.setSZULD(new Date(cursor.getLong(5)));
        egyed.setFAJKO(cursor.getInt(6));
        egyed.setKONSK(cursor.getInt(7));
        egyed.setSZINE(cursor.getInt(8));
        egyed.setITVJE(cursor.getInt(9) != 0);
        egyed.setKIVALASZTOTT(cursor.getInt(10) != 0);
        egyed.setUJ(cursor.getInt(11) != 0);
        return egyed;
    }

    // BÍRÁLATOK KEZELÉSE

    public long addBiralat(Biralat biralat) {
        ContentValues values = DBUtil.mapBiralatToContentValues(biralat);
        long id = database.insert(DBScripts.TABLE_BIRALAT, null, values);
        return id;
    }

    public int removeBiralatByTENAZ(Long TENAZ) {
        int removedCount = database.delete(DBScripts.TABLE_BIRALAT, DBScripts.COLUMN_BIRALAT_TENAZ + " = " + TENAZ, null);
        return removedCount;
    }

    public List<Biralat> getBiralatAll() {
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, null, null, null, null, null);
        return getBiralatListFromCursor(cursor);
    }

    public List<Biralat> getBiralatByTENAZ(Long TENAZ) {
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, DBScripts.COLUMN_BIRALAT_TENAZ + "=?",
                new String[]{String.valueOf(TENAZ)}, null, null, null);
        return getBiralatListFromCursor(cursor);
    }

    public List<Biralat> getBiralatByTENAZAndFELTOLTETLEN(Long TENAZ, boolean FELTOLTETLEN) {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_TENAZ).append("=").append(TENAZ);
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_FELTOLTETLEN).append(FELTOLTETLEN ? "=1" : "=0");
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, selectBuilder.toString(), null, null, null, null);
        return getBiralatListFromCursor(cursor);
    }

    private List<Biralat> getBiralatListFromCursor(Cursor cursor) {
        List<Biralat> list = new ArrayList<Biralat>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Biralat b = getBiralatFromCursor(cursor);
            list.add(b);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    private Biralat getBiralatFromCursor(Cursor cursor) {
        Biralat biralat = new Biralat();
        biralat.setAZONO(cursor.getLong(0));
        biralat.setTENAZ(cursor.getLong(1));
        biralat.setORSKO(cursor.getString(2));
        biralat.setBIRDA(new Date(cursor.getLong(3)));
        biralat.setBIRTI(cursor.getInt(4));
        biralat.setKULAZ(cursor.getString(5));
        biralat.setAKAKO(cursor.getInt(6));
        biralat.setFELTOLTETLEN(cursor.getInt(7) != 0);

        int i = 7;
        biralat.setKOD01(cursor.getString(++i));
        biralat.setERT01(cursor.getString(++i));
        biralat.setKOD02(cursor.getString(++i));
        biralat.setERT02(cursor.getString(++i));
        biralat.setKOD03(cursor.getString(++i));
        biralat.setERT03(cursor.getString(++i));
        biralat.setKOD04(cursor.getString(++i));
        biralat.setERT04(cursor.getString(++i));
        biralat.setKOD05(cursor.getString(++i));
        biralat.setERT05(cursor.getString(++i));
        biralat.setKOD06(cursor.getString(++i));
        biralat.setERT06(cursor.getString(++i));
        biralat.setKOD07(cursor.getString(++i));
        biralat.setERT07(cursor.getString(++i));
        biralat.setKOD08(cursor.getString(++i));
        biralat.setERT08(cursor.getString(++i));
        biralat.setKOD09(cursor.getString(++i));
        biralat.setERT09(cursor.getString(++i));
        biralat.setKOD10(cursor.getString(++i));
        biralat.setERT10(cursor.getString(++i));
        biralat.setKOD11(cursor.getString(++i));
        biralat.setERT11(cursor.getString(++i));
        biralat.setKOD12(cursor.getString(++i));
        biralat.setERT12(cursor.getString(++i));
        biralat.setKOD13(cursor.getString(++i));
        biralat.setERT13(cursor.getString(++i));
        biralat.setKOD14(cursor.getString(++i));
        biralat.setERT14(cursor.getString(++i));
        biralat.setKOD15(cursor.getString(++i));
        biralat.setERT15(cursor.getString(++i));
        biralat.setKOD16(cursor.getString(++i));
        biralat.setERT16(cursor.getString(++i));
        biralat.setKOD17(cursor.getString(++i));
        biralat.setERT17(cursor.getString(++i));
        biralat.setKOD18(cursor.getString(++i));
        biralat.setERT18(cursor.getString(++i));
        biralat.setKOD19(cursor.getString(++i));
        biralat.setERT19(cursor.getString(++i));
        biralat.setKOD20(cursor.getString(++i));
        biralat.setERT20(cursor.getString(++i));
        biralat.setKOD21(cursor.getString(++i));
        biralat.setERT21(cursor.getString(++i));
        biralat.setKOD22(cursor.getString(++i));
        biralat.setERT22(cursor.getString(++i));
        biralat.setKOD23(cursor.getString(++i));
        biralat.setERT23(cursor.getString(++i));
        biralat.setKOD24(cursor.getString(++i));
        biralat.setERT24(cursor.getString(++i));
        biralat.setKOD25(cursor.getString(++i));
        biralat.setERT25(cursor.getString(++i));
        biralat.setKOD26(cursor.getString(++i));
        biralat.setERT26(cursor.getString(++i));
        biralat.setKOD27(cursor.getString(++i));
        biralat.setERT27(cursor.getString(++i));
        biralat.setKOD28(cursor.getString(++i));
        biralat.setERT28(cursor.getString(++i));
        biralat.setKOD29(cursor.getString(++i));
        biralat.setERT29(cursor.getString(++i));
        biralat.setKOD30(cursor.getString(++i));
        biralat.setERT30(cursor.getString(++i));
        return biralat;
    }

}
