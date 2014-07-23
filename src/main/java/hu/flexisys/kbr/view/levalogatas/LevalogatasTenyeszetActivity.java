package hu.flexisys.kbr.view.levalogatas;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;

import java.util.*;

/**
 * Created by Peter on 2014.07.04..
 */
public class LevalogatasTenyeszetActivity extends KbrActivity {

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
        tenyeszetList.clear();
        selectedList.clear();
        List<TenyeszetListModel> rawList = app.getTenyeszetListModels();
        List<TenyeszetListModel> oldList = new ArrayList<TenyeszetListModel>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -14);
        for (TenyeszetListModel model : rawList) {
            if (model.getLEDAT() != null && model.getLEDAT().getTime() > 1 && model.getERVENYES()) {
                if (model.getLEDAT().before(cal.getTime())) {
                    oldList.add(model);
                } else {
                    tenyeszetList.add(model);
                }
            }
        }
        Collections.sort(tenyeszetList, new Comparator<TenyeszetListModel>() {
            @Override
            public int compare(TenyeszetListModel lhs, TenyeszetListModel rhs) {
                if (lhs.getLEDAT().getTime() < rhs.getLEDAT().getTime()) {
                    return -1;
                }
                if (lhs.getLEDAT().getTime() == rhs.getLEDAT().getTime()) {
                    return 0;
                }
                return 1;
            }
        });
        tenyeszetList.addAll(oldList);
        adapter.notifyDataSetChanged();
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
                torles();
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

    //    KÜLDÉS

    public void kuld() {
        toast("Not implemented jet!");
    }

    // TENYÉSZETEK TÖRLÉSE

    public void torles() {
        toast("Not implemented jet!");
    }


}