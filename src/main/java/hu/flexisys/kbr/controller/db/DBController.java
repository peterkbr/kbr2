package hu.flexisys.kbr.controller.db;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class DBController {

    public static final String TAG = "KBR_DBController";

    private static String DB_NAME = "KBR2_DB";
    private static int DB_VERSION = 1;
    private final Context context;

    private DBConnector sdCardConnector;
    private DBConnector innerConnector;

    private String innerDBPath;
    private String sdCardDBPath;

    public DBController(Context context, String userid) {
        this.context = context;
        innerDBPath = context.getFilesDir().getAbsolutePath().toString() + File.separator + DB_NAME + "_" + userid;
        innerConnector = new DBConnector(context, innerDBPath, DB_VERSION);

        String dirPath = Environment.getExternalStorageDirectory() + File.separator + "KBR2" + File.separator + "DataBase";
        File dir = new File(dirPath);
        dir.mkdirs();
        sdCardDBPath = dir.getPath() + File.separator + DB_NAME + "_" + userid;
        sdCardConnector = new DBConnector(context, sdCardDBPath, DB_VERSION);
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

    public List<Egyed> getEgyedByTENAZ(String tenaz) {
        return innerConnector.getEgyedByTENAZ(tenaz);
    }

    public List<Egyed> getEgyedByTenyeszetAndKivalasztott(Tenyeszet tenyeszet, boolean kivalasztott) {
        return innerConnector.getEgyedByTENAZAndKIVALASZTOTT(tenyeszet.getTENAZ(), kivalasztott);
    }

    public List<Biralat> getBiralatAll() {
        return innerConnector.getBiralatAll();
    }

    public List<Biralat> getBiralatByTENAZ(String tenaz) {
        return innerConnector.getBiralatByTENAZ(tenaz);
    }

    public List<Biralat> getBiralatByTenyeszetAndToUpload(Tenyeszet tenyeszet, boolean toUpload) {
        return innerConnector.getBiralatByTENAZAndFELTOLTETLEN(tenyeszet.getTENAZ(), toUpload);
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
}
