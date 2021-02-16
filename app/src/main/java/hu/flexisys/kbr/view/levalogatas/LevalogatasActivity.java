package hu.flexisys.kbr.view.levalogatas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.LogUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.ProgressDialog;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.levalogatas.biralatdialog.BiralatDialogActivity;
import hu.flexisys.kbr.view.tenyeszet.LevalogatasTorlesAlertDialog;
import hu.flexisys.kbr.view.tenyeszet.TorlesAlertListener;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static hu.flexisys.kbr.controller.KbrApplication.DbCheckType.EGYED;

/**
 * Created by Peter on 2014.07.21..
 */
public class LevalogatasActivity extends KbrActivity implements SelectionChangedAlertListener, TorlesAlertListener {

    private String[] selectedTenazArray;
    private List<Egyed> egyedList;
    private Integer selectedEgyedCounter;
    private List<String> selectionChangedEgyedAzonoList;
    private boolean allChanged;
    private LevalogatasListViewAdapter adapter;

    private SlidingPaneLayout pane;
    private Filter filter;
    private String currentOrderBy;
    private Boolean asc = true;
    private CheckBox selectAll;
    private boolean ignoreSelectAll = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levalogatas);
        setUpToolBar();
        View customView = LayoutInflater.from(this).inflate(R.layout.activity_levalogatas_actionbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(customView);

        filter = new Filter();
        urit();
        updateFilterValues();
        CheckBox checkBox = (CheckBox) findViewById(R.id.lev_szuk_mar_ellett_chb);
        checkBox.setChecked(true);

        selectedTenazArray = getIntent().getExtras().getStringArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
        egyedList = new ArrayList<Egyed>();

        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                invalidateOptionsMenu();
                final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(panel.getWindowToken(), 0);
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

        TextView telep = (TextView) findViewById(R.id.lev_szuk_telep_name);
        List<Tenyeszet> tenyeszetList = app.getTenyeszetListByTENAZArray(selectedTenazArray);
        String text = tenyeszetList.size() + " db telep";
        if (tenyeszetList.size() == 1) {
            text = tenyeszetList.get(0).getTARTO();
        }
        telep.setText(text);

        selectAll = (CheckBox) findViewById(R.id.lev_select_all);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (ignoreSelectAll) {
                    ignoreSelectAll = false;
                    return;
                }
                for (Egyed egyed : egyedList) {
                    egyed.setKIVALASZTOTT(isChecked);
                }
                allChanged = true;
                adapter.notifyDataSetChanged();
            }
        });

        ListView listView = (ListView) findViewById(R.id.teny_list);
        listView.setEmptyView(findViewById(R.id.empty_list_item));
        selectionChangedEgyedAzonoList = new ArrayList<String>();
        allChanged = false;
        adapter = new LevalogatasListViewAdapter(this, R.layout.list_levalogatas, egyedList, selectionChangedEgyedAzonoList,
                new LevalogatasListViewAdapter.EgyedListContainer() {
                    @Override
                    public void onLongClick(Egyed egyed) {
                        Intent intent = new Intent(LevalogatasActivity.this, BiralatDialogActivity.class);
                        Bundle extras = new Bundle();
                        extras.putString(BiralatDialogActivity.KEY_AZONO, egyed.getAZONO());
                        intent.putExtras(extras);
                        startActivity(intent);
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                FragmentTransaction ft = getFragmentTransactionWithTag("longClick");
//                dialog = BiralatDialog.newInstance(egyed.getBiralatList());
//                dialog.show(ft, "longClick");
                    }
                });
        listView.setAdapter(adapter);

        final RadioButton huButton = (RadioButton) findViewById(R.id.lev_szuk_hu_radio);
        final RadioButton kuButton = (RadioButton) findViewById(R.id.lev_szuk_ku_radio);
        huButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    kuButton.setChecked(false);
                }
            }
        });
        kuButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    huButton.setChecked(false);
                }
            }
        });

        startProgressDialog(ProgressDialog.BASE_TITLE);
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reloadData();
                reorderData(getString(R.string.lev_grid_header_szuletett));
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        startMyTask(task);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        onHomeClicked();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int resId = pane.isOpen() ? R.menu.menu_activity_levalogatas_szukites : R.menu.menu_activity_levalogatas;
        inflater.inflate(resId, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                onHomeClicked();
                return true;
            case R.id.szukites:
                showSzukites();
                return true;
            case R.id.mentes_kilepes:
                mentes_kilepes();
                return true;
            case R.id.mentes:
                mentes();
                resetSelectAll();
                return true;
            case R.id.torles:
                torles();
                resetSelectAll();
                return true;
            case R.id.levalogatas_torles:
                levalogatasTorles();
                resetSelectAll();
                return true;
            case R.id.szukit:
                szukit();
                resetSelectAll();
                return true;
            case R.id.urit:
                urit();
                return true;
            case R.id.kivalasztottak:
                szukitToKivalasztottak();
                resetSelectAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void resetSelectAll() {
        ignoreSelectAll = true;
        selectAll.setChecked(false);
        ignoreSelectAll = false;
    }

    private void onHomeClicked() {
        if (allChanged || selectionChangedEgyedAzonoList.size() > 0) {
            FragmentTransaction ft = getFragmentTransactionWithTag("selectionChangedDialog");
            dialog = SelectionChangedAlertDialog.newInstance(this);
            dialog.show(ft, "selectionChangedDialog");
        } else {
            finish();
        }
    }

    // onHomeClicked
    @Override
    public void onKilepes() {
        dismissDialog();
        finish();
    }

    // onHomeClicked
    @Override
    public void onMentesEsKilepes() {
        dismissDialog();
        mentes_kilepes();
    }

    private void reloadData() {
        egyedList.clear();
        selectedEgyedCounter = 0;
        List<Egyed> rawList = app.getEgyedListByTENAZArray(selectedTenazArray);

        List<Biralat> biralatList = app.getBiralatListByTENAZArray(selectedTenazArray);
        HashMap<String, ArrayList<Biralat>> biralatMap = new HashMap<String, ArrayList<Biralat>>();
        for (Biralat biralat : biralatList) {
            String azono = biralat.getAZONO();
            if (biralatMap.get(azono) == null) {
                biralatMap.put(azono, new ArrayList<Biralat>());
            }
            biralatMap.get(azono).add(biralat);
        }

        for (Egyed egyed : rawList) {
            List<Biralat> currentBiralatList = biralatMap.get(egyed.getAZONO());
            if (currentBiralatList == null) {
                currentBiralatList = new ArrayList<Biralat>();
            }
            egyed.setBiralatList(currentBiralatList);
            if (applyFilter(egyed)) {
                egyedList.add(egyed);
                if (egyed.getKIVALASZTOTT()) {
                    selectedEgyedCounter++;
                }
            }
        }
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

    private void reorderData() {
        if (egyedList.size() < 2 || currentOrderBy == null) {
            return;
        }
        final Comparator<Egyed> comparator = new Comparator<Egyed>() {
            @Override
            public int compare(Egyed leftEgyed, Egyed rightEgyed) {
                int value = 0;
                if (currentOrderBy.equals(getString(R.string.lev_grid_header_ok))) {
                    String leftOrsko = leftEgyed.getORSKO();
                    String rightOrsko = rightEgyed.getORSKO();
                    if (leftOrsko.equals(rightOrsko)) {
                        value = 0;
                    } else if (leftOrsko.equals("HU")) {
                        value = -1;
                    } else if (rightOrsko.equals("HU")) {
                        value = 1;
                    } else {
                        return leftOrsko.compareTo(rightOrsko);
                    }
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_enar))) {
                    value = leftEgyed.getAZONO().compareTo(rightEgyed.getAZONO());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_es))) {
                    value = leftEgyed.getELLSO().compareTo(rightEgyed.getELLSO());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_elles_datuma))) {
                    value = leftEgyed.getELLDA().compareTo(rightEgyed.getELLDA());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_szuletett))) {
                    value = leftEgyed.getSZULD().compareTo(rightEgyed.getSZULD());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_kon))) {
                    value = leftEgyed.getKONSK().compareTo(rightEgyed.getKONSK());
                } else if (currentOrderBy.equals(getString(R.string.lev_grid_header_itv))) {
                    value = leftEgyed.getITVJE().compareTo(rightEgyed.getITVJE());
                }
                if (!asc) {
                    value *= -1;
                }
                return value;
            }
        };

        Collections.sort(egyedList, comparator);
    }

    private boolean applyFilter(Egyed egyed) {
        Boolean marEllett = (Boolean) filter.get(Filter.MAR_ELLETT);
        if (marEllett != null && marEllett && (egyed.getELLDA() == null || egyed.getELLDA().getTime() <= 1)) {
            return false;
        }

        Boolean nemBiralt = (Boolean) filter.get(Filter.NEM_BIRALT);
        if (nemBiralt != null && nemBiralt && !egyed.getBiralatList().isEmpty()) {
            return false;
        }

        Boolean itv = (Boolean) filter.get(Filter.ITV);
        if (itv != null && itv && (egyed.getITVJE() == null || !egyed.getITVJE())) {
            return false;
        }

        Boolean kivalasztott = (Boolean) filter.get(Filter.KIVALASZTOTTAK);
        if (kivalasztott != null && kivalasztott && (egyed.getKIVALASZTOTT() == null || !egyed.getKIVALASZTOTT())) {
            return false;
        }

        Boolean hu = (Boolean) filter.get(Filter.HU);
        if (hu != null && ((hu && !egyed.getORSKO().equals("HU")) || (!hu && egyed.getORSKO().equals("HU")))) {
            return false;
        }

        String esFilter = (String) filter.get(Filter.ELLES_SORSZAMAI);
        if (esFilter != null && !esFilter.isEmpty()) {
            String[] esArray = esFilter.split(",");
            Boolean pass = false;
            for (String es : esArray) {
                if (es != null && !es.isEmpty() && Integer.valueOf(es).equals(egyed.getELLSO())) {
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                return false;
            }
        }

        String enarFilter = (String) filter.get(Filter.ENAR);
        if (enarFilter != null && !enarFilter.isEmpty()) {
            String[] enarArray = enarFilter.split(",");
            Boolean pass = false;
            for (String enar : enarArray) {
                if (egyed.getAZONO().contains(String.valueOf(enar))) {
                    pass = true;
                    break;
                }
            }
            if (!pass) {
                return false;
            }
        }


        Long szuletesTol = (Long) filter.get(Filter.SZULETES_TOL);
        Long szuletesIg = (Long) filter.get(Filter.SZULETES_IG);
        Long szuletes = egyed.getSZULD().getTime();
        if ((szuletesTol != null && szuletes < szuletesTol) || (szuletesIg != null && szuletes > szuletesIg)) {
            return false;
        }

        Long utolsoEllesTol = (Long) filter.get(Filter.UTOLSO_ELLES_TOL);
        Long utolsoEllesIg = (Long) filter.get(Filter.UTOLSO_ELLES_IG);
        Long utolsoElles = egyed.getELLDA().getTime();
        if ((utolsoEllesTol != null && utolsoElles < utolsoEllesTol) || (utolsoEllesIg != null && utolsoElles > utolsoEllesIg)) {
            return false;
        }

        Integer konTol = (Integer) filter.get(Filter.KONSTRUKCIOS_TOL);
        Integer konIg = (Integer) filter.get(Filter.KONSTRUKCIOS_IG);
        Integer kon = egyed.getKONSK();
        if ((konTol != null && kon < konTol) || (konIg != null && kon > konIg)) {
            return false;
        }

        return true;
    }

    private void updateCounters() {
        TextView counterView = (TextView) findViewById(R.id.lev_szuk_selected_egyed_counter);
        counterView.setText(String.valueOf(selectedEgyedCounter));
        counterView = (TextView) findViewById(R.id.lev_szuk_egyed_counter);
        counterView.setText(String.valueOf(egyedList.size()));
    }

    public void levalogatasTorles() {
        FragmentTransaction ft = getFragmentTransactionWithTag("levalogatasTorlesDialog");
        dialog = LevalogatasTorlesAlertDialog.newInstance(this);
        dialog.show(ft, "levalogatasTorlesDialog");
    }

    // levalogatasTorlese
    @Override
    public void onTorles() {
        dismissDialog();
        startProgressDialog(getString(R.string.lev_progress_levalogatas_torlese));
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                app.removeSelectionFromTenyeszetList(selectedTenazArray);
                app.checkDbConsistency(EGYED);
                reloadData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        startMyTask(task);
    }

    // LEVÁLOGATÁS
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void mentes_kilepes() {
        startProgressDialog(getString(R.string.lev_progress_mentes));
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                saveEgyedList();
                selectionChangedEgyedAzonoList.clear();
                allChanged = false;
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                dismissDialog();
                finish();
            }
        });
        startMyTask(task);
    }

    public void mentes() {
        startProgressDialog(getString(R.string.lev_progress_mentes));
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                saveEgyedList();
                selectionChangedEgyedAzonoList.clear();
                allChanged = false;
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                urit();
                updateFilterValues();
                EmptyTask newTask = new EmptyTask(new Executable() {
                    @Override
                    public void execute() throws Exception {
                        reloadData();
                    }
                }, new ExecutableFinishedListener() {
                    @Override
                    public void onFinished() {
                        updateCounters();
                        adapter.notifyDataSetChanged();
                        dismissDialog();
                    }
                });
                startMyTask(newTask);
            }
        });
        startMyTask(task);
    }

    private void saveEgyedList() {
        for (Egyed egyed : egyedList) {
            if (allChanged || selectionChangedEgyedAzonoList.contains(egyed.getAZONO())) {
                app.updateEgyedWithSelection(egyed.getAZONO(), egyed.getKIVALASZTOTT());
            }
        }
        app.checkDbConsistency(EGYED);
    }

    public void torles() {
        startProgressDialog(getString(R.string.lev_progress_szurok_torlese));
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                filter.clear();
                reloadData();
                selectionChangedEgyedAzonoList.clear();
                allChanged = false;
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        startMyTask(task);
    }

    public void reorder(View view) {
        TextView orderByTV = (TextView) view;
        final String orderBy = orderByTV.getText().toString();
        startProgressDialog(getString(R.string.lev_progress_sorbarendezes));
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
        startMyTask(task);
    }

    // SZŰKÍTÉS
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------

    public void szukit() {
        startProgressDialog(getString(R.string.lev_progress_szukites));
        updateFilterValues();
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reloadData();
                reorderData();
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounters();
                adapter.notifyDataSetChanged();
                dismissDialog();
            }
        });
        startMyTask(task);
    }

    private void szukitToKivalasztottak() {
        urit();
        setCheckBox(R.id.lev_szuk_kivalasztottak_chb, true);
        szukit();
    }

    private void updateFilterValues() {
        Boolean marEllett = getBooleanFromCheckBox(R.id.lev_szuk_mar_ellett_chb);
        filter.put(Filter.MAR_ELLETT, marEllett);

        Boolean nemBiralt = getBooleanFromCheckBox(R.id.lev_szuk_nem_biralt_chb);
        filter.put(Filter.NEM_BIRALT, nemBiralt);

        Boolean itv = getBooleanFromCheckBox(R.id.lev_szuk_itv_chb);
        filter.put(Filter.ITV, itv);

        Boolean kivalasztottak = getBooleanFromCheckBox(R.id.lev_szuk_kivalasztottak_chb);
        filter.put(Filter.KIVALASZTOTTAK, kivalasztottak);

        RadioButton huRadioButton = (RadioButton) findViewById(R.id.lev_szuk_hu_radio);
        RadioButton kuRadioButton = (RadioButton) findViewById(R.id.lev_szuk_ku_radio);
        if (huRadioButton.isChecked()) {
            filter.put(Filter.HU, true);
        } else if (kuRadioButton.isChecked()) {
            filter.put(Filter.HU, false);
        } else {
            filter.put(Filter.HU, null);
        }

        String ellesSorszamai = getStringFromEditText(R.id.lev_szuk_elles_sorszamai);
        if (ellesSorszamai == null || ellesSorszamai.isEmpty()) {
            filter.put(Filter.ELLES_SORSZAMAI, null);
        } else {
            Pattern patt = Pattern.compile("([0-9]{1,3},)*[0-9]{1,3},?");
            Matcher matcher = patt.matcher(ellesSorszamai);
            if (matcher.matches()) {
                filter.put(Filter.ELLES_SORSZAMAI, ellesSorszamai);
            } else {
                Toast.makeText(this, "Az ellés sorszáma(i)ra megadott feltétel hibás, így azt kihagytuk a szűrésből.", Toast.LENGTH_LONG).show();
                filter.put(Filter.ELLES_SORSZAMAI, null);
            }
        }

        String enar = getStringFromEditText(R.id.lev_szuk_enar);
        filter.put(Filter.ENAR, enar);

        String konTol = getStringFromEditText(R.id.lev_szuk_konstrukcios_tol);
        try {
            if (konTol != null && !konTol.isEmpty()) {
                Integer konTolInteger = Integer.valueOf(konTol);
                filter.put(Filter.KONSTRUKCIOS_TOL, konTolInteger);
            } else {
                filter.put(Filter.KONSTRUKCIOS_TOL, null);
            }
        } catch (Exception e) {
            Toast.makeText(this, "A konstrukciós kód kezdőértékére megadott feltétel túl nagy, így azt kihagytuk a szűrésből.", Toast.LENGTH_LONG).show();
            filter.put(Filter.KONSTRUKCIOS_TOL, null);
        }

        String konIg = getStringFromEditText(R.id.lev_szuk_konstrukcios_ig);
        try {
            if (konIg != null && !konIg.isEmpty()) {
                Integer konIgInteger = Integer.valueOf(konIg);
                filter.put(Filter.KONSTRUKCIOS_IG, konIgInteger);
            } else {
                filter.put(Filter.KONSTRUKCIOS_IG, null);
            }
        } catch (Exception e) {
            Toast.makeText(this, "A konstrukciós kód végértékére megadott feltétel túl nagy, így azt kihagytuk a szűrésből.", Toast.LENGTH_LONG).show();
            filter.put(Filter.KONSTRUKCIOS_IG, null);
        }

        Long ellesTol = getDateInLongFromTextView(R.id.lev_szuk_utolso_elles_tol);
        filter.put(Filter.UTOLSO_ELLES_TOL, ellesTol);

        Long ellesIg = getDateInLongFromTextView(R.id.lev_szuk_utolso_elles_ig);
        filter.put(Filter.UTOLSO_ELLES_IG, ellesIg);

        Long szuletesTol = getDateInLongFromTextView(R.id.lev_szuk_szuletes_tol);
        filter.put(Filter.SZULETES_TOL, szuletesTol);

        Long szuletesIg = getDateInLongFromTextView(R.id.lev_szuk_szuletes_ig);
        filter.put(Filter.SZULETES_IG, szuletesIg);
    }

    private Boolean getBooleanFromCheckBox(int resId) {
        CheckBox checkBox = (CheckBox) findViewById(resId);
        Boolean checkValue = checkBox.isChecked();
        if (!checkValue) {
            checkValue = null;
        }
        return checkValue;
    }

    private String getStringFromEditText(int resId) {
        EditText editText = (EditText) findViewById(resId);
        String filterValue = editText.getText().toString();
        if (filterValue == null || filterValue.isEmpty()) {
            filterValue = null;
        }
        return filterValue;
    }

    private Long getDateInLongFromTextView(int resId) {
        TextView textView = (TextView) findViewById(resId);
        String filterValue = String.valueOf(textView.getText());
        Long longValue = null;
        try {
            if (filterValue != null && !filterValue.isEmpty()) {
                longValue = DateUtil.getDateFromDateString(filterValue).getTime();
            }
        } catch (ParseException e) {
            Log.e(LogUtil.TAG, "Date parse error", e);
        }
        return longValue;
    }

    public void urit() {
        setCheckBox(R.id.lev_szuk_mar_ellett_chb, true);
        clearCheckBox(R.id.lev_szuk_nem_biralt_chb);
        clearCheckBox(R.id.lev_szuk_itv_chb);
        clearCheckBox(R.id.lev_szuk_kivalasztottak_chb);

        clearEditText(R.id.lev_szuk_elles_sorszamai);
        clearEditText(R.id.lev_szuk_enar);
        clearEditText(R.id.lev_szuk_konstrukcios_tol);
        clearEditText(R.id.lev_szuk_konstrukcios_ig);

        clearTextView(R.id.lev_szuk_utolso_elles_tol);
        clearTextView(R.id.lev_szuk_utolso_elles_ig);
        clearTextView(R.id.lev_szuk_szuletes_tol);
        clearTextView(R.id.lev_szuk_szuletes_ig);

        clearRadioButton(R.id.lev_szuk_hu_radio);
        clearRadioButton(R.id.lev_szuk_ku_radio);
    }

    private void setCheckBox(int resId, boolean value) {
        CheckBox checkBox = (CheckBox) findViewById(resId);
        checkBox.setChecked(value);
    }

    private void clearCheckBox(int resId) {
        setCheckBox(resId, false);
    }

    private void clearEditText(int resId) {
        EditText editText = (EditText) findViewById(resId);
        editText.setText("");
    }

    private void clearTextView(int resId) {
        TextView textView = (TextView) findViewById(resId);
        textView.setText("");
    }

    private void clearRadioButton(int resId) {
        RadioButton radioButton = (RadioButton) findViewById(resId);
        radioButton.setChecked(false);
    }

    public void showSzukites() {
        pane.openPane();
    }

}
