package hu.flexisys.kbr.view.bongeszo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.*;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.emptytask.*;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.FileUtil;
import hu.flexisys.kbr.util.LogUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;
import hu.flexisys.kbr.util.export.BiralatCvsExporter;
import hu.flexisys.kbr.util.export.BiralatPdfExporter;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.ProgressDialog;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.bongeszo.biralatdialog.BiralatDialogEditActivity;
import hu.flexisys.kbr.view.bongeszo.diagram.DiagramActivity;
import hu.flexisys.kbr.view.bongeszo.export.ExportDialog;

import java.io.File;
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
        adapter = new BongeszoListAdapter(this, 0, biralatList, egyedMap, new BongeszoListAdapter.BongeszoListContainer() {
            @Override
            public void onLongClick(Egyed currentEgyed, Biralat currentBiralat) {
                Intent intent = new Intent(BongeszoActivity.this, BiralatDialogEditActivity.class);
                Bundle extras = new Bundle();
                extras.putSerializable(BiralatDialogEditActivity.KEY_EGYED, currentEgyed);
                extras.putSerializable(BiralatDialogEditActivity.KEY_BIRALAT, currentBiralat);
                intent.putExtras(extras);
                startActivity(intent);
            }

            @Override
            public void showMegjegyzes(String megjegyzes) {
                FragmentTransaction ft = getFragmentTransactionWithTag("megjegyzes");
                dialog = BongMegjegyzesDialog.newInstance(megjegyzes);
                dialog.show(ft, "megjegyzes");
            }
        });
        biralatListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        startProgressDialog(ProgressDialog.BASE_TITLE);
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                reloadData();
                if (currentOrderBy == null || currentOrderBy.isEmpty()) {
                    reorderData(getString(R.string.bong_grid_header_enar));
                } else {
                    reorderData();
                }
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                updateCounter();
                adapter.notifyDataSetChanged();
                dismissDialog();
                pane.closePane();
            }
        });
        startMyTask(task);
    }

    // LIST

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
        EmptyTask task = new EmptyTask(new PreExecutable() {
            @Override
            public void preExecute() {
                startProgressDialog(getString(R.string.bong_progress_sorbarendezes));
            }
        }, new Executable() {
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
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_enar))) {
                    value = left.getAZONO().compareTo(right.getAZONO());
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

    // DIAGRAM

    private void startDiagramActivity() {
        ArrayList<String> values = getDiagramValues();
        Intent intent = new Intent(this, DiagramActivity.class);
        Bundle extras = new Bundle();
        extras.putStringArrayList(DiagramActivity.VALUES_KEY, values);
        intent.putExtras(extras);
        startActivity(intent);
    }

    private ArrayList<String> getDiagramValues() {
        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(app.getBiralatTipus());
        List<BiralatSzempont> szempontList = new ArrayList<BiralatSzempont>();
        Map<String, Integer[]> diagramValueMap = new HashMap<String, Integer[]>();
        for (String szempontId : tipus.szempontList) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(szempontId);
            szempontList.add(szempont);
            Integer[] arr = new Integer[szempont.kategoriaBounds.length];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = 0;
            }
            diagramValueMap.put(szempont.kod, arr);
        }

        for (Biralat biralat : biralatList) {
            for (BiralatSzempont szempont : szempontList) {
                String ertString = biralat.getErtByKod(szempont.kod);
                if (ertString == null || ertString.isEmpty()) {
                    continue;
                }
                Integer ert = Integer.parseInt(ertString);
                int k = 0;
                for (int i = 1; i < szempont.kategoriaBounds.length; i++) {
                    if (ert >= szempont.kategoriaBounds[i]) {
                        k = i;
                    }
                }
                Integer[] arr = diagramValueMap.get(szempont.kod);
                arr[k]++;
                diagramValueMap.put(szempont.kod, arr);
            }
        }

        ArrayList<String> diagramValuesList = new ArrayList<String>();
        for (BiralatSzempont szempont : szempontList) {
            Integer[] values = diagramValueMap.get(szempont.kod);
            StringBuilder builder = new StringBuilder(szempont.rovidNev);


            Integer sum = 0;
            for (Integer i : values) {
                sum += i;
            }
            Double d = 100d / sum;
            for (int i = 0; i < values.length; i++) {
                builder.append(",").append((int) Math.floor(values[i] * d));
            }

//            Integer sum = values[0] + values[1] + values[2];
//            Double i = 100d / sum;
//            Integer value_0 = (int) Math.floor(values[0] * i);
//            Integer value_1 = (int) Math.floor(values[1] * i);
//            Integer value_2 = (int) Math.floor(values[2] * i);
//            builder.append(szempont.rovidNev).append(",").append(value_0).append(",").append(value_1).append(",").append(value_2);

            diagramValuesList.add(builder.toString());
        }
        return diagramValuesList;
    }

    // EXPORT

    private void export() {
        if (biralatList.size() > 0) {
            FragmentTransaction ft = getFragmentTransactionWithTag("exportDialog");
            dialog = ExportDialog.newInstance(new ExportDialog.ExportListener() {
                @Override
                public void onExport(final boolean pdf, final boolean csv) {
                    String dirPath = FileUtil.getExternalAppPath() + File.separator + "Export" + File.separator + "Bírálatok";
                    final File dir = new File(dirPath);
                    dir.mkdirs();
                    dismissDialog();

                    startProgressDialog(getString(R.string.bong_progress_export));
                    EmptyTask emptyTask = new EmptyTask(new Executable() {
                        @Override
                        public void execute() throws Exception {
                            StringBuilder tenazBuilder = new StringBuilder();
                            StringBuilder tartoBuilder = new StringBuilder();
                            List<Tenyeszet> selectedTenyeszetList = app.getTenyeszetListByTENAZArray(selectedTenazArray);
                            for (Tenyeszet tenyeszet : selectedTenyeszetList) {
                                if (tenazBuilder.length() > 0) {
                                    tenazBuilder.append(", ");
                                }
                                tenazBuilder.append(tenyeszet.getTENAZ());

                                if (tartoBuilder.length() > 0) {
                                    tartoBuilder.append(", ");
                                }
                                tartoBuilder.append(tenyeszet.getTARTO());
                            }

                            if (pdf) {
                                BiralatPdfExporter.initPdfExporter(tenazBuilder.toString(), tartoBuilder.toString(), app.getBiraloNev());
                                BiralatPdfExporter.export(dir.getPath(), app.getBiralatTipus(), biralatList, egyedMap);
                            }
                            if (csv) {
                                BiralatCvsExporter.export(dir.getPath(), app.getBiralatTipus(), biralatList, egyedMap);
                            }
                            for (Biralat biralat : biralatList) {
                                if (!biralat.getEXPORTALT()) {
                                    biralat.setEXPORTALT(true);
                                    app.updateBiralat(biralat);
                                }
                            }
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
                    }, new ExecutableErrorListener() {
                        @Override
                        public void onError(Exception e) {
                            dismissDialog();
                            Log.e(LogUtil.TAG, e.getMessage(), e);
                            // TODO i18n
                            Toast.makeText(BongeszoActivity.this, "Hiba történt az exportálás során! Az SD kártya nem írható.", Toast.LENGTH_LONG).show();
                        }
                    });
                    startMyTask(emptyTask);
                }
            });
            dialog.show(ft, "exportDialog");
        }
    }

    // SZŰKÍTÉS

    private void szukit() {
        startProgressDialog(getString(R.string.bong_progress_szukites));
        try {
            TextView datumTol = (TextView) findViewById(R.id.bong_szuk_datum_tol);
            datumTolFilter = DateUtil.getDateFromDateString(datumTol.getText().toString()).getTime();
        } catch (ParseException e) {
            Log.e(LogUtil.TAG, "Date parse error", e);
            datumTolFilter = null;
        }
        try {
            TextView datumIg = (TextView) findViewById(R.id.bong_szuk_datum_ig);
            datumIgFilter = DateUtil.getDateFromDateString(datumIg.getText().toString()).getTime();
        } catch (ParseException e) {
            Log.e(LogUtil.TAG, "Date parse error", e);
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
        startMyTask(task);
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
            case android.R.id.home:
                onHomeClicked();
                return true;
            case R.id.szukites:
                pane.openPane();
                return true;
            case R.id.bongeszo_export:
                export();
                return true;
            case R.id.bongeszo_linearis:
                startDiagramActivity();
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

    private void onHomeClicked() {
        finish();
    }
}
