package hu.flexisys.kbr.controller.db;

import android.content.Context;
import android.util.Log;

import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.FileUtil;
import hu.flexisys.kbr.util.LogUtil;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        innerDBPath = FileUtil.innerAppPath + File.separator + DB_NAME + "_innerDB_" + userid;
        String dirPath = FileUtil.externalAppPath + File.separator + "DataBase";
        File dir = new File(dirPath);
        boolean dirCreated = dir.mkdirs();
        if (!dirCreated && !dir.exists()) {
            throw new Exception("External directory creation error.");
        }
        sdCardDBPath = dir.getPath() + File.separator + DB_NAME + "_sdcardDB_" + userid;
    }

    private void createDBConnector() {
        innerConnector = new DBConnector(context, innerDBPath, DB_VERSION);
        sdCardConnector = new DBConnector(context, sdCardDBPath, DB_VERSION);
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
        String innerMD5 = getMD5EncryptedString(innerDBPath);
        String sdCardMD5 = getMD5EncryptedString(sdCardDBPath);
        if (!innerMD5.equals(sdCardMD5)) {
            throw new Exception(LogUtil.TAG + ":checkDbConsistency:different db");
        }
    }

    private String getMD5EncryptedString(String path) {
        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "getMD5EncryptedString", e);
        }
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(LogUtil.TAG, "getMD5EncryptedString", e);
        }
        mdEnc.update(bytes, 0, bytes.length);
        StringBuilder md5 = new StringBuilder(new BigInteger(1, mdEnc.digest()).toString(16));
        while (md5.length() < 32) {
            md5.insert(0, "0");
        }
        return md5.toString();
    }

    public void synchronizeDb() {
        File src = new File(innerDBPath);
        File dst = new File(sdCardDBPath);

        if (innerConnector.isEmpty()) {
            src.delete();
            dst.delete();
            createDBConnector();
        } else {
            try {
                FileUtil.copyFile(src, dst);
            } catch (IOException e) {
                Log.e(LogUtil.TAG, "synchronizeDb", e);
            }
        }
    }

    public String getInnerDBPath() {
        return innerDBPath;
    }

    public String getSdCardDBPath() {
        return sdCardDBPath;
    }
}