package hu.flexisys.kbr.view.bongeszo.biralatdialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.SoundUtil;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.KbrDialog;
import hu.flexisys.kbr.view.biralat.biral.*;
import hu.flexisys.kbr.view.levalogatas.biralatdialog.BiralatDialog;

import java.util.*;

/**
 * Created by peter on 04/09/14.
 */
public class BiralatDialogEditActivity extends KbrActivity {

    public static String KEY_EGYED = "KEY_EGYED";
    public static String KEY_BIRALAT = "KEY_BIRALAT";

    public BiralFragment biralFragment;
    private KbrDialog dialog2;
    private Egyed selectedEgyed;
    private Biralat selectedBiralat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedEgyed = (Egyed) getIntent().getExtras().getSerializable(KEY_EGYED);
        selectedBiralat = (Biralat) getIntent().getExtras().getSerializable(KEY_BIRALAT);

        List<Biralat> egyedBiralatList = new ArrayList<Biralat>();
        egyedBiralatList.add(selectedBiralat);
        selectedEgyed.setBiralatList(egyedBiralatList);

        FragmentTransaction ft = getFragmentTransactionWithTag("longClick");


        if (isWithin30Days(selectedEgyed.getELLDA())) {
            openReadonlyDialog();
        } else {
            Date lastBiralatDate = null;
            for (Biralat biralat : selectedEgyed.getBiralatList()) {
                if (biralat.getBIRDA() != null && biralat.getEXPORTALT() && (lastBiralatDate == null || lastBiralatDate.before(biralat.getBIRDA()))) {
                    lastBiralatDate = biralat.getBIRDA();
                }
            }
            if (isWithin30Days(lastBiralatDate)) {
                openReadonlyDialog();
            } else {
                if (selectedBiralat.getLETOLTOTT()) {
                    openReadonlyDialog();
                } else {
                    dialog = BiralatDialogEdit.newInstance(new BiralFragment.BiralFragmentContainer() {

                        @Override
                        public void onAkako(final String akako) {
                            if (akako.equals("3")) {
                                biralFragment.updateCurrentBiralatWithAkako(akako);
                            } else {
                                FragmentTransaction ft = getFragmentTransactionWithTag("biralando");
                                dialog2 = null;
                                dialog2 = BirBirAkakoDialog.newInstance(new BirBirAkakoDialog.BirBirAkakoDialogListener() {
                                    @Override
                                    public void onNoClicked() {
                                        biralFragment.updateCurrentBiralatWithAkako(null);
                                        dialog2.dismiss();
                                    }

                                    @Override
                                    public void onYesClicked() {
                                        biralFragment.updateCurrentBiralatWithAkako(akako);
                                        dialog2.dismiss();
                                    }
                                });
                                dialog2.show(ft, "biralando");
                            }
                        }

                        @Override
                        public void onBiralFragmentResume(BiralFragment biralFragment) {
                            BiralatDialogEditActivity.this.biralFragment = biralFragment;
                            biralFragment.updateFragmentWithEgyed(selectedEgyed);
                        }

                        @Override
                        public void selectBiralat(Biralat lastBiralat) {
                            selectedBiralat = lastBiralat;
                        }

                        @Override
                        public void openMegjegyzesDialog(String megjegyzes) {
                            FragmentTransaction ft = getFragmentTransactionWithTag("megjegyzes");
                            dialog2 = BirBirMegjegyzesDialog.newInstance(new BirBirMegjegyzesDialog.BirBirMegjegyzesListener() {
                                @Override
                                public void onBirBirBirBirMegjegyzesCancel(EditText et) {
                                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                                    dialog2.dismiss();
                                }

                                @Override
                                public void onBirBirBirBirMegjegyzesOk(EditText et, String newMegjegyzes) {
                                    final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                                    biralFragment.setMegjegyzes(newMegjegyzes);
                                    dialog2.dismiss();
                                }
                            }, megjegyzes);
                            dialog2.show(ft, "megjegyzes");
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show(ft, "longClick");
                }
            }
        }
    }

    private boolean isWithin30Days(Date date) {
        if (date != null && date.getTime() > 0) {
            Calendar cal = Calendar.getInstance();
            cal.roll(Calendar.DAY_OF_YEAR, -30);
            Date maxDate = cal.getTime();
            if (maxDate.before(date)) {
                return true;
            }
        }
        return false;
    }

    private void openReadonlyDialog() {
        List<Biralat> biralatList = new ArrayList<Biralat>();
        biralatList.add(selectedBiralat);

        FragmentTransaction ft = getFragmentTransactionWithTag("longClick");
        dialog = BiralatDialog.newInstance(biralatList);
        dialog.setCancelable(false);
        dialog.show(ft, "longClick");
    }

    public void onSaveBiralatClicked(View view) {
        if (!biralFragment.getBiralatStarted()) {
            dismissDialog();
            finish();
            return;
        }
        if (selectedEgyed != null) {
            Biralat biralat = new Biralat();
            if (selectedBiralat != null) {
                biralat.setId(selectedBiralat.getId());
            }
            biralat.setTENAZ(selectedEgyed.getTENAZ());
            biralat.setAZONO(selectedEgyed.getAZONO());
            biralat.setFELTOLTETLEN(true);
            biralat.setEXPORTALT(false);
            biralat.setLETOLTOTT(false);
            biralat.setMEGJEGYZES(biralFragment.getMegjegyzes());
            biralat.setORSKO(selectedEgyed.getORSKO());
            biralat.setKULAZ(app.getBiraloAzonosito());
            biralat.setBIRDA(new Date());
            biralat.setBIRTI(7);

            String akakoString = biralFragment.getAkako();
            Map<String, String> map = biralFragment.getKodErtMap();
            if (akakoString == null || akakoString.isEmpty() || akakoString.equals("3")) {
                if (map != null) {
                    String invalidErtAtKod = invalidErtAtKod();
                    if (invalidErtAtKod != null) {
                        SoundUtil.buttonBeep();
                        Toast.makeText(this, "Érvénytelen bírálat!", Toast.LENGTH_LONG).show();
                        biralFragment.forceEditing();
                        biralFragment.selectInputByKod(invalidErtAtKod);
                    } else {
                        if (akakoString != null && !akakoString.isEmpty() && akakoString.equals("3")) {
                            biralat.setAKAKO(Integer.valueOf(akakoString));
                        }
                        biralat.setKodErtMap(map);
                        saveBiralat(biralat);
                    }
                } else {
                    FragmentTransaction ft = getFragmentTransactionWithTag("unfinished");
                    dialog2 = BirBirUnfinishedBiralatDialog.newInstance(new BirBirUnfinishedBiralatListener() {
                        @Override
                        public void onBirBirUnfinishedBiralatCancel() {
                            dialog2.dismiss();
                        }

                        @Override
                        public void onBirBirUnfinishedBiralatOk() {
                            dialog2.dismiss();
                            dismissDialog();
                            finish();
                        }
                    });
                    dialog2.show(ft, "unfinished");
                }
            } else {
                Integer akakoInt = Integer.valueOf(akakoString);
                if (akakoInt > 0 && akakoInt < 6) {
                    biralat.setAKAKO(akakoInt);
                    saveBiralat(biralat);
                }
            }
        }
    }

    private String invalidErtAtKod() {
        return biralFragment.invalidErtAtKod();
    }

    public void saveBiralat(Biralat biralat) {
        app.updateBiralat(biralat);
        biralFragment.setBiralatSaved();
        dismissDialog();
        finish();
    }
}
