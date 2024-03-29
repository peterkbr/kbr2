package hu.flexisys.kbr.controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.db.DBController;
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
import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.util.*;

/**
 * Created by Peter on 2014.07.01..
 */
@ReportsCrashes(formKey = "",
        mailTo = "kbr@flexisys.hu",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
public class KbrApplication extends Application {

    public static String errorOnInit = null;
    public static Boolean initialized = false;
    private DBController dbController;
    private KbrActivity currentActivity;

    private int activityCounter = 0;
    private boolean downloading = false;

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
        init();
        LogUtil.initLogUtil(this);
        LogUtil.startLog();
    }

    public void init() {
        BiralatSzempontUtil.initBiralatSzempontUtil(this);
        BiralatTipusUtil.initBiralatTipusUtil(this);
        NetworkUtil.initNetworkUtil(this);
        KbrApplicationUtil.initKbrApplicationUtil(this);
        SoundUtil.initSoundUtil(this);
        EmailUtil.initEmailUtil(this);

        if (KbrApplicationUtil.initialized()) {
            initialized = true;
            try {
                FileUtil.initFileUtil(this);
                dbController = new DBController(this, KbrApplicationUtil.getBiraloUserName());
            } catch (Exception e) {
                Log.e(LogUtil.TAG, "init DBController", e);
                errorOnInit = "Hiba történt az adatbázis inicializálásakor!;Kérem, ellenőrizze a készülék SD kártyáját!";
            }
        } else {
            initialized = false;
        }
    }

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
                dbController.updateBiralat(biralat);
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

    public void removeBiralat(Biralat biralat) {
        dbController.removeBiralat(biralat);
        checkDbConsistency();
    }

    public int updateEgyedWithSelection(String azono, Boolean selection) {
        int count = dbController.updateEgyedByAZONOWithKIVALASZTOTT(azono, selection);
        checkDbConsistency();
        return count;
    }

    public List<TenyeszetListModel> getTenyeszetListModels() {
        return getTenyeszetListModels(true, false, false);
    }

    public List<TenyeszetListModel> getTenyeszetListModels(boolean withEgyedCount, boolean withBiralatWaitingForUpdate, boolean withBiralatCount) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -14);

        ArrayList<TenyeszetListModel> list = new ArrayList<TenyeszetListModel>();
        if (dbController == null) {
            init();
        }
        List<Tenyeszet> tenyeszetList = dbController.getTenyeszetAll();
        for (Tenyeszet tenyeszet : tenyeszetList) {

            TenyeszetListModel model = new TenyeszetListModel(this, tenyeszet);
            if (!model.getLEDAT().before(cal.getTime())) {
                updateTenyeszetModel(model);
            } else {
                if (withEgyedCount) {
                    updateTenyeszetModelWithEgyedCount(model);
                }
                if (withBiralatWaitingForUpdate) {
                    updateTenyeszetModelWithBiralatWaitingForUpdate(model);
                }
                if (withBiralatCount) {
                    updateTenyeszetModelWithBiralatCount(model);
                }
            }

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

    public void updateTenyeszetModel(TenyeszetListModel model) {
        Tenyeszet tenyeszet = model.getTenyeszet();
        model.setEgyedCount(dbController.getEgyedTehenCountByTenyeszet(tenyeszet));
        model.setSelectedEgyedCount(dbController.getEgyedCountByTENAZAndKIVALASZTOTT(tenyeszet.getTENAZ(), true));
        model.setBiralatWaitingForUpload(dbController.getBiralatCountByTenyeszetAndFeltoltetlen(tenyeszet.getTENAZ(), true));
        model.setBiralatCount(dbController.getBiralatCountByTENAZ(tenyeszet.getTENAZ()));
        model.setBiralatUnexportedCount(dbController.getBiralatCountByTenyeszetAndExported(tenyeszet.getTENAZ(), false));
    }

    public void updateTenyeszetModelWithEgyedCount(TenyeszetListModel model) {
        Tenyeszet tenyeszet = model.getTenyeszet();
        model.setEgyedCount(dbController.getEgyedTehenCountByTenyeszet(tenyeszet));
    }

    public void updateTenyeszetModelWithBiralatWaitingForUpdate(TenyeszetListModel model) {
        Tenyeszet tenyeszet = model.getTenyeszet();
        model.setBiralatWaitingForUpload(dbController.getBiralatCountByTenyeszetAndFeltoltetlen(tenyeszet.getTENAZ(), true));
    }

    public void updateTenyeszetModelWithBiralatCount(TenyeszetListModel model) {
        Tenyeszet tenyeszet = model.getTenyeszet();
        model.setBiralatCount(dbController.getBiralatCountByTENAZ(tenyeszet.getTENAZ()));
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

    public int getFeltoltetlenBiralatCount() {
        return dbController.getBiralatCountByFeltoltetlen(true);
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
            Log.e(LogUtil.TAG, "checkDbConsistency", e);
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
        activityCounter++;
//        if (currentActivity == null) {
//            currentActivity = kbrActivity;
//            if (errorOnInit == null && initialized) {
//                checkDbConsistency();
//            }
//        } else {
//            currentActivity = kbrActivity;
//        }
        currentActivity = kbrActivity;
    }

    public void activityDestroyed() {
        activityCounter--;
        if (activityCounter < 1) {
            LogUtil.stopLog();
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

    public boolean isDownloading() {
        return downloading;
    }

    public void startDownloading() {
        downloading = true;
    }

    public void finishedDownloading() {
        downloading = false;
    }
}
