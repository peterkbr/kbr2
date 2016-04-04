package hu.flexisys.kbr.view.bongeszo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.Log;
import android.view.*;
import android.widget.*;
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
import hu.flexisys.kbr.view.KbrDialog;
import hu.flexisys.kbr.view.ProgressDialog;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.bongeszo.biralatdialog.BiralatDialogEditActivity;
import hu.flexisys.kbr.view.bongeszo.diagram.DiagramActivity;
import hu.flexisys.kbr.view.bongeszo.export.ExportDialog;

import java.io.File;
import java.text.ParseException;
import java.util.*;

public class BongeszoActivity extends KbrActivity {

    public static final String ert1_hus = "53";
    public static final String ert2_hus = "40";
    public static final String ert3_hus = "41";
    public static final String ert4_hus = "42";

    public static final String ert1_tej = "61";
    public static final String ert2_tej = "62";
    public static final String ert3_tej = "63";
    public static final String ert4_tej = "64";

    public static String ert1 = ert1_tej;
    public static String ert2 = ert2_tej;
    public static String ert3 = ert3_tej;
    public static String ert4 = ert4_tej;

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

    private KbrDialog dialog2;

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

        final RadioButton husButton = (RadioButton) findViewById(R.id.bong_szuk_hus_radio);
        final RadioButton tejButton = (RadioButton) findViewById(R.id.bong_szuk_tej_radio);
        husButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tejButton.setChecked(!isChecked);
            }
        });
        tejButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                husButton.setChecked(!isChecked);
            }
        });

        urit();

        findViewById(R.id.list_bong_header_hus_layout).setVisibility(View.GONE);

        ListView biralatListView = (ListView) findViewById(R.id.bongeszo_bir_list);
        biralatListView.setEmptyView(findViewById(R.id.empty_list_item));
        adapter = new BongeszoListAdapter(this, 0, biralatList, egyedMap, new BongeszoListAdapter.BongeszoListContainer() {
            @Override
            public void onLongClick(final Egyed currentEgyed, final Biralat currentBiralat) {
                if (currentBiralat.getEXPORTALT()) {
                    showBiralatViewEdit(currentEgyed, currentBiralat);
                } else {
                    FragmentTransaction ft = getFragmentTransactionWithTag("longClick");
                    dialog = BongLongClickDialog.newInstance(new BongLongClickDialog.LongClickDialogListener() {

                        @Override
                        public void onView() {
                            dismissDialog();
                            showBiralatViewEdit(currentEgyed, currentBiralat);
                        }

                        @Override
                        public void onDelete() {
                            dismissDialog();
                            FragmentTransaction ft = getFragmentTransactionWithTag("delete");
                            dialog2 = null;
                            dialog2 = BongLongClickDeleteDialog.newInstance(new BongLongClickDeleteDialog.BongLongClickDeleteDialogListener() {
                                @Override
                                public void onDelete() {
                                    dialog2.dismiss();
                                    startProgressDialog(getString(R.string.bong_progress_delete));
                                    app.removeBiralat(currentBiralat);
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

                                @Override
                                public void onDismiss() {
                                    dialog2.dismiss();
                                }
                            }, currentEgyed);
                            dialog2.show(ft, "delete");
                        }

                        @Override
                        public void onDismiss() {
                            dismissDialog();
                        }
                    });
                    dialog.show(ft, "longClick");

                }
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

    private void showBiralatViewEdit(Egyed currentEgyed, Biralat currentBiralat) {
        Intent intent = new Intent(BongeszoActivity.this, BiralatDialogEditActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable(BiralatDialogEditActivity.KEY_EGYED, currentEgyed);
        extras.putSerializable(BiralatDialogEditActivity.KEY_BIRALAT, currentBiralat);
        intent.putExtras(extras);
        startActivity(intent);
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

        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(BiralatTipusUtil.getCurrentBiralatTipus());
        for (Biralat biralat : rawList) {
            String biralatTipusKod = String.valueOf(biralat.getBIRTI());
            if (!tipus.id.equals(biralatTipusKod)) {
                continue;
            }
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
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert1_hus))) {
                    String leftValue = left.getErtByKod(ert1) == null ? "" : left.getErtByKod(ert1);
                    String rightValue = right.getErtByKod(ert1) == null ? "" : right.getErtByKod(ert1);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert2_hus))) {
                    String leftValue = left.getErtByKod(ert2) == null ? "" : left.getErtByKod(ert2);
                    String rightValue = right.getErtByKod(ert2) == null ? "" : right.getErtByKod(ert2);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert3_hus))) {
                    String leftValue = left.getErtByKod(ert3) == null ? "" : left.getErtByKod(ert3);
                    String rightValue = right.getErtByKod(ert3) == null ? "" : right.getErtByKod(ert3);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert4_hus))) {
                    String leftValue = left.getErtByKod(ert4) == null ? "" : left.getErtByKod(ert4);
                    String rightValue = right.getErtByKod(ert4) == null ? "" : right.getErtByKod(ert4);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert1_tej))) {
                    String leftValue = left.getErtByKod(ert1) == null ? "" : left.getErtByKod(ert1);
                    String rightValue = right.getErtByKod(ert1) == null ? "" : right.getErtByKod(ert1);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert2_tej))) {
                    String leftValue = left.getErtByKod(ert2) == null ? "" : left.getErtByKod(ert2);
                    String rightValue = right.getErtByKod(ert2) == null ? "" : right.getErtByKod(ert2);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert3_tej))) {
                    String leftValue = left.getErtByKod(ert3) == null ? "" : left.getErtByKod(ert3);
                    String rightValue = right.getErtByKod(ert3) == null ? "" : right.getErtByKod(ert3);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ert4_tej))) {
                    String leftValue = left.getErtByKod(ert4) == null ? "" : left.getErtByKod(ert4);
                    String rightValue = right.getErtByKod(ert4) == null ? "" : right.getErtByKod(ert4);
                    value = leftValue.compareTo(rightValue);
                } else if (currentOrderBy.equals(getString(R.string.bong_grid_header_ak))) {
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
        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(BiralatTipusUtil.getCurrentBiralatTipus());
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
            String biralatTipusKod = String.valueOf(biralat.getBIRTI());
            if (!tipus.id.equals(biralatTipusKod)) {
                continue;
            }
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
                                BiralatPdfExporter.export(dir.getPath(), BiralatTipusUtil.getCurrentBiralatTipus(), biralatList, egyedMap);
                            }
                            if (csv) {
                                BiralatCvsExporter.export(dir.getPath(), BiralatTipusUtil.getCurrentBiralatTipus(), biralatList, egyedMap);
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

        final RadioButton husButton = (RadioButton) findViewById(R.id.bong_szuk_hus_radio);
        if (husButton.isChecked()) {
            BiralatTipusUtil.setCurrentBiralatTipus(BiralatTipusUtil.HUS_BIRALAT_TIPUS);
            findViewById(R.id.list_bong_header_hus_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.list_bong_header_tej_layout).setVisibility(View.GONE);
            ert1 = ert1_hus;
            ert2 = ert2_hus;
            ert3 = ert3_hus;
            ert4 = ert4_hus;
        } else {
            BiralatTipusUtil.setCurrentBiralatTipus(BiralatTipusUtil.TEJ_BIRALAT_TIPUS);
            findViewById(R.id.list_bong_header_hus_layout).setVisibility(View.GONE);
            findViewById(R.id.list_bong_header_tej_layout).setVisibility(View.VISIBLE);
            ert1 = ert1_tej;
            ert2 = ert2_tej;
            ert3 = ert3_tej;
            ert4 = ert4_tej;
        }

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
        elkuldetlenCheckBox.setChecked(true);

        final RadioButton husButton = (RadioButton) findViewById(R.id.bong_szuk_hus_radio);
        husButton.setChecked(false);
        BiralatTipusUtil.setCurrentBiralatTipus(BiralatTipusUtil.HUS_BIRALAT_TIPUS);
        final RadioButton tejButton = (RadioButton) findViewById(R.id.bong_szuk_tej_radio);
        tejButton.setChecked(true);
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
