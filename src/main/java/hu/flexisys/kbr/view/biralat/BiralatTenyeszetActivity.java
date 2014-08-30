package hu.flexisys.kbr.view.biralat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.ProgressDialog;
import hu.flexisys.kbr.view.levalogatas.EmptyTask;
import hu.flexisys.kbr.view.levalogatas.Executable;
import hu.flexisys.kbr.view.levalogatas.ExecutableFinishedListener;
import hu.flexisys.kbr.view.tenyeszet.LevalogatasTorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetAdapter;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatTenyeszetActivity extends KbrActivity implements TorlesAlertListener {

    public static String EXTRAKEY_SELECTEDTENAZLIST = "selectedTenazArray";
    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<TenyeszetListModel>();
    private final List<String> selectedList = new ArrayList<String>();
    private TenyeszetAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biralat_tenyeszet);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_biralat_tenyeszet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.biral:
                biral();
                return true;
            case R.id.torles:
                levalogatasTorlese();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // LEVÁLOGATÁSOK TÖRLÉSE

    public void levalogatasTorlese() {
        if (selectedList.isEmpty()) {
            return;
        }
        FragmentTransaction ft = getFragmentTransactionWithTag("torlesDialog");
        dialog = LevalogatasTorlesAlertDialog.newInstance(this);
        dialog.show(ft, "torlesDialog");
    }

    // levalogatasTorlese
    @Override
    public void onTorles() {
        dismissDialog();
        startProgressDialog(getString(R.string.bir_teny_progress_torles));
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

    private void reloadData() {
        tenyeszetList.clear();
        selectedList.clear();

        List<TenyeszetListModel> emptyTenyeszetList = new ArrayList<TenyeszetListModel>();
        for (TenyeszetListModel model : app.getTenyeszetListModels()) {
            if (model.getERVENYES()) {
                if (model.getSelectedEgyedCount() != null && model.getSelectedEgyedCount() > 0) {
                    tenyeszetList.add(model);
                } else {
                    emptyTenyeszetList.add(model);
                }
            }
        }
        tenyeszetList.addAll(emptyTenyeszetList);
    }

    // BÍRÁLAT

    public void biral() {
        if (selectedList.isEmpty()) {
            return;
        }
        Intent intent = new Intent(this, BiralatActivity.class);
        Bundle extras = new Bundle();

        String[] selectedTenazArray = new String[selectedList.size()];
        for (int i = 0; i < selectedList.size(); i++) {
            selectedTenazArray[i] = selectedList.get(i);
        }
        extras.putStringArray(EXTRAKEY_SELECTEDTENAZLIST, selectedTenazArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

}