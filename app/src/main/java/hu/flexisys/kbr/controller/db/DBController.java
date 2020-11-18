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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBController {

    private static final int DB_VERSION = 1;
    private final Context context;

    private DBConnector innerConnector;
    private DBConnector sdCardConnector;

    private Map<String, DBConnector> innerSubConnectors = new HashMap<>();
    private Map<String, DBConnector> sdCardSubConnectors = new HashMap<>();

    private final String innerDBName;
    private final String sdCardDBName;
    private final String innerDBPath;
    private final String sdCardDBPath;

    public DBController(Context context, String userid) {
        this.context = context;
        innerDBName = "innerDB_" + userid;
        sdCardDBName = "sdcardDB_" + userid;
        innerDBPath = FileUtil.innerAppPath + File.separator + innerDBName;
        sdCardDBPath = FileUtil.externalAppPath + File.separator + "DataBase" + File.separator + sdCardDBName;
        createDBConnectors();
    }

    private String getSubDBPath(String tenaz, boolean inner) {
        String dbName = inner ? innerDBName : sdCardDBName;
        String dbPath = inner ? innerDBPath : sdCardDBPath;
        return dbPath + "_" + tenaz + File.separator + dbName + "_" + tenaz;
    }

    private String getInnerSubDBPath(String tenaz) {
        return getSubDBPath(tenaz, true);
    }

    private String getSdCardSubDBPath(String tenaz) {
        return getSubDBPath(tenaz, false);
    }

    private void createDBConnectors() {
        innerConnector = new DBConnector(context, innerDBPath, DB_VERSION);
        sdCardConnector = new DBConnector(context, sdCardDBPath, DB_VERSION);

        innerSubConnectors.clear();
        sdCardSubConnectors.clear();

        for (Tenyeszet tenyeszet : innerConnector.getTenyeszetAll()) {
            String tenaz = tenyeszet.getTENAZ();
            String innerSubPath = getInnerSubDBPath(tenaz);
            String sdSubPath = getSdCardSubDBPath(tenaz);

            DBConnector connector = new DBConnector(context, innerSubPath, DB_VERSION);
            innerSubConnectors.put(tenaz, connector);

            File sd = new File(sdSubPath);
            if (!sd.exists()) {
                try {
                    FileUtil.copyFile(new File(innerSubPath), sd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connector = new DBConnector(context, sdSubPath, DB_VERSION);
            sdCardSubConnectors.put(tenaz, connector);
        }
    }

    public void addTenyeszet(Tenyeszet tenyeszet) {
        innerConnector.insertTenyeszet(tenyeszet);
        sdCardConnector.insertTenyeszet(tenyeszet);

        String tenaz = tenyeszet.getTENAZ();
        if (innerSubConnectors.get(tenaz) == null) {
            DBConnector connector = new DBConnector(context, getInnerSubDBPath(tenaz), DB_VERSION);
            connector.insertTenyeszetWithChildren(tenyeszet);
            innerSubConnectors.put(tenaz, connector);
        }

        if (sdCardSubConnectors.get(tenaz) == null) {
            DBConnector connector = new DBConnector(context, getSdCardSubDBPath(tenaz), DB_VERSION);
            connector.insertTenyeszetWithChildren(tenyeszet);
            sdCardSubConnectors.put(tenaz, connector);
        }
    }

    public void removeTenyeszet(String TENAZ) {
        Log.i(LogUtil.TAG, "removeTenyeszet from innerConnector:" + TENAZ);
        if (innerConnector.removeTenyeszet(TENAZ) == 1) {
            innerSubConnectors.remove(TENAZ);
            new File(getInnerSubDBPath(TENAZ)).delete();
        }
        Log.i(LogUtil.TAG, "removeTenyeszet from sdCardConnector:" + TENAZ);
        if (sdCardConnector.removeTenyeszet(TENAZ) == 1) {
            sdCardSubConnectors.remove(TENAZ);
            new File(getSdCardSubDBPath(TENAZ)).delete();
        }
    }

    public void invalidateTenyeszetByTENAZ(String TENAZ) {
        innerConnector.invalidateTenyeszetByTENAZ(TENAZ);
        sdCardConnector.invalidateTenyeszetByTENAZ(TENAZ);

        innerSubConnectors.remove(TENAZ);
        new File(getInnerSubDBPath(TENAZ)).delete();

        sdCardSubConnectors.remove(TENAZ);
        new File(getSdCardSubDBPath(TENAZ)).delete();
    }

    private List<DBConnector> getSubDbConnectorsByTENAZ(String TENAZ) {
        ArrayList<DBConnector> connectors = new ArrayList<>();

        DBConnector connector = innerSubConnectors.get(TENAZ);
        if (connector != null) {
            connectors.add(connector);
        }
        connector = sdCardSubConnectors.get(TENAZ);
        if (connector != null) {
            connectors.add(connector);
        }

        return connectors;
    }

    public void removeSelectionFromTenyeszet(String TENAZ) {
        for (DBConnector dbConnector : getSubDbConnectorsByTENAZ(TENAZ)) {
            dbConnector.removeSelectionFromTenyeszet(TENAZ);
        }
    }

    public void addEgyed(Egyed egyed) {
        for (DBConnector dbConnector : getSubDbConnectorsByTENAZ(egyed.getTENAZ())) {
            dbConnector.insertEgyed(egyed);
        }
    }

    public void updateEgyedByAZONOWithKIVALASZTOTT(String TENAZ, String AZONO, Boolean KIVALASZTOTT) {
        for (DBConnector dbConnector : getSubDbConnectorsByTENAZ(TENAZ)) {
            dbConnector.updateEgyedByAZONOWithKIVALASZTOTT(AZONO, KIVALASZTOTT);
        }
    }

    public void updateBiralat(Biralat biralat) {
        for (DBConnector dbConnector : getSubDbConnectorsByTENAZ(biralat.getTENAZ())) {
            dbConnector.updateBiralat(biralat);
        }
    }

    public void removeBiralat(Biralat biralat) {
        for (DBConnector dbConnector : getSubDbConnectorsByTENAZ(biralat.getTENAZ())) {
            dbConnector.deleteBiralat(biralat);
        }
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

    public int getEgyedTehenCountByTENAZ(String tenaz) {
        return innerSubConnectors.get(tenaz).getEgyedTehenCountByTENAZ(tenaz);
    }

    public List<Egyed> getEgyedByTENAZ(String tenaz) {
        return innerSubConnectors.get(tenaz).getEgyedByTENAZ(tenaz);
    }

    public List<Egyed> getEgyedByTENAZAndKIVALASZTOTT(String TENAZ, boolean KIVALASZTOTT) {
        return innerSubConnectors.get(TENAZ).getEgyedByTENAZAndKIVALASZTOTT(TENAZ, KIVALASZTOTT);
    }

    public int getEgyedCountByTENAZAndKIVALASZTOTT(String TENAZ, boolean KIVALASZTOTT) {
        return innerSubConnectors.get(TENAZ).getEgyedCountByTENAZAndKIVALASZTOTT(TENAZ, KIVALASZTOTT);
    }

    public List<Biralat> getBiralatByTENAZ(String tenaz) {
        return innerSubConnectors.get(tenaz).getBiralatByTENAZ(tenaz);
    }

    public int getBiralatCountByTENAZ(String tenaz) {
        return innerSubConnectors.get(tenaz).getBiralatCountByTENAZ(tenaz);
    }

    public List<Biralat> getBiralatByAZONO(String TENAZ, String azono) {
        return innerSubConnectors.get(TENAZ).getBiralatByAZONO(azono);
    }

    public List<Biralat> getBiralatByTenyeszetAndFeltoltetlen(String TENAZ, Boolean FELTOLTETLEN) {
        return innerSubConnectors.get(TENAZ).getBiralatByTENAZAndFELTOLTETLEN(TENAZ, FELTOLTETLEN);
    }

    public int getBiralatCountByTenyeszetAndFeltoltetlen(String TENAZ, Boolean FELTOLTETLEN) {
        return innerSubConnectors.get(TENAZ).getBiralatCountByTENAZAndFELTOLTETLEN(TENAZ, FELTOLTETLEN);
    }

    public int getBiralatCountByFeltoltetlen(Boolean FELTOLTETLEN) {
        int count = 0;
        for (Tenyeszet tenyeszet : innerConnector.getTenyeszetAll()) {
            count += innerSubConnectors.get(tenyeszet.getTENAZ()).getBiralatCountByFELTOLTETLEN(FELTOLTETLEN);
        }
        return count;
    }

    public int getBiralatCountByTenyeszetAndExported(String TENAZ, boolean unexported) {
        return innerSubConnectors.get(TENAZ).getBiralatCountByTenyeszetAndExported(TENAZ, unexported);
    }

    public void checkDbConsistency(String tenaz) throws Exception {
        String innerMD5 = getMD5EncryptedString(innerSubConnectors.get(tenaz).path);
        String sdCardMD5 = getMD5EncryptedString(sdCardSubConnectors.get(tenaz).path);
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
            createDBConnectors();
            return;
        }
        try {
            File sdDir = new File(FileUtil.externalAppPath + File.separator + "DataBase");
            sdDir.delete();
            FileUtil.copyFile(src, dst);
            createDBConnectors();
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "synchronizeDb", e);
        }
    }

    public ArrayList<String> getInnerDBPaths() {
        ArrayList<String> paths = new ArrayList<>();
        paths.add(innerDBPath);
        for (Tenyeszet tenyeszet : innerConnector.getTenyeszetAll()) {
            paths.add(innerSubConnectors.get(tenyeszet.getTENAZ()).path);
        }
        return paths;
    }

    public ArrayList<String> getSdCardDBPaths() {
        ArrayList<String> paths = new ArrayList<>();
        paths.add(sdCardDBPath);
        for (Tenyeszet tenyeszet : sdCardConnector.getTenyeszetAll()) {
            paths.add(sdCardSubConnectors.get(tenyeszet.getTENAZ()).path);
        }
        return paths;
    }
}