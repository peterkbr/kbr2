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

    public int updateTenyeszetByTENAZWithERVENYES(Long TENAZ, Boolean ERVENYES) {
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

    public void deleteTenyeszet(Long tenaz) {
        dbController.removeTenyeszet(tenaz);
        try {
            dbController.checkDbConsistency();
        } catch (Exception e) {
            Log.e(TAG, "checkDbConsistency", e);
        }
    }

    public void removeSelectionFromTenyeszet(Long tenaz) {
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

    public List<Egyed> getEgyedListByTENAZArray(long[] tenazArray) {
        List<Egyed> egyedList = new ArrayList<Egyed>();
        for (long tenaz : tenazArray) {
            egyedList.addAll(dbController.getEgyedByTENAZ(tenaz));
        }
        return egyedList;
    }

    // GETTERS, SETTERS

    public String getUserId() {
        return userId;
    }

}
