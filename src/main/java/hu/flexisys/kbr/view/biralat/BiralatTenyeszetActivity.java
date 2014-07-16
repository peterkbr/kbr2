package hu.flexisys.kbr.view.biralat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.db.RemoveSelectionFromTenyeszetArrayTask;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetAdapter;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BiralatTenyeszetActivity extends KbrActivity implements TorlesAlertListener {

    public static String EXTRAKEY_SELECTEDTENAZLIST = "selectedTenazArray";
    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<TenyeszetListModel>();
    private final List<Long> selectedList = new ArrayList<Long>();
    private TenyeszetAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biralat_tenyeszet);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ListView listView = (ListView) findViewById(R.id.teny_list);
        reloadData();
        adapter = new TenyeszetAdapter(this, R.layout.list_tenyeszet, tenyeszetList, selectedList);
        listView.setAdapter(adapter);
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
                torles();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // LEVÁLOGATÁSOK TÖRLÉSE

    public void torles() {
        if (selectedList.isEmpty()) {
            return;
        }
        FragmentTransaction ft = getFragmentTransactionWithTag("torlesDialog");
        dialog = TorlesAlertDialog.newInstance(this);
        dialog.show(ft, "torlesDialog");
    }

    @Override
    public void onTorles() {
        dialog.dismiss();
        startProgressDialog();

        RemoveSelectionFromTenyeszetArrayTask removeSelectionFromTenyeszetArrayTask = new RemoveSelectionFromTenyeszetArrayTask(app, this);
        removeSelectionFromTenyeszetArrayTask.execute(selectedList.toArray());

        reloadData();
        adapter.notifyDataSetChanged();
    }

    private void reloadData() {
        tenyeszetList.clear();
        selectedList.clear();
        for (TenyeszetListModel model : app.getTenyeszetListModels()) {
            if (model.getERVENYES() && model.getSelectedEgyedCount() != null && model.getSelectedEgyedCount() > 0) {
                tenyeszetList.add(model);
            }
        }
    }

    // BÍRÁLAT

    public void biral() {
        if (selectedList.isEmpty()) {
            return;
        }
        Intent intent = new Intent(this, BiralatActivity.class);
        Bundle extras = new Bundle();
        long[] selectedTenazArray = new long[selectedList.size()];
        for (int i = 0; i < selectedList.size(); i++) {
            selectedTenazArray[i] = selectedList.get(i);
        }
        extras.putLongArray(EXTRAKEY_SELECTEDTENAZLIST, selectedTenazArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

}