package hu.flexisys.kbr.view.bongeszo.biralatdialog;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.KbrDialog;
import hu.flexisys.kbr.view.biralat.biral.BirBirAkakoDialog;
import hu.flexisys.kbr.view.biralat.biral.BirBirUnfinishedBiralatDialog;
import hu.flexisys.kbr.view.biralat.biral.BirBirUnfinishedBiralatListener;
import hu.flexisys.kbr.view.biralat.biral.BiralFragment;
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
        actionBar.hide();

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
            biralat.setId(selectedBiralat.getId());
            biralat.setTENAZ(selectedEgyed.getTENAZ());
            biralat.setAZONO(selectedEgyed.getAZONO());
            biralat.setFELTOLTETLEN(true);
            biralat.setEXPORTALT(false);
            biralat.setLETOLTOTT(false);
            biralat.setORSKO(selectedEgyed.getORSKO());
            biralat.setKULAZ(app.getBiraloAzonosito());
            biralat.setBIRDA(new Date());
            biralat.setBIRTI(7);

            String akakoString = biralFragment.getAkako();
            Map<String, String> map = biralFragment.getKodErtMap();
            if (akakoString == null || akakoString.isEmpty() || akakoString.equals("3")) {
                if (map != null) {
                    if (akakoString != null && !akakoString.isEmpty() && akakoString.equals("3")) {
                        biralat.setAKAKO(Integer.valueOf(akakoString));
                    }
                    biralat.setKodErtMap(map);
                    saveBiralat(biralat);
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

    public void saveBiralat(Biralat biralat) {
        app.updateBiralat(biralat);
        biralFragment.setBiralatSaved();
        dismissDialog();
        finish();
    }
}
