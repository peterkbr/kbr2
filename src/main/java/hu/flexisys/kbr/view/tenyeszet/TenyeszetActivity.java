package hu.flexisys.kbr.view.tenyeszet;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.network.tenyeszet.DownloadTenyeszetArrayTask;
import hu.flexisys.kbr.controller.network.tenyeszet.DownloadTenyeszetHandler;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.NotificationDialog;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;

import java.util.*;

/**
 * Created by Peter on 2014.07.04..
 */
public class TenyeszetActivity extends KbrActivity {

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
        adapter = new TenyeszetAdapter(this, R.layout.list_tenyeszet, tenyeszetList, selectedList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadData();
        adapter.notifyDataSetChanged();
    }

    private void reloadData() {
        tenyeszetList.clear();
        selectedList.clear();

        List<TenyeszetListModel> rawList = app.getTenyeszetListModels();
        List<TenyeszetListModel> oldList = new ArrayList<TenyeszetListModel>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -14);
        for (TenyeszetListModel model : rawList) {
            if (model.getLEDAT().before(cal.getTime())) {
                oldList.add(model);
            } else {
                tenyeszetList.add(model);
            }
        }
        Collections.sort(tenyeszetList, new TenyeszetListModelComparatorByLetda());
        tenyeszetList.addAll(oldList);

        for (TenyeszetListModel model : tenyeszetList) {
            if (model.getERVENYES() != null && !model.getERVENYES()) {
                selectedList.add(model.getTENAZ());
            }
        }
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
        FelveszDialog newFragment = FelveszDialog.newInstance(new FelveszListener() {

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
        });
        newFragment.show(ft, "felveszDialog");
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

        startProgressDialog(getString(R.string.teny_progress_letoltes));
        setOrigOrder();
        DownloadTenyeszetArrayTask downloadTenyeszetArrayTask = new DownloadTenyeszetArrayTask(app, new DownloadTenyeszetHandler() {
            @Override
            public void onDownloadFinished(HashMap<String, String> resultMap) {
                tenyeszetList.clear();
                for (int j = 0; j < origOrder.size(); j++) {
                    tenyeszetList.add(null);
                }

                List<TenyeszetListModel> newList = app.getTenyeszetListModels();
                for (TenyeszetListModel newTenyeszet : newList) {
                    int index = origOrder.indexOf(newTenyeszet.getTENAZ());
                    tenyeszetList.set(index, newTenyeszet);
                }
                adapter.notifyDataSetChanged();
                dismissDialog();

                StringBuilder messageBuilder = new StringBuilder();
                for (String TENAZ : origOrder) {
                    String messageRow = resultMap.get(TENAZ);
                    if (messageRow != null && !messageRow.isEmpty()) {
                        if (messageBuilder.length() != 0) {
                            messageBuilder.append("\n");
                        }
                        messageBuilder.append("\n").append(TENAZ).append("\n").append(messageRow);
                    }
                }
                messageBuilder.append("\n");
                String title = getString(R.string.teny_notification_download_title);
                String message = messageBuilder.toString();

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
        dialog = TorlesAlertDialog.newInstance(new TorlesAlertListener() {

            @Override
            public void onTorles() {
                dismissDialog();

                startProgressDialog(getString(R.string.teny_progress_torles));
                EmptyTask torolTask = new EmptyTask(new Executable() {
                    @Override
                    public void execute() throws Exception {
                        for (String tenaz : selectedList) {
                            app.deleteTenyeszet(tenaz);
                        }
                    }
                }, new ExecutableFinishedListener() {
                    @Override
                    public void onFinished() {
                        dismissDialog();

                        List<String> oldList = new ArrayList<String>(selectedList);
                        reloadData();
                        adapter.notifyDataSetChanged();

                        StringBuilder buider = null;
                        for (String tenaz : oldList) {
                            if (buider == null) {
                                buider = new StringBuilder();
                            } else {
                                buider.append("\n");
                            }
                            boolean deleted = true;
                            for (TenyeszetListModel model : tenyeszetList) {
                                if (model.getTENAZ().equals(tenaz)) {
                                    deleted = false;
                                    break;
                                }
                            }
                            buider.append(tenaz).append(" - ");
                            if (deleted) {
                                buider.append("Törölve");
                            } else {
                                buider.append("Törlés sikertelen");
                            }
                        }

                        FragmentTransaction ft = getFragmentTransactionWithTag("delete");
                        dialog = NotificationDialog.newInstance(buider.toString(), null);
                        dialog.show(ft, "delete");
                    }
                });
                torolTask.execute();
            }
        });
        dialog.show(ft, "torlesDialog");
    }
}