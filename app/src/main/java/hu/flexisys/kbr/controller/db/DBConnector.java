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

    public long addTenyeszet(Tenyeszet tenyeszet) {
        ContentValues values = DBUtil.mapTenyeszetToContentValues(tenyeszet);
        long insertId = database.insert(DBScripts.TABLE_TENYESZET, null, values);
        return insertId;
    }

    public int removeTenyeszet(String TENAZ) {
        int removedCount = database.delete(DBScripts.TABLE_TENYESZET, DBScripts.COLUMN_TENYESZET_TENAZ + " = ?", new String[]{TENAZ});
        return removedCount;
    }

    public int removeSelectionFromTenyeszet(String TENAZ) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_EGYED_KIVALASZTOTT, false);
        int count = database.update(DBScripts.TABLE_EGYED, values, DBScripts.COLUMN_EGYED_TENAZ + " = ?", new String[]{TENAZ});
        return count;
    }

    public int updateTenyeszet(Tenyeszet tenyeszet) {
        ContentValues values = DBUtil.mapTenyeszetToContentValues(tenyeszet);
        int count = database.update(DBScripts.TABLE_TENYESZET, values, DBScripts.COLUMN_TENYESZET_TENAZ + " = ?", new String[]{tenyeszet.getTENAZ()});
        return count;
    }

    public int updateTenyeszetByTENAZWithERVENYES(String TENAZ, Boolean ERVENYES) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_TENYESZET_ERVENYES, ERVENYES);
        int count = database.update(DBScripts.TABLE_TENYESZET, values, DBScripts.COLUMN_TENYESZET_TENAZ + " = ?", new String[]{TENAZ});
        return count;
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
        List<Tenyeszet> tenyeszetList = new ArrayList<Tenyeszet>();
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

    public long addEgyed(Egyed egyed) {
        ContentValues values = DBUtil.mapEgyedToContentValues(egyed);
        long id = database.insert(DBScripts.TABLE_EGYED, null, values);
        return id;
    }

    public int removeEgyed(Egyed egyed) {
        int removedCount = database.delete(DBScripts.TABLE_EGYED, DBScripts.COLUMN_EGYED_AZONO + " = ?", new String[]{egyed.getAZONO()});
        return removedCount;
    }

    public int removeEgyedByTENAZ(String TENAZ) {
        int removedCount = database.delete(DBScripts.TABLE_EGYED, DBScripts.COLUMN_EGYED_TENAZ + " = ?", new String[]{TENAZ});
        return removedCount;
    }

    public int updateEgyedByAZONOWithKIVALASZTOTT(String AZONO, Boolean KIVALASZTOTT) {
        ContentValues values = new ContentValues();
        values.put(DBScripts.COLUMN_EGYED_KIVALASZTOTT, KIVALASZTOTT);
        int count = database.update(DBScripts.TABLE_EGYED, values, DBScripts.COLUMN_EGYED_AZONO + " = ?", new String[]{AZONO});
        return count;
    }

    public List<Egyed> getEgyedAll() {
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, null, null, null, null, null);
        List<Egyed> list = getEgyedListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public List<Egyed> getEgyedByTENAZ(String TENAZ) {
        Cursor cursor =
                database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, DBScripts.COLUMN_EGYED_TENAZ + " = ?", new String[]{String.valueOf(TENAZ)}, null,
                        null, null);
        List<Egyed> list = getEgyedListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public List<Egyed> getEgyedTehenByTENAZ(String TENAZ) {
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED,
                DBScripts.COLUMN_EGYED_TENAZ + "=? AND " + DBScripts.COLUMN_EGYED_ELLSO + " <> 0", new String[]{String.valueOf(TENAZ)}, null, null, null);
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
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(DBScripts.COLUMN_EGYED_TENAZ).append(" = ?");
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_EGYED_KIVALASZTOTT).append(" = ?");
        String boolParam = KIVALASZTOTT ? "1" : "0";
        Cursor cursor = database.query(DBScripts.TABLE_EGYED, DBScripts.COLUMNS_EGYED, selectBuilder.toString(), new String[]{TENAZ, boolParam}, null, null, null);
        List<Egyed> list = getEgyedListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getEgyedCountByTENAZAndKIVALASZTOTT(String TENAZ, Boolean KIVALASZTOTT) {
        StringBuilder selectBuilder = new StringBuilder("SELECT count(*) FROM ").append(DBScripts.TABLE_EGYED).append(" WHERE ");
        selectBuilder.append(DBScripts.COLUMN_EGYED_TENAZ).append(" = ?");
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_EGYED_KIVALASZTOTT).append(" = ?");
        String boolParam = KIVALASZTOTT ? "1" : "0";
        Cursor cursor = database.rawQuery(selectBuilder.toString(), new String[]{TENAZ, boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private List<Egyed> getEgyedListFromCursor(Cursor cursor) {
        List<Egyed> egyedList = new ArrayList<Egyed>();
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
        int removedCount = database.delete(DBScripts.TABLE_BIRALAT, DBScripts.COLUMN_BIRALAT_TENAZ + " = ?", new String[]{TENAZ});
        return removedCount;
    }

    private long addBiralat(Biralat biralat) {
        ContentValues values = DBUtil.mapBiralatToContentValues(biralat);
        long id = database.insert(DBScripts.TABLE_BIRALAT, null, values);
        return id;
    }

    public int updateBiralat(Biralat biralat) {
        Long id = biralat.getId();
        if (id == null) {
            id = addBiralat(biralat);
            if (id != null && id > 0) {
                return 1;
            } else {
                return 0;
            }
        }
        ContentValues values = DBUtil.mapBiralatToContentValues(biralat);
        int count = database.update(DBScripts.TABLE_BIRALAT, values, DBScripts.COLUMN_BIRALAT_ID + " = " + id, null);
        return count;
    }

    // itt kihasználjuk, hogy csak egy db exportálatlan bírálat tartozhat egy egyedhez
    public int removeBiralat(Biralat biralat) {
        StringBuilder where = new StringBuilder();
        where.append(DBScripts.COLUMN_BIRALAT_TENAZ).append(" = ?");
        where.append(" AND ");
        where.append(DBScripts.COLUMN_BIRALAT_AZONO).append(" = ?");
        where.append(" AND ");
        where.append(DBScripts.COLUMN_BIRALAT_EXPORTALT).append(" = ?");
        int removedCount = database.delete(DBScripts.TABLE_BIRALAT, where.toString(), new String[]{biralat.getTENAZ(), biralat.getAZONO(), "0"});
        return removedCount;
    }

    public List<Biralat> getBiralatAll() {
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, null, null, null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
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
        StringBuilder selectBuilder = new StringBuilder("SELECT count(*) FROM ").append(DBScripts.TABLE_BIRALAT).append(" WHERE ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_TENAZ).append(" = ?");
        Cursor cursor = database.rawQuery(selectBuilder.toString(), new String[]{String.valueOf(TENAZ)});
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

    public List<Biralat> getBiralatByFELTOLTETLEN(boolean FELTOLTETLEN) {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_FELTOLTETLEN).append(" = ?");
        String boolParam = FELTOLTETLEN ? "1" : "0";
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, selectBuilder.toString(), new String[]{boolParam}, null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getBiralatCountByFELTOLTETLEN(boolean FELTOLTETLEN) {
        StringBuilder selectBuilder = new StringBuilder("SELECT count(*) FROM ").append(DBScripts.TABLE_BIRALAT).append(" WHERE ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_FELTOLTETLEN).append(" = ?");
        String boolParam = FELTOLTETLEN ? "1" : "0";
        Cursor cursor = database.rawQuery(selectBuilder.toString(), new String[]{boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<Biralat> getBiralatByTENAZAndFELTOLTETLEN(String TENAZ, boolean FELTOLTETLEN) {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_TENAZ).append(" = ?");
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_FELTOLTETLEN).append(" = ?");
        String boolParam = FELTOLTETLEN ? "1" : "0";
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, selectBuilder.toString(), new String[]{TENAZ, boolParam}, null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getBiralatCountByTENAZAndFELTOLTETLEN(String TENAZ, boolean FELTOLTETLEN) {
        StringBuilder selectBuilder = new StringBuilder("SELECT count(*) FROM ").append(DBScripts.TABLE_BIRALAT).append(" WHERE ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_TENAZ).append(" = ?");
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_FELTOLTETLEN).append(" = ?");
        String boolParam = FELTOLTETLEN ? "1" : "0";
        Cursor cursor = database.rawQuery(selectBuilder.toString(), new String[]{TENAZ, boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    public List<Biralat> getBiralatByTenyeszetAndExported(String TENAZ, boolean EXPORTED) {
        StringBuilder selectBuilder = new StringBuilder();
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_TENAZ).append(" = ?");
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_EXPORTALT).append(" = ?");
        String boolParam = EXPORTED ? "1" : "0";
        Cursor cursor = database.query(DBScripts.TABLE_BIRALAT, DBScripts.COLUMNS_BIRALAT, selectBuilder.toString(), new String[]{TENAZ, boolParam}, null, null, null);
        List<Biralat> list = getBiralatListFromCursor(cursor);
        cursor.close();
        return list;
    }

    public int getBiralatCountByTenyeszetAndExported(String TENAZ, boolean EXPORTED) {
        StringBuilder selectBuilder = new StringBuilder("SELECT count(*) FROM ").append(DBScripts.TABLE_BIRALAT).append(" WHERE ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_TENAZ).append(" = ?");
        selectBuilder.append(" AND ");
        selectBuilder.append(DBScripts.COLUMN_BIRALAT_EXPORTALT).append(" = ?");
        String boolParam = EXPORTED ? "1" : "0";
        Cursor cursor = database.rawQuery(selectBuilder.toString(), new String[]{TENAZ, boolParam});
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    private List<Biralat> getBiralatListFromCursor(Cursor cursor) {
        List<Biralat> list = new ArrayList<Biralat>();
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
