package hu.flexisys.kbr.controller.db;

import android.content.Context;
import android.util.Log;

import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.FileUtil;
import hu.flexisys.kbr.util.LogUtil;

import java.io.*;
import java.util.List;

public class DBController {

    private static final String DB_NAME = "KBR2_DB";
    private static final int DB_VERSION = 1;
    private final Context context;

    private DBConnector sdCardConnector;
    private DBConnector innerConnector;

    private String innerDBPath;
    private String sdCardDBPath;

    public DBController(Context context, String userid) throws Exception {
        this.context = context;
        getDBPath(userid);
        createDBConnector();
    }

    private void getDBPath(String userid) throws Exception {
        innerDBPath = FileUtil.innerAppPath + File.separator + DB_NAME + "_innerDB_" + userid + ".sqlite";
        String dirPath = FileUtil.externalAppPath + File.separator + "DataBase";
        File dir = new File(dirPath);
        boolean dirCreated = dir.mkdirs();
        if (!dirCreated && !dir.exists()) {
            throw new Exception("External directory creation error.");
        }
        sdCardDBPath = dir.getPath() + File.separator + DB_NAME + "_sdcardDB_" + userid + ".sqlite";
    }

    public void createDBConnector() {
        innerConnector = new DBConnector(context, innerDBPath, DB_VERSION);
        sdCardConnector = new DBConnector(context, sdCardDBPath, DB_VERSION);
    }

    public void closeDBConnector() {
        innerConnector.close();
        sdCardConnector.close();
    }

    public void addTenyeszet(Tenyeszet tenyeszet) {
        innerConnector.addTenyeszet(tenyeszet);
        sdCardConnector.addTenyeszet(tenyeszet);
    }

    public void removeTenyeszet(String TENAZ) {
        Log.i(LogUtil.TAG, "removeTenyeszet from innerConnector:" + TENAZ);
        removeTenyeszet(innerConnector, TENAZ);
        Log.i(LogUtil.TAG, "removeTenyeszet from sdCardConnector:" + TENAZ);
        removeTenyeszet(sdCardConnector, TENAZ);
    }

    private void removeTenyeszet(DBConnector connector, String TENAZ) {
        int ok = connector.removeTenyeszet(TENAZ);
        Log.i(LogUtil.TAG, "removeTenyeszet:" + TENAZ + " - tenyeszet:" + ok);
        if (ok == 1) {
            int egyedCounter = connector.removeEgyedByTENAZ(TENAZ);
            Log.i(LogUtil.TAG, "removeTenyeszet:" + TENAZ + " - egyed:" + egyedCounter);
            int biralatCounter = connector.removeBiralatByTENAZ(TENAZ);
            Log.i(LogUtil.TAG, "removeTenyeszet:" + TENAZ + " - biralat:" + biralatCounter);
        }
    }

    public void invalidateTenyeszetByTENAZ(String TENAZ) {
        innerConnector.invalidateTenyeszetByTENAZ(TENAZ);
        sdCardConnector.invalidateTenyeszetByTENAZ(TENAZ);
    }

    public void removeSelectionFromTenyeszet(String TENAZ) {
        innerConnector.removeSelectionFromTenyeszet(TENAZ);
        sdCardConnector.removeSelectionFromTenyeszet(TENAZ);
    }

    public void addEgyed(Egyed egyed) {
        innerConnector.addEgyed(egyed);
        sdCardConnector.addEgyed(egyed);
    }

    public void updateEgyedByAZONOWithKIVALASZTOTT(String AZONO, Boolean KIVALASZTOTT) {
        innerConnector.updateEgyedByAZONOWithKIVALASZTOTT(AZONO, KIVALASZTOTT);
        sdCardConnector.updateEgyedByAZONOWithKIVALASZTOTT(AZONO, KIVALASZTOTT);
    }

    public void updateBiralat(Biralat biralat) {
        innerConnector.updateBiralat(biralat);
        sdCardConnector.updateBiralat(biralat);
    }

    public void removeBiralat(Biralat biralat) {
        innerConnector.removeBiralat(biralat);
        sdCardConnector.removeBiralat(biralat);
    }

    public Tenyeszet getTenyeszetByTENAZ(String tenaz) {
        return innerConnector.getTenyeszetByTENAZ(tenaz);
    }

    public List<Tenyeszet> getTenyeszetAll() {
        return innerConnector.getTenyeszetAll();
    }

    public int getEgyedTehenCountByTenyeszet(Tenyeszet tenyeszet) {
        return getEgyedTehenCountByTENAZ(tenyeszet.getTENAZ());
    }

    public List<Egyed> getEgyedByTENAZ(String tenaz) {
        return innerConnector.getEgyedByTENAZ(tenaz);
    }

    public int getEgyedTehenCountByTENAZ(String tenaz) {
        return innerConnector.getEgyedTehenCountByTENAZ(tenaz);
    }

    public List<Egyed> getEgyedByTENAZAndKIVALASZTOTT(String TENAZ, boolean KIVALASZTOTT) {
        return innerConnector.getEgyedByTENAZAndKIVALASZTOTT(TENAZ, KIVALASZTOTT);
    }

    public int getEgyedCountByTENAZAndKIVALASZTOTT(String TENAZ, boolean KIVALASZTOTT) {
        return innerConnector.getEgyedCountByTENAZAndKIVALASZTOTT(TENAZ, KIVALASZTOTT);
    }

    public List<Biralat> getBiralatByTENAZ(String tenaz) {
        return innerConnector.getBiralatByTENAZ(tenaz);
    }

    public int getBiralatCountByTENAZ(String tenaz) {
        return innerConnector.getBiralatCountByTENAZ(tenaz);
    }

    public List<Biralat> getBiralatByAZONO(String azono) {
        return innerConnector.getBiralatByAZONO(azono);
    }

    public List<Biralat> getBiralatByTenyeszetAndFeltoltetlen(String TENAZ, Boolean FELTOLTETLEN) {
        return innerConnector.getBiralatByTENAZAndFELTOLTETLEN(TENAZ, FELTOLTETLEN);
    }

    public int getBiralatCountByTenyeszetAndFeltoltetlen(String TENAZ, Boolean FELTOLTETLEN) {
        return innerConnector.getBiralatCountByTENAZAndFELTOLTETLEN(TENAZ, FELTOLTETLEN);
    }

    public int getBiralatCountByFeltoltetlen(Boolean FELTOLTETLEN) {
        return innerConnector.getBiralatCountByFELTOLTETLEN(FELTOLTETLEN);
    }

    public int getBiralatCountByTenyeszetAndExported(String TENAZ, boolean unexported) {
        return innerConnector.getBiralatCountByTenyeszetAndExported(TENAZ, unexported);
    }

    public void checkDbConsistency() throws Exception {
        checkTenyeszetConsistency();
        checkEgyedConsistency();
        checkBiralatConsistency();
    }

    public void checkTenyeszetConsistency() throws Exception {
        List<Tenyeszet> il = innerConnector.getTenyeszetAll();
        List<Tenyeszet> sl = sdCardConnector.getTenyeszetAll();
        if (il.size() != sl.size()) {
            throw new Exception(LogUtil.TAG + ":checkDbConsistency:different tenyeszetList");
        }
        for (int i = 0; i < il.size(); i++) {
            if (!il.get(i).equals(sl.get(i))) {
                throw new Exception(LogUtil.TAG + ":checkDbConsistency:different tenyeszet");
            }
        }
    }

    public void checkEgyedConsistency() throws Exception {
        List<Egyed> il = innerConnector.getEgyedAll();
        List<Egyed> sl = sdCardConnector.getEgyedAll();
        if (il.size() != sl.size()) {
            throw new Exception(LogUtil.TAG + ":checkDbConsistency:different egyedList");
        }
        for (int i = 0; i < il.size(); i++) {
            if (!il.get(i).equals(sl.get(i))) {
                throw new Exception(LogUtil.TAG + ":checkDbConsistency:different egyed");
            }
        }
    }

    public void checkBiralatConsistency() throws Exception {
        List<Biralat> il = innerConnector.getBiralatAll();
        List<Biralat> sl = sdCardConnector.getBiralatAll();
        if (il.size() != sl.size()) {
            throw new Exception(LogUtil.TAG + ":checkDbConsistency:different biralatList");
        }
        for (int i = 0; i < il.size(); i++) {
            if (!il.get(i).equals(sl.get(i))) {
                throw new Exception(LogUtil.TAG + ":checkDbConsistency:different biralat");
            }
        }
    }

    public void synchronizeDb() {
        File src = new File(innerDBPath);
        File dst = new File(sdCardDBPath);

        try {
            if (innerConnector.isEmpty()) {
                src.delete();
                dst.delete();
            } else {
                FileUtil.copyFile(src, dst);
            }
            createDBConnector();
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "synchronizeDb", e);
        }
    }

    public String getInnerDBPath() {
        return innerDBPath;
    }

    public String getSdCardDBPath() {
        return sdCardDBPath;
    }
}