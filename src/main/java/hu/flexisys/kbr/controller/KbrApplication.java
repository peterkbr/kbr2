package hu.flexisys.kbr.controller;

import android.app.Application;
import android.util.Log;
import hu.flexisys.kbr.controller.db.DBController;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class KbrApplication extends Application {

    private static String TAG = "KBR_APPLICATION";
    private String userId = "jakablk";
    private DBController dbController;

    @Override
    public void onCreate() {
        super.onCreate();
        dbController = new DBController(this, userId);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public void insertEgyed(Egyed egyed) {
        dbController.addEgyed(egyed);
    }

    public void insertBiralat(Biralat biralat) {
        dbController.addBiralat(biralat);
    }

    public void insertTenyeszetWithChildren(Tenyeszet tenyeszet) {
        dbController.addTenyeszet(tenyeszet);
        for (Egyed egyed : tenyeszet.getEgyedList()) {
            dbController.addEgyed(egyed);
            for (Biralat biralat : egyed.getBiralatList()) {
                dbController.addBiralat(biralat);
            }
        }
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public int updateTenyeszetByTENAZWithERVENYES(String TENAZ, Boolean ERVENYES) {
        int count = dbController.updateTenyeszetByTENAZWithERVENYES(TENAZ, ERVENYES);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
        return count;
    }

    public int updateTenyeszet(Tenyeszet tenyeszet) {
        int count = dbController.updateTenyeszet(tenyeszet);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
        return count;
    }

    public void deleteTenyeszet(String tenaz) {
        dbController.removeTenyeszet(tenaz);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public void removeSelectionFromTenyeszetList(List<String> tenazList) {
        for (String tenaz : tenazList) {
            dbController.removeSelectionFromTenyeszet(tenaz);
        }
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public void removeSelectionFromTenyeszetList(String[] tenazArray) {
        for (String tenaz : tenazArray) {
            dbController.removeSelectionFromTenyeszet(tenaz);
        }
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public void removeSelectionFromTenyeszet(String tenaz) {
        dbController.removeSelectionFromTenyeszet(tenaz);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public List<TenyeszetListModel> getTenyeszetListModels() {
        ArrayList<TenyeszetListModel> list = new ArrayList<TenyeszetListModel>();
        List<Tenyeszet> tenyeszetList = dbController.getTenyeszetAll();
        for (Tenyeszet tenyeszet : tenyeszetList) {
            TenyeszetListModel model = new TenyeszetListModel(tenyeszet);

            List<Egyed> egyedList = dbController.getEgyedByTenyeszet(tenyeszet);
            model.setEgyedCount(egyedList.size());
            egyedList = dbController.getEgyedByTenyeszetAndKivalasztott(tenyeszet, true);
            model.setSelectedEgyedCount(egyedList.size());

            List<Biralat> biralatList = dbController.getBiralatByTenyeszetAndToUpload(tenyeszet, true);
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

    public int updateEgyedWithSelection(String azono, Boolean selection) {
        int count = dbController.updateEgyedByAZONOWithKIVALASZTOTT(azono, selection);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
        return count;
    }

    public List<Biralat> getBiralatListByTENAZArray(String[] tenazArray) {
        List<Biralat> biralatList = new ArrayList<Biralat>();
        for (String tenaz : tenazArray) {
            biralatList.addAll(dbController.getBiralatByTENAZ(tenaz));
        }
        return biralatList;
    }

    // GETTERS, SETTERS

    public String getUserId() {
        return userId;
    }
}
