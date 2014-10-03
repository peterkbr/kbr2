package hu.flexisys.kbr.util;

import android.util.Log;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 01/10/14.
 */
public class LogUtil {


    public static final String TAG = "KBR2";
    private static String logFilePath = null;

    private static KbrApplication app;

    public static void initLogUtil(KbrApplication app) {
        LogUtil.app = app;
    }

    public static void startLog() {
        EmptyTask exportLogTask = new EmptyTask(new Executable() {
            @Override
            public void execute() throws Exception {
                try {

                    if (logFilePath == null) {
                        String dirPath = FileUtil.getExternalAppPath() + File.separator + "LOG";
                        File dir = new File(dirPath);
                        dir.mkdirs();
                        logFilePath = dirPath + File.separator + "KBRLog_" + DateUtil.getRequestId() + ".txt";
                    }

                    Process process = Runtime.getRuntime().exec(new String[]{"logcat", "-d", "AndroidRuntime:E " + TAG + ":V *:S"});
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    StringBuilder log = new StringBuilder();
                    String line;
                    String separator = System.getProperty("line.separator");
                    while ((line = bufferedReader.readLine()) != null) {
                        log.append(line).append(separator);
                    }

                    File logFile = new File(logFilePath);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }
                    FileOutputStream outStream = new FileOutputStream(logFile, true);
                    byte[] buffer = log.toString().getBytes();
                    outStream.write(buffer);
                    outStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "exportLog", e);
                }
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                List<String> pathList = new ArrayList<String>();
                pathList.add(logFilePath);
                EmailUtil.sendMailWithAttachments(new String[]{KbrApplicationUtil.getSupportEmail()}, "[KBR2][LOG] " + app.getBiraloNev(), null, pathList);
            }
        });
        app.startMyTask(exportLogTask);
    }

}
