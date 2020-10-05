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
        openFirstDialog();
    }

    private void openFirstDialog() {
        FragmentTransaction ft = getFragmentTransactionWithTag("db1");
        dialog = DbInconsistencyDialog.newInstance(new DbInconsistencyDialog.DbInconsistenyListener() {
            @Override
            public void handleDbIconsistency() {
                try {
                    copyDbFiles();
                } catch (IOException e) {
                    Log.e(LogUtil.TAG, e.getMessage(), e);
                }
                app.synchronizeDb();
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
                sendDbEmail("[KBR2][ERROR][DB inkonzisztencia] ");
                dismissDialog();
                finish();
            }
        });
        dialog.show(ft, "db2");
    }

}
