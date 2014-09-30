package hu.flexisys.kbr.controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import hu.flexisys.kbr.controller.db.DBController;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.*;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.db.DbInconsistencyHandlerActivity;
import hu.flexisys.kbr.view.db.SendDbActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class KbrApplication extends Application {

    public static String errorOnInit = null;
    public static Boolean initialized = false;
    private static String TAG = "KBR2_APPLICATION";
    private static Boolean exportLog;
    private static String logFilePath;
    private static Boolean sendEmail;
    private DBController dbController;
    private KbrActivity currentActivity;

    // LOG

    @Override
    public void onCreate() {
        super.onCreate();
        startLog();
        init();
    }

    public void init() {
        BiralatSzempontUtil.initBiralatSzempontUtil(this);
        BiralatTipusUtil.initBiralatTipusUtil(this);
        NetworkUtil.initNetworkUtil(this);
        KbrApplicationUtil.initKbrApplicationUtil(this);
        SoundUtil.initSoundUtil(this);
        EmailUtil.initEmailUtil(this);
        FileUtil.initFileUtil(this);

        if (KbrApplicationUtil.initialized()) {
            initialized = true;
            try {
                dbController = new DBController(this, KbrApplicationUtil.getBiraloUserName());
            } catch (SQLiteCantOpenDatabaseException e) {
                Log.e(TAG, "init DBController", e);
                errorOnInit = "Hiba történt az adatbázis inicializálásakor!;Kérem ellenőrizze a készülék SD kártyáját!";
            }
        } else {
            initialized = false;
        }
    }

    private void startLog() {
        exportLog = false;
        sendEmail = true;

        String dirPath = FileUtil.getExternalAppPath() + File.separator + "LOG";
        File dir = new File(dirPath);
        dir.mkdirs();
        logFilePath = dirPath + File.separator + "KBRLog_" + DateUtil.getRequestId() + ".txt";

        EmptyTask exportLogTask = new EmptyTask(new Executable() {
            @Override
            public void execute() throws Exception {

                try {
                    Process process = Runtime.getRuntime().exec("logcat -v long");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    StringBuilder log = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null && !exportLog) {
                        if (line.startsWith("[ ")) {
                            log.append("\n");
                        }
                        log.append(line);
                    }

                    File logFile = new File(logFilePath);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }

                    FileOutputStream outStream = new FileOutputStream(logFile, true);
                    byte[] buffer = log.toString().getBytes();

                    outStream.write(buffer);
                    outStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "exportLog", e);
                }
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                List<String> pathList = new ArrayList<String>();
                pathList.add(logFilePath);
                if (sendEmail) {
                    EmailUtil.sendMailWithAttachments(new String[]{KbrApplicationUtil.getSupportEmail()}, "[KBR2][LOG] " + getBiraloNev(), null, pathList);
                }
                startLog();
            }
        });

        startMyTask(exportLogTask);
    }

    public void exportLog(Boolean sendEmail) {
        exportLog = true;
        KbrApplication.sendEmail = sendEmail;
    }

    public void exportLog() {
        exportLog(true);
    }

    // WRITE DB

    public void insertEgyed(Egyed egyed) {
        dbController.addEgyed(egyed);
        checkDbConsistency();
    }

    public void updateBiralat(Biralat biralat) {
        dbController.updateBiralat(biralat);
        checkDbConsistency();
    }

    public void insertTenyeszetWithChildren(Tenyeszet tenyeszet) {
        dbController.addTenyeszet(tenyeszet);
        for (Egyed egyed : tenyeszet.getEgyedList()) {
            dbController.addEgyed(egyed);
            for (Biralat biralat : egyed.getBiralatList()) {
                dbController.addBiralat(biralat);
            }
        }
        checkDbConsistency();
    }

    public int updateTenyeszetByTENAZWithERVENYES(String TENAZ, Boolean ERVENYES) {
        int count = dbController.updateTenyeszetByTENAZWithERVENYES(TENAZ, ERVENYES);
        checkDbConsistency();
        return count;
    }

    public int updateTenyeszet(Tenyeszet tenyeszet) {
        int count = dbController.updateTenyeszet(tenyeszet);
        checkDbConsistency();
        return count;
    }

    public void deleteTenyeszet(String tenaz) {
        dbController.removeTenyeszet(tenaz);
        checkDbConsistency();
    }

    public void removeSelectionFromTenyeszetList(List<String> tenazList) {
        for (String tenaz : tenazList) {
            dbController.removeSelectionFromTenyeszet(tenaz);
        }
        checkDbConsistency();
    }

    public void removeSelectionFromTenyeszetList(String[] tenazArray) {
        for (String tenaz : tenazArray) {
            dbController.removeSelectionFromTenyeszet(tenaz);
        }
        checkDbConsistency();
    }

    public void removeSelectionFromTenyeszet(String tenaz) {
        dbController.removeSelectionFromTenyeszet(tenaz);
        checkDbConsistency();
    }

    public int updateEgyedWithSelection(String azono, Boolean selection) {
        int count = dbController.updateEgyedByAZONOWithKIVALASZTOTT(azono, selection);
        checkDbConsistency();
        return count;
    }

    // READ DB

    public List<TenyeszetListModel> getTenyeszetListModels() {
        ArrayList<TenyeszetListModel> list = new ArrayList<TenyeszetListModel>();
        List<Tenyeszet> tenyeszetList = dbController.getTenyeszetAll();
        for (Tenyeszet tenyeszet : tenyeszetList) {
            TenyeszetListModel model = new TenyeszetListModel(tenyeszet);

            List<Egyed> egyedList = dbController.getEgyedTehenByTenyeszet(tenyeszet);
            model.setEgyedCount(egyedList.size());
            egyedList = dbController.getEgyedByTENAZAndKIVALASZTOTT(tenyeszet.getTENAZ(), true);
            model.setSelectedEgyedCount(egyedList.size());

            List<Biralat> biralatList = dbController.getBiralatByTenyeszetAndFeltoltetlen(tenyeszet.getTENAZ(), true);
            model.setBiralatWaitingForUpload(biralatList.size());
            biralatList = dbController.getBiralatByTENAZ(tenyeszet.getTENAZ());
            model.setBiralatCount(biralatList.size());
            biralatList = dbController.getBiralatByTenyeszetAndExported(tenyeszet.getTENAZ(), false);
            model.setBiralatUnexportedCount(biralatList.size());

            String cim = tenyeszet.getTECIM();
            if (cim != null && !cim.isEmpty()) {
                int firstIndex = cim.indexOf(" ");
                int secondIndex = cim.indexOf(" ", firstIndex + 1);
                String telepules = cim.substring(firstIndex, secondIndex);
                model.setTelepules(telepules);
            }
            list.add(model);
        }

        Collections.sort(list, new Comparator<TenyeszetListModel>() {
            @Override
            public int compare(TenyeszetListModel lhs, TenyeszetListModel rhs) {
                String lhsTARTO = lhs.getTARTO();
                String rhsTARTO = rhs.getTARTO();
                if (lhsTARTO == null && rhsTARTO == null) {
                    return 0;
                } else if (lhsTARTO == null) {
                    return -1;
                } else if (rhsTARTO == null) {
                    return 1;
                } else {
                    return lhs.getTARTO().compareToIgnoreCase(rhs.getTARTO());
                }
            }
        });

        return list;
    }

    public List<Tenyeszet> getTenyeszetListByTENAZArray(String[] tenazArray) {
        List<Tenyeszet> tenyeszetList = new ArrayList<Tenyeszet>();
        for (String tenaz : tenazArray) {
            tenyeszetList.add(dbController.getTenyeszetByTENAZ(tenaz));
        }
        return tenyeszetList;
    }

    public List<Egyed> getEgyedListByTENAZArray(String[] tenazArray) {
        List<Egyed> egyedList = new ArrayList<Egyed>();
        for (String tenaz : tenazArray) {
            egyedList.addAll(dbController.getEgyedByTENAZ(tenaz));
        }
        return egyedList;
    }

    public List<Egyed> getEgyedListByTENAZListAndKivalasztott(List<String> tenazList, boolean kivalasztott) {
        List<Egyed> egyedList = new ArrayList<Egyed>();
        for (String tenaz : tenazList) {
            egyedList.addAll(dbController.getEgyedByTENAZAndKIVALASZTOTT(tenaz, kivalasztott));
        }
        return egyedList;
    }

    public List<Biralat> getFeltoltetlenBiralatListByTenazList(List<String> tenazList) {
        List<Biralat> biralatList = new ArrayList<Biralat>();
        for (String TENAZ : tenazList) {
            biralatList.addAll(dbController.getBiralatByTenyeszetAndFeltoltetlen(TENAZ, true));
        }
        return biralatList;
    }

    public List<Biralat> getFeltoltetlenBiralatList() {
        List<Biralat> biralatList = dbController.getBiralatByFeltoltetlen(true);
        return biralatList;
    }

    public List<Biralat> getBiralatListByTENAZArray(String[] tenazArray) {
        List<Biralat> biralatList = new ArrayList<Biralat>();
        for (String tenaz : tenazArray) {
            biralatList.addAll(dbController.getBiralatByTENAZ(tenaz));
        }
        return biralatList;
    }

    public List<Biralat> getBiralatByAZONO(String AZONO) {
        List<Biralat> biralatList = dbController.getBiralatByAZONO(AZONO);
        return biralatList;
    }

    public void checkDbConsistency() {
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
            Intent intent = new Intent(currentActivity, DbInconsistencyHandlerActivity.class);
            Bundle extras = getDbPathExtras();
            intent.putExtras(extras);
            currentActivity.startActivity(intent);
        }
    }

    public void sendDbs() {
        Intent intent = new Intent(currentActivity, SendDbActivity.class);
        Bundle extras = getDbPathExtras();
        intent.putExtras(extras);
        currentActivity.startActivity(intent);
    }

    private Bundle getDbPathExtras() {
        Bundle extras = new Bundle();
        extras.putString(DbInconsistencyHandlerActivity.KEY_INNER_PATH, dbController.getInnerDBPath());
        extras.putString(DbInconsistencyHandlerActivity.KEY_SDCARD_PATH, dbController.getSdCardDBPath());
        return extras;
    }

    public void synchronizeDb(boolean inner) {
        dbController.synchronizeDb(inner);
    }

    // GETTERS, SETTERS

    public String getBiraloUserId() {
        return KbrApplicationUtil.getBiraloUserName();
    }

    public String getBiraloAzonosito() {
        return KbrApplicationUtil.getBiraloAzonosito();
    }

    public String getBiraloNev() {
        return KbrApplicationUtil.getBiraloNev();
    }

    public String getBiralatTipus() {
        return KbrApplicationUtil.getBiralatTipus();
    }

    public void setCurrentActivity(KbrActivity kbrActivity) {
        if (currentActivity == null) {
            currentActivity = kbrActivity;
            if (errorOnInit == null && initialized) {
                checkDbConsistency();
            }
        } else {
            currentActivity = kbrActivity;
        }
    }

    public Context geActivityContext() {
        return currentActivity;
    }

    public void startMyTask(AsyncTask asyncTask, Object[] params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }

    public void startMyTask(AsyncTask asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[]{});
        } else {
            asyncTask.execute(new Void[]{});
        }
    }

}
