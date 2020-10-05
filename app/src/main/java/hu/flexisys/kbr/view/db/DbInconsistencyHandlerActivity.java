package hu.flexisys.kbr.view.db;

import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import hu.flexisys.kbr.util.LogUtil;

import java.io.IOException;

/**
 * Created by peter on 06/09/14.
 */
public class DbInconsistencyHandlerActivity extends SendDbActivity {

    @Override
    protected void resume() {
        openSecondDialog();
    }

    private void openSecondDialog() {
        FragmentTransaction ft = getFragmentTransactionWithTag("db2");
        dialog = DbInconsistencyEmailDialog.newInstance(new DbInconsistencyEmailDialog.DbInconsistenyEmailListener() {
            @Override
            public void handleDbIconsistency() {
                try {
                    copyDbFiles();
                } catch (IOException e) {
                    Log.e(LogUtil.TAG, e.getMessage(), e);
                }
                app.synchronizeDb();
                sendDbEmail("[KBR2][ERROR][DB inkonzisztencia] ");
                dismissDialog();
                finish();
            }
        });
        dialog.show(ft, "db2");
    }

}
