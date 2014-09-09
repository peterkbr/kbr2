package hu.flexisys.kbr.view.levalogatas;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableErrorListener;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.EmailUtil;
import hu.flexisys.kbr.util.export.LevalogatasCvsExporter;
import hu.flexisys.kbr.util.export.LevalogatasPdfExporter;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.bongeszo.export.ExportDialog;
import hu.flexisys.kbr.view.tenyeszet.LevalogatasTorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModelComparatorByLetda;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class LevalogatasTenyeszetActivity extends KbrActivity implements TorlesAlertListener {

    private static final String TAG = "LevalogatasTenyeszetActivity";
    public static String EXTRAKEY_SELECTEDTENAZLIST = "selectedTenazArray";
    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<TenyeszetListModel>();
    private final List<String> selectedList = new ArrayList<String>();
    private LevalogatasTenyeszetAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levalogatas_tenyeszet);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ListView listView = (ListView) findViewById(R.id.teny_list);
        adapter = new LevalogatasTenyeszetAdapter(this, R.layout.list_tenyeszet, tenyeszetList, selectedList);
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
            if (model.getEgyedCount() > 0 && model.getLEDAT() != null && model.getLEDAT().getTime() > 1 && model.getERVENYES()) {
                if (model.getLEDAT().before(cal.getTime())) {
                    oldList.add(model);
                } else {
                    tenyeszetList.add(model);
                }
            }
        }
        Collections.sort(tenyeszetList, new TenyeszetListModelComparatorByLetda());
        tenyeszetList.addAll(oldList);
    }

    // MENU IN ACTIONBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_levalogatas_tenyeszet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tovabb:
                levalogatas();
                return true;
            case R.id.kuld:
                kuld();
                return true;
            case R.id.torles:
                levalogatasTorlese();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // LEVÁLOGATÁS

    private String[] getSelectedTenazArray() {
        String[] selectedTenazArray = new String[selectedList.size()];
        for (int i = 0; i < selectedList.size(); i++) {
            selectedTenazArray[i] = selectedList.get(i);
        }
        return selectedTenazArray;
    }

    public void levalogatas() {
        Intent intent = new Intent(this, LevalogatasActivity.class);
        if (selectedList.isEmpty()) {
            return;
        }
        Bundle extras = new Bundle();
        String[] selectedTenazArray = getSelectedTenazArray();
        extras.putStringArray(EXTRAKEY_SELECTEDTENAZLIST, selectedTenazArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

    // KÜLDÉS

    public void kuld() {
        if (selectedList.size() > 0) {
            FragmentTransaction ft = getFragmentTransactionWithTag("exportDialog");
            dialog = ExportDialog.newInstance(new ExportDialog.ExportListener() {
                @Override
                public void onExport(final boolean pdf, final boolean csv) {
                    String dirPath = Environment.getExternalStorageDirectory() + File.separator + "KBR2" + File.separator + "Export" + File.separator + "Leválogatás";
                    final File dir = new File(dirPath);
                    dir.mkdirs();
                    dismissDialog();

                    startProgressDialog(getString(R.string.bong_progress_export));
                    new EmptyTask(new Executable() {
                        @Override
                        public void execute() throws Exception {
                            List<Egyed> selectedEgyedList = app.getEgyedListByTENAZListAndKivalasztott(selectedList, true);

                            List<String> pathList = new ArrayList<String>();
                            if (pdf) {
                                LevalogatasPdfExporter.initLevalogatasPdfExporter("TODO : TENAZ", "TODO : TARTO", app.getBiraloNev());
                                String pdfFilePath = LevalogatasPdfExporter.export(dir.getPath(), selectedEgyedList);
                                pathList.add(pdfFilePath);
                            }
                            if (csv) {
                                String csvFilePath = LevalogatasCvsExporter.export(dir.getPath(), selectedEgyedList);
                                pathList.add(csvFilePath);
                            }

                            StringBuilder subjectBuider = null;
                            for (String tenaz : selectedList) {
                                if (subjectBuider == null) {
                                    subjectBuider = new StringBuilder();
                                } else {
                                    subjectBuider.append(",");
                                }
                                subjectBuider.append(tenaz);
                            }
                            subjectBuider.append(" leválogatott egyedei");

                            EmailUtil.sendMailWithAttachments(null, subjectBuider.toString(), null, pathList);

                        }
                    }, new ExecutableFinishedListener() {
                        @Override
                        public void onFinished() {
                            dismissDialog();
                        }
                    }, new ExecutableErrorListener() {
                        @Override
                        public void onError(Exception e) {
                            dismissDialog();
                            Log.e(TAG, e.getMessage(), e);
                            // TODO i18n
                            Toast.makeText(LevalogatasTenyeszetActivity.this, "Hiba történt az exportálás során! Az SD kártya nem írható.", Toast.LENGTH_LONG).show();
                        }
                    }).execute();
                }
            });
            dialog.show(ft, "exportDialog");
        }
    }

    private void addFileAttachement(Intent intent, String filePath) {

    }

    // LEVÁLOGATÁS TÖRLÉSE

    public void levalogatasTorlese() {
        FragmentTransaction ft = getFragmentTransactionWithTag("levalogatasTorlesDialog");
        dialog = LevalogatasTorlesAlertDialog.newInstance(this);
        dialog.show(ft, "levalogatasTorlesDialog");
    }

    // levalogatasTorlese
    @Override
    public void onTorles() {
        dismissDialog();
        startProgressDialog(getString(R.string.lev_teny_progress_torles));
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                app.removeSelectionFromTenyeszetList(selectedList);
                reloadData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }


}