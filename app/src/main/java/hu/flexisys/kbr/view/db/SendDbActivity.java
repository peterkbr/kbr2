package hu.flexisys.kbr.view.db;

import android.os.Bundle;
import android.util.Log;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;
import hu.flexisys.kbr.controller.emptytask.PreExecutable;
import hu.flexisys.kbr.util.*;
import hu.flexisys.kbr.view.KbrActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 27/09/14.
 */
public class SendDbActivity extends KbrActivity {

    public static String KEY_INNER_PATH = "KEY_INNER_PATH";
    public static String KEY_SDCARD_PATH = "KEY_SDCARD_PATH";
    private File innerErrorFile;
    private File sdcardErrorFile;

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    protected void resume() {
        EmptyTask task = new EmptyTask(new PreExecutable() {
            @Override
            public void preExecute() {
                startProgressDialog("Adatbázisok előkészítése");
            }
        }, new Executable() {
            @Override
            public void execute() throws Exception {
                try {
                    copyDbFiles();
                } catch (IOException e) {
                    Log.e(LogUtil.TAG, e.getMessage(), e);
                }
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                dismissDialog();
                sendDbEmail("[KBR2] Adatbázisok ");
                finish();
            }
        });
        startMyTask(task);
    }

    protected void copyDbFiles() throws IOException {
        String dirPath = FileUtil.externalAppPath + File.separator + "DataBase" + File.separator + "ErrorFiles";
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

    protected void sendDbEmail(String subject) {
        List<String> pathList = new ArrayList<String>();
        pathList.add(innerErrorFile.getAbsolutePath());
        pathList.add(sdcardErrorFile.getAbsolutePath());
        EmailUtil.sendMailWithAttachments(new String[]{KbrApplicationUtil.getSupportEmail()}, subject + app.getBiraloNev(), null, pathList);
    }
}
