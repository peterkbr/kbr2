package hu.flexisys.kbr.controller.db;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.FileUtil;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class DBController {

    public static final String TAG = "KBR2_DBController";

    private static String DB_NAME = "KBR2_DB";
    private static int DB_VERSION = 1;
    private final Context context;

    private DBConnector sdCardConnector;
    private DBConnector innerConnector;

    private String innerDBPath;
    private String sdCardDBPath;

    public DBController(Context context, String userid) {
        this.context = context;
        innerDBPath = FileUtil.getInnerAppPath() + File.separator + DB_NAME + "_innerDB_" + userid;
        innerConnector = new DBConnector(context, innerDBPath, DB_VERSION);

        String dirPath = FileUtil.getExternalAppPath() + File.separator + "DataBase";
        File dir = new File(dirPath);
        dir.mkdirs();
        sdCardDBPath = dir.getPath() + File.separator + DB_NAME + "_sdcardDB_" + userid;
        sdCardConnector = new DBConnector(context, sdCardDBPath, DB_VERSION);

//        File storageDir = new File("/mnt/");
//        if (storageDir.isDirectory()) {
//            String[] list = storageDir.list();
// find KBR2 folder on any of these:
//            for (String direct : list) {
//                Log.i(TAG, direct);
//            }
//        }
    }


    // DB MODIFICATION

    public void addTenyeszet(Tenyeszet tenyeszet) {
        innerConnector.addTenyeszet(tenyeszet);
        sdCardConnector.addTenyeszet(tenyeszet);
    }

    public void removeTenyeszet(String TENAZ) {
        Log.i(TAG, "removeTenyeszet from innerConnector:" + TENAZ);
        removeTenyeszet(innerConnector, TENAZ);
        Log.i(TAG, "removeTenyeszet from sdCardConnector:" + TENAZ);
        removeTenyeszet(sdCardConnector, TENAZ);
    }

    private void removeTenyeszet(DBConnector connector, String TENAZ) {
        int ok = connector.removeTenyeszet(TENAZ);
        Log.i(TAG, "removeTenyeszet:" + TENAZ + " - tenyeszet:" + ok);
        if (ok == 1) {
            int egyedCounter = connector.removeEgyedByTENAZ(TENAZ);
            Log.i(TAG, "removeTenyeszet:" + TENAZ + " - egyed:" + egyedCounter);
            int biralatCounter = connector.removeBiralatByTENAZ(TENAZ);
            Log.i(TAG, "removeTenyeszet:" + TENAZ + " - biralat:" + biralatCounter);
        }
    }

    public int updateTenyeszet(Tenyeszet tenyeszet) {
        int innerCount = innerConnector.updateTenyeszet(tenyeszet);
        sdCardConnector.updateTenyeszet(tenyeszet);
        return innerCount;
    }

    public int updateTenyeszetByTENAZWithERVENYES(String TENAZ, Boolean ERVENYES) {
        int innerCount = innerConnector.updateTenyeszetByTENAZWithERVENYES(TENAZ, ERVENYES);
        sdCardConnector.updateTenyeszetByTENAZWithERVENYES(TENAZ, ERVENYES);
        return innerCount;
    }

    public int removeSelectionFromTenyeszet(String TENAZ) {
        int innerCount = innerConnector.removeSelectionFromTenyeszet(TENAZ);
        sdCardConnector.removeSelectionFromTenyeszet(TENAZ);
        return innerCount;
    }

    public void addEgyed(Egyed egyed) {
        innerConnector.addEgyed(egyed);
        sdCardConnector.addEgyed(egyed);
    }

    public int updateEgyedByAZONOWithKIVALASZTOTT(String AZONO, Boolean KIVALASZTOTT) {
        int count = innerConnector.updateEgyedByAZONOWithKIVALASZTOTT(AZONO, KIVALASZTOTT);
        sdCardConnector.updateEgyedByAZONOWithKIVALASZTOTT(AZONO, KIVALASZTOTT);
        return count;
    }

    public void addBiralat(Biralat biralat) {
        innerConnector.addBiralat(biralat);
        sdCardConnector.addBiralat(biralat);
    }

    public void updateBiralat(Biralat biralat) {
//        innerConnector.removeBiralat(biralat.getTENAZ(), biralat.getAZONO());
//        innerConnector.addBiralat(biralat);
//        sdCardConnector.removeBiralat(biralat.getTENAZ(), biralat.getAZONO());
//        sdCardConnector.addBiralat(biralat);
        innerConnector.updateBiralat(biralat);
        sdCardConnector.updateBiralat(biralat);
    }

    // READ FROM DB

    public Tenyeszet getTenyeszetByTENAZ(String tenaz) {
        return innerConnector.getTenyeszetByTENAZ(tenaz);
    }

    public List<Tenyeszet> getTenyeszetAll() {
        return innerConnector.getTenyeszetAll();
    }

    public List<Egyed> getEgyedAll() {
        return innerConnector.getEgyedAll();
    }

    public List<Egyed> getEgyedByTenyeszet(Tenyeszet tenyeszet) {
        return getEgyedByTENAZ(tenyeszet.getTENAZ());
    }

    public List<Egyed> getEgyedTehenByTenyeszet(Tenyeszet tenyeszet) {
        return getEgyedTehenByTENAZ(tenyeszet.getTENAZ());
    }

    public List<Egyed> getEgyedByTENAZ(String tenaz) {
        return innerConnector.getEgyedByTENAZ(tenaz);
    }

    public List<Egyed> getEgyedTehenByTENAZ(String tenaz) {
        return innerConnector.getEgyedTehenByTENAZ(tenaz);
    }

    public List<Egyed> getEgyedByTENAZAndKIVALASZTOTT(String TENAZ, boolean KIVALASZTOTT) {
        return innerConnector.getEgyedByTENAZAndKIVALASZTOTT(TENAZ, KIVALASZTOTT);
    }

    public List<Biralat> getBiralatAll() {
        return innerConnector.getBiralatAll();
    }

    public List<Biralat> getBiralatByTENAZ(String tenaz) {
        return innerConnector.getBiralatByTENAZ(tenaz);
    }

    public List<Biralat> getBiralatByAZONO(String azono) {
        return innerConnector.getBiralatByAZONO(azono);
    }

    public List<Biralat> getBiralatByTenyeszetAndFeltoltetlen(String TENAZ, Boolean FELTOLTETLEN) {
        return innerConnector.getBiralatByTENAZAndFELTOLTETLEN(TENAZ, FELTOLTETLEN);
    }

    public List<Biralat> getBiralatByFeltoltetlen(Boolean FELTOLTETLEN) {
        return innerConnector.getBiralatByFELTOLTETLEN(FELTOLTETLEN);
    }

    public List<Biralat> getBiralatByTenyeszetAndExported(String TENAZ, boolean unexported) {
        return innerConnector.getBiralatByTenyeszetAndExported(TENAZ, unexported);
    }

    // DB CONSISTENCY

    public void checkDbConsistency() throws Exception {
        String innerMD5 = getMD5EncryptedString(innerDBPath);
        String sdCardMD5 = getMD5EncryptedString(sdCardDBPath);
        if (!innerMD5.equals(sdCardMD5)) {
            throw new Exception(TAG + ":checkDbConsistency:different db");
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
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getMD5EncryptedString", e);
        } catch (IOException e) {
            Log.e(TAG, "getMD5EncryptedString", e);
        }
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "getMD5EncryptedString", e);
        }
        mdEnc.update(bytes, 0, bytes.length);
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }

    public void synchronizeDb(boolean inner) {
        File src;
        File dst;
        if (inner) {
            src = new File(innerDBPath);
            dst = new File(sdCardDBPath);
        } else {
            src = new File(sdCardDBPath);
            dst = new File(innerDBPath);
        }
        try {
            FileUtil.copyFile(src, dst);
        } catch (IOException e) {
            Log.e(TAG, "synchronizeDb", e);
        }
    }

    public String getInnerDBPath() {
        return innerDBPath;
    }

    public String getSdCardDBPath() {
        return sdCardDBPath;
    }
}
