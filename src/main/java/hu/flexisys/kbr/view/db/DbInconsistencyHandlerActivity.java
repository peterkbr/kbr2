package hu.flexisys.kbr.view.db;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.io.IOException;

/**
 * Created by peter on 06/09/14.
 */
public class DbInconsistencyHandlerActivity extends SendDbActivity {

    private static final String TAG = "KBR2_DbInconsistencyHandlerActivity";

    @Override
    protected void resume() {
        openFirstDialog();
    }

    private void openFirstDialog() {
        FragmentTransaction ft = getFragmentTransactionWithTag("db1");
        dialog = DbInconsistencyDialog.newInstance(new DbInconsistencyDialog.DbInconsistenyListener() {
            @Override
            public void handleDbIconsistency(boolean inner) {
                app.synchronizeDb(inner);
                dismissDialog();
                openSecondDialog();
            }
        });
        dialog.show(ft, "db1");
    }

    private void openSecondDialog() {
        FragmentTransaction ft = getFragmentTransactionWithTag("db2");
        dialog = DbInconsistencyEmailDialog.newInstance(new DbInconsistencyEmailDialog.DbInconsistenyEmailListener() {
            @Override
            public void handleDbIconsistency() {
                try {
                    copyDbFiles();
                    sendDbEmail("[KBR2][ERROR][DB inkonzisztencia] ");
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                dismissDialog();
                finish();
            }
        });
        dialog.show(ft, "db2");
    }

}
