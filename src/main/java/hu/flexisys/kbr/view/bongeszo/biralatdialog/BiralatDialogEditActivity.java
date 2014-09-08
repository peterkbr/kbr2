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

import java.util.Date;
import java.util.Map;

/**
 * Created by peter on 04/09/14.
 */
public class BiralatDialogEditActivity extends KbrActivity {

    public static String KEY_EGYED = "KEY_EGYED";

    public BiralFragment biralFragment;
    private KbrDialog dialog2;
    private Egyed selectedEgyed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.hide();

        selectedEgyed = (Egyed) getIntent().getExtras().getSerializable(KEY_EGYED);
        FragmentTransaction ft = getFragmentTransactionWithTag("longClick");
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
                            biralFragment.clearAkakoView();
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
        });
        dialog.show(ft, "longClick");
    }


    public void onSaveBiralatClicked(View view) {
        if (selectedEgyed != null && biralFragment.getBiralatStarted()) {
            Biralat biralat = new Biralat();
            biralat.setTENAZ(selectedEgyed.getTENAZ());
            biralat.setAZONO(selectedEgyed.getAZONO());
            biralat.setFELTOLTETLEN(true);
            biralat.setEXPORTALT(false);
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
                    dialog = BirBirUnfinishedBiralatDialog.newInstance(new BirBirUnfinishedBiralatListener() {
                        @Override
                        public void onBirBirUnfinishedBiralatCancel() {
                            dismissDialog();
                        }

                        @Override
                        public void onBirBirUnfinishedBiralatOk() {
                            biralFragment.clearCurrentBiralat();
                            actionBar.selectTab(actionBar.getTabAt(1));
                            dismissDialog();
                        }
                    });
                    dialog.show(ft, "unfinished");
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
        biralFragment.setBiralatSaved();
        app.updateBiralat(biralat);
        dismissDialog();
    }
}