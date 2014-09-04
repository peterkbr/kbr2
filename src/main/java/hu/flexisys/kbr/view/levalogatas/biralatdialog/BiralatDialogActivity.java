package hu.flexisys.kbr.view.levalogatas.biralatdialog;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.view.KbrActivity;

import java.util.List;

/**
 * Created by peter on 04/09/14.
 */
public class BiralatDialogActivity extends KbrActivity {

    public static String KEY_AZONO = "KEY_AZONO";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String azono = getIntent().getExtras().getString(KEY_AZONO);
        List<Biralat> biralatList = app.getBiralatByAZONO(azono);
        FragmentTransaction ft = getFragmentTransactionWithTag("longClick");
        dialog = BiralatDialog.newInstance(biralatList);
        dialog.show(ft, "longClick");
    }
}
