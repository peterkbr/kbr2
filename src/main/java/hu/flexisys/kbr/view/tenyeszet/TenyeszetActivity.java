package hu.flexisys.kbr.view.tenyeszet;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.controller.db.RemoveTenyeszetArrayTask;
import hu.flexisys.kbr.controller.network.tenyeszet.DownloadTenyeszetArrayTask;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.NotificationDialog;
import hu.flexisys.kbr.view.ProgressHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class TenyeszetActivity extends KbrActivity implements FelveszListener, TorlesAlertListener {

    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<TenyeszetListModel>();
    private final List<String> selectedList = new ArrayList<String>();
    private TenyeszetAdapter adapter;
    private List<String> origOrder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenyeszet);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ListView listView = (ListView) findViewById(R.id.teny_list);
        tenyeszetList.addAll(app.getTenyeszetListModels());
        for (TenyeszetListModel model : tenyeszetList) {
            if (model.getERVENYES() != null && !model.getERVENYES()) {
                selectedList.add(model.getTENAZ());
            }
        }
        adapter = new TenyeszetAdapter(this, R.layout.list_tenyeszet, tenyeszetList, selectedList);
        listView.setAdapter(adapter);
    }

    // MENU IN ACTIONBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_tenyeszet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.felvesz:
                felvesz();
                return true;
            case R.id.letoltes:
                letoltes();
                return true;
            case R.id.torles:
                torles();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOrigOrder() {
        origOrder = new ArrayList<String>();
        for (TenyeszetListModel tenyeszet : tenyeszetList) {
            origOrder.add(tenyeszet.getTENAZ());
        }
    }

    // TENYÉSZETEK FELVÉTELE

    public void felvesz() {
        FragmentTransaction ft = getFragmentTransactionWithTag("felveszDialog");
        FelveszDialog newFragment = FelveszDialog.newInstance(this);
        newFragment.show(ft, "felveszDialog");
    }

    @Override
    public void onFelvesz(String tenaz) {
        if (tenaz == null || tenaz.isEmpty()) {
            toast(R.string.teny_felvesz_error_invalid);
        } else if (isDuplicate(tenaz)) {
            toast(R.string.teny_felvesz_error_duplicate);
        } else {
            Tenyeszet tenyeszet = new Tenyeszet(tenaz);
            tenyeszet.setERVENYES(false);
            tenyeszet.setLEDAT(new Date(1));
            app.insertTenyeszetWithChildren(tenyeszet);

            TenyeszetListModel model = new TenyeszetListModel(tenyeszet);
            tenyeszetList.add(0, model);
            selectedList.add(model.getTENAZ());
            adapter.notifyDataSetChanged();
        }
    }

    private Boolean isDuplicate(String tenaz) {
        for (TenyeszetListModel tenyeszet : tenyeszetList) {
            if (tenaz.equals(tenyeszet.getTENAZ())) {
                return true;
            }
        }
        return false;
    }

    // TENYÉSZETEK LETÖLTÉSE

    public void letoltes() {
        if (selectedList.isEmpty()) {
            return;
        }

        for (TenyeszetListModel model : tenyeszetList) {
            if (selectedList.contains(model.getTENAZ()) && model.getBiralatWaitingForUpload() != null && model.getBiralatWaitingForUpload() > 0) {
                FragmentTransaction ft = getFragmentTransactionWithTag("notificationDialog");
                String title = getString(R.string.teny_notification_download_error_biralat_title);
                dialog = NotificationDialog.newInstance(title, null);
                dialog.show(ft, "notificationDialog");
                return;
            }
        }

        startProgressDialog();
        setOrigOrder();
        DownloadTenyeszetArrayTask downloadTenyeszetArrayTask = new DownloadTenyeszetArrayTask(app, new ProgressHandler() {
            @Override
            public void onProgressEnded() {
                tenyeszetList.clear();
                for (int j = 0; j < origOrder.size(); j++) {
                    tenyeszetList.add(null);
                }
                ArrayList<String> errorList = new ArrayList<String>();

                List<TenyeszetListModel> newList = app.getTenyeszetListModels();
                for (TenyeszetListModel newTenyeszet : newList) {
                    int index = origOrder.indexOf(newTenyeszet.getTENAZ());
                    if (selectedList.contains(newTenyeszet.getTENAZ()) && !newTenyeszet.getERVENYES()) {
                        errorList.add(newTenyeszet.getTENAZ());
                    }
                    tenyeszetList.set(index, newTenyeszet);
                }
                adapter.notifyDataSetChanged();
                dismissDialog();

                String title = null;
                String message = null;
                if (errorList.isEmpty()) {
                    title = getString(R.string.teny_notification_download_ok_title);
                } else {
                    title = getString(R.string.teny_notification_download_error_title);
                    StringBuilder errorMessageBuilder = new StringBuilder();
                    for (String tezan : errorList) {
                        if (errorMessageBuilder.length() != 0) {
                            errorMessageBuilder.append("\n");
                        }
                        errorMessageBuilder.append(tezan);
                    }
                    message = errorMessageBuilder.toString();
                }

                FragmentTransaction ft = getFragmentTransactionWithTag("notificationDialog");
                dialog = NotificationDialog.newInstance(title, message);
                dialog.show(ft, "notificationDialog");
            }
        });
        downloadTenyeszetArrayTask.execute(selectedList.toArray());
    }

    // TENYÉSZETEK TÖRLÉSE

    public void torles() {
        if (selectedList.isEmpty()) {
            return;
        }
        for (TenyeszetListModel model : tenyeszetList) {
            if (selectedList.contains(model.getTENAZ()) && model.getBiralatWaitingForUpload() != null && model.getBiralatWaitingForUpload() > 0) {
                FragmentTransaction ft = getFragmentTransactionWithTag("notificationDialog");
                String title = getString(R.string.teny_notification_delete_biralat_title);
                String message = getString(R.string.teny_notification_delete_biralat_message);
                dialog = NotificationDialog.newInstance(title, message);
                dialog.show(ft, "notificationDialog");
                return;
            }
        }
        FragmentTransaction ft = getFragmentTransactionWithTag("torlesDialog");
        dialog = TorlesAlertDialog.newInstance(this);
        dialog.show(ft, "torlesDialog");
    }

    @Override
    public void onTorles() {
        dismissDialog();
        startProgressDialog();
        for (String tenaz : selectedList) {
            tenyeszetList.remove(new TenyeszetListModel(tenaz));
        }
        adapter.notifyDataSetChanged();
        RemoveTenyeszetArrayTask removeTenyeszetArrayTask = new RemoveTenyeszetArrayTask(app, this);
        removeTenyeszetArrayTask.execute(selectedList.toArray());
        selectedList.clear();
    }

}