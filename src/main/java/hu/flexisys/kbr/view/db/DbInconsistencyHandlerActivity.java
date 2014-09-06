package hu.flexisys.kbr.view.db;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.EmailUtil;
import hu.flexisys.kbr.util.FileUtil;
import hu.flexisys.kbr.view.KbrActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 06/09/14.
 */
public class DbInconsistencyHandlerActivity extends KbrActivity {

    private static final String TAG = "DbInconsistencyHandlerActivity";
    public static String KEY_INNER_PATH = "KEY_INNER_PATH";
    public static String KEY_SDCARD_PATH = "KEY_SDCARD_PATH";
    private File innerErrorFile;
    private File sdcardErrorFile;

    @Override
    protected void onResume() {
        super.onResume();
        actionBar.hide();
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
                    sendDbInconsistencyEmail();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                dismissDialog();
                finish();
            }
        });
        dialog.show(ft, "db2");
    }

    private void copyDbFiles() throws IOException {
        String dirPath = Environment.getExternalStorageDirectory() + File.separator + "KBR2" + File.separator + "DataBase" + File.separator + "ErrorFiles";
        File dir = new File(dirPath);
        dir.mkdirs();

        Bundle extras = getIntent().getExtras();
        String innerPath = extras.getString(KEY_INNER_PATH);
        String sdCardPath = extras.getString(KEY_SDCARD_PATH);

        String time = DateUtil.formatTimestampFileName(new Date());
        File innerOrig = new File(innerPath);
        innerErrorFile = new File(dirPath + File.separator + time + "_" + app.getBiraloUserId() + "_inner");
        FileUtil.copyFile(innerOrig, innerErrorFile);

        File sdcardOrig = new File(sdCardPath);
        sdcardErrorFile = new File(dirPath + File.separator + time + "_" + app.getBiraloUserId() + "_sdCard");
        FileUtil.copyFile(sdcardOrig, sdcardErrorFile);
    }

    private void sendDbInconsistencyEmail() {
        List<String> pathList = new ArrayList<String>();
        pathList.add(innerErrorFile.getAbsolutePath());
        pathList.add(sdcardErrorFile.getAbsolutePath());
        EmailUtil.sendMailWithAttachments(new String[]{"info@flexisys.hu"}, "[KBR2][ERROR][DB inkonzisztencia] " + app.getBiraloNev(), null, pathList);
    }
}
