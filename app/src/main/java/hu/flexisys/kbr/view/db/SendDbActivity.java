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

public class SendDbActivity extends KbrActivity {

    public static String KEY_INNER_PATH = "KEY_INNER_PATH";
    public static String KEY_SDCARD_PATH = "KEY_SDCARD_PATH";
    private File errorZip;

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
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }
        List<String> paths = new ArrayList<>();
        paths.addAll(extras.getStringArrayList(KEY_INNER_PATH));
        paths.addAll(extras.getStringArrayList(KEY_SDCARD_PATH));
        String time = DateUtil.formatTimestampFileName(new Date());

        String zipPath = FileUtil.innerAppPath + File.separator + "DataBase" + File.separator + "ErrorFiles" + File.separator + time + "_" + app.getBiraloUserId() + ".zip";
        errorZip = new File(zipPath);
        errorZip.getParentFile().mkdirs();
        ZipManager.zip(paths.toArray(new String[0]), errorZip.getAbsolutePath());

        String externalZipPath = FileUtil.externalAppPath + File.separator + "DataBase" + File.separator + "ErrorFiles" + File.separator + time + "_" + app.getBiraloUserId() + ".zip";
        File externalZip = new File(externalZipPath);
        externalZip.mkdirs();
        FileUtil.copyFile(errorZip, externalZip);
    }

    protected void sendDbEmail(String subject) {
        List<String> pathList = new ArrayList<>();
        pathList.add(errorZip.getAbsolutePath());
        EmailUtil.sendMailWithAttachments(new String[]{KbrApplicationUtil.getSupportEmail()}, subject + app.getBiraloNev(), null, pathList);
    }
}