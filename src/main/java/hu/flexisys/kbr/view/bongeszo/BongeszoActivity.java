package hu.flexisys.kbr.view.bongeszo;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.*;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.levalogatas.EmptyTask;
import hu.flexisys.kbr.view.levalogatas.Executable;
import hu.flexisys.kbr.view.levalogatas.ExecutableFinishedListener;

import java.text.ParseException;
import java.util.*;

/**
 * Created by Peter on 2014.07.21..
 */
public class BongeszoActivity extends KbrActivity {

    public static final String ert1 = "16";
    public static final String ert2 = "47";
    public static final String ert3 = "17";
    public static final String ert4 = "21";
    public static final String ert5 = "15";
    public static final String ert6 = "25";
    private static final String TAG = "KBR_BongeszoActivity";
    private String[] selectedTenazArray;
    private List<Biralat> biralatList;
    private Map<String, Egyed> egyedMap;
    private BongeszoListAdapter adapter;
    private Long datumTolFilter;
    private Long datumIgFilter;
    private Boolean elkuldetlenFilter;
    private SlidingPaneLayout pane;
    private String currentOrderBy;
    private Boolean asc = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bongeszo);

        View customView = LayoutInflater.from(this).inflate(R.layout.activity_bongeszo_actionbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(customView);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelOpened(View panel) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelClosed(View panel) {
                invalidateOptionsMenu();
            }
        });

        selectedTenazArray = getIntent().getExtras().getStringArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
        TextView telep = (TextView) findViewById(R.id.bong_szuk_telep_name);
        List<Tenyeszet> tenyeszetList = app.getTenyeszetListByTENAZArray(selectedTenazArray);
        String text = tenyeszetList.size() + " db telep";
        if (tenyeszetList.size() == 1) {
            text = tenyeszetList.get(0).getTARTO();
        }
        telep.setText(text);

        biralatList = new ArrayList<Biralat>();
        List<Egyed> egyedList = app.getEgyedListByTENAZArray(selectedTenazArray);
        egyedMap = new HashMap<String, Egyed>();
        for (Egyed egyed : egyedList) {
            egyedMap.put(egyed.getAZONO(), egyed);
        }

        elkuldetlenFilter = true;
        CheckBox elkuldetlenCheckBox = (CheckBox) findViewById(R.id.bong_szuk_elkuldetlen);
        elkuldetlenCheckBox.setChecked(elkuldetlenFilter);

        ListView biralatListView = (ListView) findViewById(R.id.bongeszo_bir_list);
        biralatListView.setEmptyView(findViewById(R.id.empty_list_item));
        adapter = new BongeszoListAdapter(this, 0, biralatList, egyedMap);
        biralatListView.setAdapter(adapter);

        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reloadData();
                reorderData(getString(R.string.bong_grid_header_enar));
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounter();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }

    public void reloadData() {
        biralatList.clear();
        List<Biralat> rawList = app.getBiralatListByTENAZArray(selectedTenazArray);
        for (Biralat biralat : rawList) {
            if (applyFilters(biralat)) {
                biralatList.add(biralat);
            }
        }
    }

    public void reorder(View view) {
        TextView orderByTV = (TextView) view;
        final String orderBy = orderByTV.getText().toString();
        startProgressDialog();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reorderData(orderBy);
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

    private void reorderData() {
        if (biralatList.size() < 2 || currentOrderBy == null) {
            return;
        }

        final Comparator<Biralat> comparator = new Comparator<Biralat>() {
            @Override
            public int compare(Biralat left, Biralat right) {
                Egyed leftEgyed = egyedMap.get(left.getAZONO());
                Egyed rightEgyed = egyedMap.get(right.getAZONO());
                int value = 0;
                if (currentOrderBy.equals(getString(R.string.lev_grid_header_ok))) {
                    if (!left.getORSKO().equals("HU") && right.getORSKO().equals("HU")) {
                        value = -1;
                    } else if (left.getORSKO().equals("HU") && !right.getORSKO().equals("HU")) {
                        value = 1;
                    }
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_enar))) {
                    value = Long.valueOf(left.getAZONO()).compareTo(Long.valueOf(right.getAZONO()));
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_datum))) {
                    value = left.getBIRDA().compareTo(right.getBIRDA());
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_es))) {
                    value = leftEgyed.getELLSO().compareTo(rightEgyed.getELLSO());
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert1))) {
                    String leftValue = left.getErtByKod(ert1) == null ? "" : left.getErtByKod(ert1);
                    String rightValue = right.getErtByKod(ert1) == null ? "" : right.getErtByKod(ert1);
                    value = leftValue.compareTo(rightValue);

                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert2))) {
                    String leftValue = left.getErtByKod(ert2) == null ? "" : left.getErtByKod(ert2);
                    String rightValue = right.getErtByKod(ert2) == null ? "" : right.getErtByKod(ert2);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert3))) {
                    String leftValue = left.getErtByKod(ert3) == null ? "" : left.getErtByKod(ert3);
                    String rightValue = right.getErtByKod(ert3) == null ? "" : right.getErtByKod(ert3);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert4))) {
                    String leftValue = left.getErtByKod(ert4) == null ? "" : left.getErtByKod(ert4);
                    String rightValue = right.getErtByKod(ert4) == null ? "" : right.getErtByKod(ert4);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert5))) {
                    String leftValue = left.getErtByKod(ert5) == null ? "" : left.getErtByKod(ert5);
                    String rightValue = right.getErtByKod(ert5) == null ? "" : right.getErtByKod(ert5);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert6))) {
                    String leftValue = left.getErtByKod(ert6) == null ? "" : left.getErtByKod(ert6);
                    String rightValue = right.getErtByKod(ert6) == null ? "" : right.getErtByKod(ert6);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_a))) {
                    value = left.getAKAKO().compareTo(right.getAKAKO());
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_itv))) {
                    value = leftEgyed.getITVJE().compareTo(rightEgyed.getITVJE());
                }
                if (!asc) {
                    value *= -1;
                }
                return value;
            }
        };
        Collections.sort(biralatList, comparator);
    }

    private void reorderData(String orderBy) {
        if (asc == null || currentOrderBy == null || !currentOrderBy.equals(orderBy)) {
            currentOrderBy = orderBy;
            asc = true;
        } else {
            asc = !asc;
        }
        reorderData();
    }

    private boolean applyFilters(Biralat biralat) {
        if (datumTolFilter != null && biralat.getBIRDA().getTime() < datumTolFilter) {
            return false;
        }
        if (datumIgFilter != null && biralat.getBIRDA().getTime() > datumIgFilter) {
            return false;
        }
        if (elkuldetlenFilter != null && elkuldetlenFilter && !biralat.getFELTOLTETLEN()) {
            return false;
        }
        return true;
    }

    public void updateCounter() {
        TextView counter = (TextView) findViewById(R.id.bongeszo_egyed_counter);
        if (biralatList != null) {
            counter.setText(String.valueOf(biralatList.size()));
        } else {
            counter.setText("0");
        }
    }

    private void szukit() {
        startProgressDialog();
        try {
            TextView datumTol = (TextView) findViewById(R.id.bong_szuk_datum_tol);
            datumTolFilter = DateUtil.getDateFromDateString(datumTol.getText().toString()).getTime();
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
            datumTolFilter = null;
        }
        try {
            TextView datumIg = (TextView) findViewById(R.id.bong_szuk_datum_ig);
            datumIgFilter = DateUtil.getDateFromDateString(datumIg.getText().toString()).getTime();
        } catch (ParseException e) {
            Log.e(TAG, "Date parse error", e);
            datumIgFilter = null;
        }
        CheckBox elkuldetlenCheckBox = (CheckBox) findViewById(R.id.bong_szuk_elkuldetlen);
        elkuldetlenFilter = elkuldetlenCheckBox.isChecked();

        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reloadData();
                reorderData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounter();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        task.execute();
    }

    private void urit() {
        TextView datumTol = (TextView) findViewById(R.id.bong_szuk_datum_tol);
        datumTol.setText("");
        TextView datumIg = (TextView) findViewById(R.id.bong_szuk_datum_ig);
        datumIg.setText("");
        CheckBox elkuldetlenCheckBox = (CheckBox) findViewById(R.id.bong_szuk_elkuldetlen);
        elkuldetlenCheckBox.setChecked(false);
    }

    // MENU IN ACTIONBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int resId = pane.isOpen() ? R.menu.menu_activity_bongeszo_szukites : R.menu.menu_activity_bongeszo;
        inflater.inflate(resId, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.szukites:
                pane.openPane();
                return true;
            case R.id.bongeszo_export:
                return true;
            case R.id.bongeszo_linearis:
                return true;
            case R.id.bongeszo_szukit:
                szukit();
                return true;
            case R.id.bongeszo_urit:
                urit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
