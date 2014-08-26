package hu.flexisys.kbr.view.levalogatas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.tenyeszet.LevalogatasTorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetComparator;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class LevalogatasTenyeszetActivity extends KbrActivity implements TorlesAlertListener {

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
        Collections.sort(tenyeszetList, new TenyeszetComparator());
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

    public void levalogatas() {
        Intent intent = new Intent(this, LevalogatasActivity.class);
        if (selectedList.isEmpty()) {
            return;
        }
        Bundle extras = new Bundle();
        String[] selectedTenazArray = new String[selectedList.size()];
        for (int i = 0; i < selectedList.size(); i++) {
            selectedTenazArray[i] = selectedList.get(i);
        }
        extras.putStringArray(EXTRAKEY_SELECTEDTENAZLIST, selectedTenazArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

    // KÜLDÉS

    public void kuld() {
        toast("Not implemented jet!");
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
        startProgressDialog();
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