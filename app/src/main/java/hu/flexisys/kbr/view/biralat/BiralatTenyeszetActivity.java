package hu.flexisys.kbr.view.biralat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.tenyeszet.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static hu.flexisys.kbr.controller.KbrApplication.DbCheckType.EGYED;

public class BiralatTenyeszetActivity extends KbrActivity implements TorlesAlertListener {

    public static String EXTRAKEY_SELECTEDTENAZLIST = "selectedTenazArray";
    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<>();
    private final List<String> selectedList = new ArrayList<>();
    private TenyeszetAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biralat_tenyeszet);
        setUpToolBar();

        ListView listView = findViewById(R.id.teny_list);
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

    public void levalogatasTorlese() {
        if (selectedList.isEmpty()) {
            return;
        }
        FragmentTransaction ft = getFragmentTransactionWithTag("torlesDialog");
        dialog = LevalogatasTorlesAlertDialog.newInstance(this);
        dialog.show(ft, "torlesDialog");
    }

    @Override
    public void onTorles() {
        dismissDialog();
        startProgressDialog(getString(R.string.bir_teny_progress_torles));
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                app.removeSelectionFromTenyeszetList(selectedList);
                app.checkDbConsistency(EGYED);
                reloadData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        startMyTask(task);
    }

    private void reloadData() {
        tenyeszetList.clear();
        selectedList.clear();

        List<TenyeszetListModel> rawList = app.getTenyeszetListModels();
        List<TenyeszetListModel> oldList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -14);
        for (TenyeszetListModel model : rawList) {
            if (model.getERVENYES() != null && !model.getERVENYES()) {
                continue;
            }
            if (model.getLEDAT().before(cal.getTime())) {
                oldList.add(model);
            } else {
                tenyeszetList.add(model);
            }
        }
        Collections.sort(tenyeszetList, new TenyeszetListModelComparatorByLetda());
        tenyeszetList.addAll(oldList);
    }

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