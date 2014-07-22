package hu.flexisys.kbr.view.levalogatas;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Peter on 2014.07.21..
 */
public class LevalogatasActivity extends KbrActivity {

    private static final String TAG = "KBR_LevalogatasActivity";
    private long[] selectedTenazArray;
    private List<Egyed> egyedList;
    private Integer selectedEgyedCounter;

    private LevalogatasListViewAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levalogatas);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        selectedTenazArray = getIntent().getExtras().getLongArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
        reloadData();

        TextView telep = (TextView) findViewById(R.id.lev_telep_name);
        List<Tenyeszet> tenyeszetList = app.getTenyeszetListByTENAZArray(selectedTenazArray);
        String text = tenyeszetList.size() + " db telep";
        if (tenyeszetList.size() == 1) {
            text = tenyeszetList.get(0).getTARTO();
        }
        telep.setText(text);

        updateCounters();

        CheckBox selectAll = (CheckBox) findViewById(R.id.lev_select_all);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (Egyed egyed : egyedList) {
                    egyed.setKIVALASZTOTT(isChecked);
                }
                adapter.notifyDataSetChanged();
            }
        });

        ListView listView = (ListView) findViewById(R.id.teny_list);
        listView.setEmptyView(findViewById(R.id.empty_list_item));
        adapter = new LevalogatasListViewAdapter(this, R.layout.list_levalogatas, egyedList);
        listView.setAdapter(adapter);

    }

    private void reloadData() {
        egyedList = app.getEgyedListByTENAZArray(selectedTenazArray);
        selectedEgyedCounter = 0;

        if (egyedList.isEmpty()) {
            Log.i(TAG, "Üres az egyedlista!");
        } else {
            List<Biralat> biralatList = app.getBiralatListByTENAZArray(selectedTenazArray);
            HashMap<Long, ArrayList<Biralat>> biralatMap = new HashMap<Long, ArrayList<Biralat>>();
            for (Biralat biralat : biralatList) {
                Long azono = biralat.getAZONO();
                if (biralatMap.get(azono) == null) {
                    biralatMap.put(azono, new ArrayList<Biralat>());
                }
                biralatMap.get(azono).add(biralat);
            }
            for (int i = 0; i < egyedList.size(); i++) {
                Egyed egyed = egyedList.get(i);
                if (egyed.getKIVALASZTOTT()) {
                    selectedEgyedCounter++;
                }
                List<Biralat> currentBiralatList = biralatMap.get(egyed.getAZONO());
                if (currentBiralatList == null) {
                    currentBiralatList = new ArrayList<Biralat>();
                }
                egyed.setBiralatList(currentBiralatList);
            }
        }
    }

    private void updateCounters() {
        TextView counterView = (TextView) findViewById(R.id.lev_selected_egyed_counter);
        counterView.setText(String.valueOf(selectedEgyedCounter));
        counterView = (TextView) findViewById(R.id.lev_egyed_counter);
        counterView.setText(String.valueOf(egyedList.size()));
    }

    // MENU IN ACTIONBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_levalogatas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mentes_kilepes:
                mentes_kilepes();
                return true;
            case R.id.mentes:
                mentes();
                return true;
            case R.id.torles:
                torles();
                return true;
            case R.id.levalogatas_torles:
                levalogatas_torles();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void mentes_kilepes() {
        Log.i(TAG, "saveLevalogatasStarted");
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                saveEgyedList();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                dismissDialog();
                Log.i(TAG, "saveLevalogatasEnded");
                finish();
            }
        });
        task.execute();
    }

    public void mentes() {
        Log.i(TAG, "saveLevalogatasStarted");
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                saveEgyedList();
                reloadData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
                Log.i(TAG, "saveLevalogatasEnded");
            }
        });
        task.execute();
    }

    private void saveEgyedList() {
        for (Egyed egyed : egyedList) {
            app.updateEgyedWithSelection(egyed.getAZONO(), egyed.getKIVALASZTOTT());
        }
    }

    public void torles() {
    }

    public void levalogatas_torles() {
    }
}
