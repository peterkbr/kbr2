package hu.flexisys.kbr.view.bongeszo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BongeszoTenyeszetActivity extends KbrActivity {

    public static String EXTRAKEY_SELECTEDTENAZLIST = "selectedTenazArray";
    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<TenyeszetListModel>();
    private final List<String> selectedList = new ArrayList<String>();
    private BongeszoTenyeszetAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bongeszo_tenyeszet);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        ListView listView = (ListView) findViewById(R.id.teny_list);
        adapter = new BongeszoTenyeszetAdapter(this, R.layout.list_tenyeszet, tenyeszetList, selectedList);
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
        for (TenyeszetListModel model : rawList) {
            if (model.getERVENYES()) {
                Boolean hasBiralat = model.getBiralatWaitingForUpload() > 0;
                if (!hasBiralat) {
                    for (Egyed egyed : model.getTenyeszet().getEgyedList()) {
                        if (egyed.getBiralatList().size() > 0) {
                            hasBiralat = true;
                            break;
                        }
                    }
                }
                if (hasBiralat) {
                    if (model.getBiralatWaitingForUpload() < 1) {
                        oldList.add(model);
                    } else {
                        tenyeszetList.add(model);
                    }
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
                bongeszes();
                return true;
            case R.id.kuld:
                kuld();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // LEVÁLOGATÁS

    public void bongeszes() {
//        Intent intent = new Intent(this, BongeszoActivity.class);
//        if (selectedList.isEmpty()) {
//            return;
//        }
//        Bundle extras = new Bundle();
//        String[] selectedTenazArray = new String[selectedList.size()];
//        for (int i = 0; i < selectedList.size(); i++) {
//            selectedTenazArray[i] = selectedList.get(i);
//        }
//        extras.putStringArray(EXTRAKEY_SELECTEDTENAZLIST, selectedTenazArray);
//        intent.putExtras(extras);
//        startActivity(intent);
        toast("Not implemented jet!");
    }

    // KÜLDÉS

    public void kuld() {
        toast("Not implemented jet!");
    }

}