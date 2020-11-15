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

public class DBConnector {

    private SQLiteDatabase database;

    public DBConnector(Context context, String path, int version) {
        DBOpenHelper openHelper = new DBOpenHelper(context, path, version);
        database = openHelper.getWritableDatabase();
    }

    public void addTenyeszet(Tenyeszet tenyeszet) {
        ContentValues values = DBUtil.mapTenyeszetToContentValues(tenyeszet);
        database.insert(DBScripts.TABLE_TENYESZET, null, values);
    }

    public int removeTenyeszet(String TENAZ) {
        return database.delete(DBScripts.TABLE_TENYESZET, DBScripts.COLUMN_TENYESZET_TENAZ + " = ?", new String[]{TENAZ});
    }

    public void removeSelectionFromTenyeszet(String TENAZ) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_EGYED_KIVALASZTOTT, false);
        database.update(DBScripts.TABLE_EGYED, values, DBScripts.COLUMN_EGYED_TENAZ + " = ?", new String[]{TENAZ});
    }

    public void invalidateTenyeszetByTENAZ(String TENAZ) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_TENYESZET_ERVENYES, false);
        database.update(DBScripts.TABLE_TENYESZET, values, DBScripts.COLUMN_TENYESZET_TENAZ + " = ?", new String[]{TENAZ});
    }

    public Tenyeszet getTenyeszetByTENAZ(String TENAZ) {
        String query = DBScripts.COLUMN_TENYESZET_TENAZ + " = ?";
        Cursor cursor = database.query(DBScripts.TABLE_TENYESZET, DBScripts.COLUMNS_TENYESZET, query, new String[]{TENAZ}, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            Tenyeszet list = getTenyeszetFromCursor(cursor);
            cursor.close();
            return list;
        }
        return null;
    }

    public List<Tenyeszet> getTenyeszetAll() {
        List<Tenyeszet> tenyeszetList = new ArrayList<>();
        Cursor cursor = database.query(DBScripts.TABLE_TENYESZET, DBScripts.COLUMNS_TENYESZET, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Tenyeszet tenyeszet = getTenyeszetFromCursor(cursor);
            if (tenyeszet.getTENAZ() != null)
                tenyeszetList.add(tenyeszet);
            cursor.moveToNext();
        }
        cursor.close();
        return tenyeszetList;
    }

    private Tenyeszet getTenyeszetFromCursor(Cursor cursor) {
        Tenyeszet tenyeszet = new Tenyeszet();
        tenyeszet.setTENAZ(cursor.getString(0));
        tenyeszet.setTARTO(cursor.getString(1));
        tenyeszet.setTECIM(cursor.getString(2));
        tenyeszet.setLEDAT(new Date(cursor.getLong(3)));
        tenyeszet.setERVENYES(cursor.getInt(4) != 0);
        return tenyeszet;
    }

    public void addEgyed(Egyed egyed) {
        ContentValues values = DBUtil.mapEgyedToContentValues(egyed);
        database.insert(DBScripts.TABLE_EGYED, null, values);
    }

    public int removeEgyedByTENAZ(String TENAZ) {
        return database.delete(DBScripts.TABLE_EGYED, DBScripts.COLUMN_EGYED_TENAZ + " = ?", new String[]{TENAZ});
    }

    public void updateEgyedByAZONOWithKIVALASZTOTT(String AZONO, Boolean KIVALASZTOTT) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_EGYED_KIVALASZTOTT, KIVALASZTOTT);
        database.update(DBScripts.TABLE_EGYED, values, DBScripts.COLUMN_EGYED_AZONO + " = ?", new String[]{AZONO});
    }

    public List<Egyed> getEgyedByTENAZ(String TENAZ) {
        Cursor cursor =
                database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, DBScripts.COLUMN_EGYED_TENAZ + " = ?", new String[]{String.valueOf(TENAZ)}, null,
                        null, null);
        List<Egyed> list = getEgyedListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getEgyedTehenCountByTENAZ(String TENAZ) {
        String select =
                "SELECT count(*) FROM " + DBScripts.TABLE_EGYED + " WHERE " + DBScripts.COLUMN_EGYED_TENAZ + "=? AND " + DBScripts.COLUMN_EGYED_ELLSO + " <> 0";
        Cursor cursor = database.rawQuery(select, new String[]{TENAZ});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<Egyed> getEgyedByTENAZAndKIVALASZTOTT(String TENAZ, Boolean KIVALASZTOTT) {
        String select = DBScripts.COLUMN_EGYED_TENAZ + " = ?" + " AND " + DBScripts.COLUMN_EGYED_KIVALASZTOTT + " = ?";
        String boolParam = KIVALASZTOTT ? "1" : "0";
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, select, new String[]{TENAZ, boolParam}, null, null, null);
        List<Egyed> list = getEgyedListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getEgyedCountByTENAZAndKIVALASZTOTT(String TENAZ, Boolean KIVALASZTOTT) {
        String select = "SELECT count(*) " +
                "FROM " + DBScripts.TABLE_EGYED + " " +
                "WHERE " + DBScripts.COLUMN_EGYED_TENAZ + " = ?" + " " +
                "AND " + DBScripts.COLUMN_EGYED_KIVALASZTOTT + " = ?";
        String boolParam = KIVALASZTOTT ? "1" : "0";
        Cursor cursor = database.rawQuery(select, new String[]{TENAZ, boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private List<Egyed> getEgyedListFromCursor(Cursor cursor) {
        List<Egyed> egyedList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Egyed egyed = getEgyedFromCursor(cursor);
            egyedList.add(egyed);
            cursor.moveToNext();
        }
        return egyedList;
    }

    private Egyed getEgyedFromCursor(Cursor cursor) {
        Egyed egyed = new Egyed();
        egyed.setAZONO(cursor.getString(0));
        egyed.setTENAZ(cursor.getString(1));
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

    public int removeBiralatByTENAZ(String TENAZ) {
        return database.delete(DBScripts.TABLE_BIRALAT, DBScripts.COLUMN_BIRALAT_TENAZ + " = ?", new String[]{TENAZ});
    }

    private void addBiralat(Biralat biralat) {
        ContentValues values = DBUtil.mapBiralatToContentValues(biralat);
        database.insert(DBScripts.TABLE_BIRALAT, null, values);
    }

    public void updateBiralat(Biralat biralat) {
        Long id = biralat.getId();
        if (id == null) {
            addBiralat(biralat);
            return;
        }
        ContentValues values = DBUtil.mapBiralatToContentValues(biralat);
        database.update(DBScripts.TABLE_BIRALAT, values, DBScripts.COLUMN_BIRALAT_ID + " = " + id, null);
    }

    // itt kihasználjuk, hogy csak egy db exportálatlan bírálat tartozhat egy egyedhez
    public void removeBiralat(Biralat biralat) {
        String where = DBScripts.COLUMN_BIRALAT_TENAZ + " = ?" +
                " AND " +
                DBScripts.COLUMN_BIRALAT_AZONO + " = ?" +
                " AND " +
                DBScripts.COLUMN_BIRALAT_EXPORTALT + " = ?";
        database.delete(DBScripts.TABLE_BIRALAT, where, new String[]{biralat.getTENAZ(), biralat.getAZONO(), "0"});
    }

    public List<Biralat> getBiralatByTENAZ(String TENAZ) {
        Cursor cursor =
                database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, DBScripts.COLUMN_BIRALAT_TENAZ + " = ?", new String[]{String.valueOf(TENAZ)},
                        null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getBiralatCountByTENAZ(String TENAZ) {
        String select = "SELECT count(*) FROM " + DBScripts.TABLE_BIRALAT + " WHERE " +
                DBScripts.COLUMN_BIRALAT_TENAZ + " = ?";
        Cursor cursor = database.rawQuery(select, new String[]{String.valueOf(TENAZ)});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<Biralat> getBiralatByAZONO(String azono) {
        Cursor cursor =
                database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, DBScripts.COLUMN_BIRALAT_AZONO + " = ?", new String[]{String.valueOf(azono)},
                        null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getBiralatCountByFELTOLTETLEN(boolean FELTOLTETLEN) {
        String boolParam = FELTOLTETLEN ? "1" : "0";
        String select = "SELECT count(*) FROM " + DBScripts.TABLE_BIRALAT + " WHERE " +
                DBScripts.COLUMN_BIRALAT_FELTOLTETLEN + " = ?";
        Cursor cursor = database.rawQuery(select, new String[]{boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<Biralat> getBiralatByTENAZAndFELTOLTETLEN(String TENAZ, boolean FELTOLTETLEN) {
        String boolParam = FELTOLTETLEN ? "1" : "0";
        String select = DBScripts.COLUMN_BIRALAT_TENAZ + " = ?" + " AND " + DBScripts.COLUMN_BIRALAT_FELTOLTETLEN + " = ?";
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, select, new String[]{TENAZ, boolParam}, null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getBiralatCountByTENAZAndFELTOLTETLEN(String TENAZ, boolean FELTOLTETLEN) {
        String boolParam = FELTOLTETLEN ? "1" : "0";
        String select = "SELECT count(*) FROM " + DBScripts.TABLE_BIRALAT + " WHERE " +
                DBScripts.COLUMN_BIRALAT_TENAZ + " = ?" +
                " AND " +
                DBScripts.COLUMN_BIRALAT_FELTOLTETLEN + " = ?";
        Cursor cursor = database.rawQuery(select, new String[]{TENAZ, boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public int getBiralatCountByTenyeszetAndExported(String TENAZ, boolean EXPORTED) {
        String boolParam = EXPORTED ? "1" : "0";
        String select = "SELECT count(*) FROM " + DBScripts.TABLE_BIRALAT + " WHERE " +
                DBScripts.COLUMN_BIRALAT_TENAZ + " = ?" +
                " AND " +
                DBScripts.COLUMN_BIRALAT_EXPORTALT + " = ?";
        Cursor cursor = database.rawQuery(select, new String[]{TENAZ, boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private List<Biralat> getBiralatListFromCursor(Cursor cursor) {
        List<Biralat> list = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Biralat b = getBiralatFromCursor(cursor);
            list.add(b);
            cursor.moveToNext();
        }
        return list;
    }

    private Biralat getBiralatFromCursor(Cursor cursor) {
        Biralat biralat = new Biralat();
        int i = 0;
        biralat.setId(cursor.getLong(i++));
        biralat.setAZONO(cursor.getString(i++));
        biralat.setTENAZ(cursor.getString(i++));
        biralat.setORSKO(cursor.getString(i++));
        biralat.setBIRDA(new Date(cursor.getLong(i++)));
        biralat.setBIRTI(cursor.getInt(i++));
        biralat.setKULAZ(cursor.getString(i++));

        Integer AKAKO = cursor.getInt(i++);
        biralat.setAKAKO(AKAKO);

        biralat.setFELTOLTETLEN(cursor.getInt(i++) != 0);
        biralat.setEXPORTALT(cursor.getInt(i++) != 0);
        biralat.setLETOLTOTT(cursor.getInt(i++) != 0);

        biralat.setMEGJEGYZES(cursor.getString(i++));

        biralat.setKOD01(cursor.getString(i++));
        biralat.setERT01(cursor.getString(i++));
        biralat.setKOD02(cursor.getString(i++));
        biralat.setERT02(cursor.getString(i++));
        biralat.setKOD03(cursor.getString(i++));
        biralat.setERT03(cursor.getString(i++));
        biralat.setKOD04(cursor.getString(i++));
        biralat.setERT04(cursor.getString(i++));
        biralat.setKOD05(cursor.getString(i++));
        biralat.setERT05(cursor.getString(i++));
        biralat.setKOD06(cursor.getString(i++));
        biralat.setERT06(cursor.getString(i++));
        biralat.setKOD07(cursor.getString(i++));
        biralat.setERT07(cursor.getString(i++));
        biralat.setKOD08(cursor.getString(i++));
        biralat.setERT08(cursor.getString(i++));
        biralat.setKOD09(cursor.getString(i++));
        biralat.setERT09(cursor.getString(i++));
        biralat.setKOD10(cursor.getString(i++));
        biralat.setERT10(cursor.getString(i++));
        biralat.setKOD11(cursor.getString(i++));
        biralat.setERT11(cursor.getString(i++));
        biralat.setKOD12(cursor.getString(i++));
        biralat.setERT12(cursor.getString(i++));
        biralat.setKOD13(cursor.getString(i++));
        biralat.setERT13(cursor.getString(i++));
        biralat.setKOD14(cursor.getString(i++));
        biralat.setERT14(cursor.getString(i++));
        biralat.setKOD15(cursor.getString(i++));
        biralat.setERT15(cursor.getString(i++));
        biralat.setKOD16(cursor.getString(i++));
        biralat.setERT16(cursor.getString(i++));
        biralat.setKOD17(cursor.getString(i++));
        biralat.setERT17(cursor.getString(i++));
        biralat.setKOD18(cursor.getString(i++));
        biralat.setERT18(cursor.getString(i++));
        biralat.setKOD19(cursor.getString(i++));
        biralat.setERT19(cursor.getString(i++));
        biralat.setKOD20(cursor.getString(i++));
        biralat.setERT20(cursor.getString(i++));
        biralat.setKOD21(cursor.getString(i++));
        biralat.setERT21(cursor.getString(i++));
        biralat.setKOD22(cursor.getString(i++));
        biralat.setERT22(cursor.getString(i++));
        biralat.setKOD23(cursor.getString(i++));
        biralat.setERT23(cursor.getString(i++));
        biralat.setKOD24(cursor.getString(i++));
        biralat.setERT24(cursor.getString(i++));
        biralat.setKOD25(cursor.getString(i++));
        biralat.setERT25(cursor.getString(i++));
        biralat.setKOD26(cursor.getString(i++));
        biralat.setERT26(cursor.getString(i++));
        biralat.setKOD27(cursor.getString(i++));
        biralat.setERT27(cursor.getString(i++));
        biralat.setKOD28(cursor.getString(i++));
        biralat.setERT28(cursor.getString(i++));
        biralat.setKOD29(cursor.getString(i++));
        biralat.setERT29(cursor.getString(i++));
        biralat.setKOD30(cursor.getString(i++));
        biralat.setERT30(cursor.getString(i));
        return biralat;
    }

    public boolean isEmpty() {
        int count = 0;

        String select = "SELECT count(*) FROM " + DBScripts.TABLE_TENYESZET;
        Cursor cursor = database.rawQuery(select, new String[]{});
        cursor.moveToFirst();
        count += cursor.getInt(0);
        cursor.close();

        select = "SELECT count(*) FROM " + DBScripts.TABLE_EGYED;
        cursor = database.rawQuery(select, new String[]{});
        cursor.moveToFirst();
        count += cursor.getInt(0);
        cursor.close();

        select = "SELECT count(*) FROM " + DBScripts.TABLE_BIRALAT;
        cursor = database.rawQuery(select, new String[]{});
        cursor.moveToFirst();
        count += cursor.getInt(0);
        cursor.close();

        return count == 0;
    }
}
