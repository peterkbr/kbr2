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


    private static final String TAG = "KBR2_LogUtil";
    private static Boolean exportLog = false;
    private static String logFilePath = null;
    private static Boolean sendEmail = true;

    private static KbrApplication app;

    public static void initLogUtil(KbrApplication app) {
        LogUtil.app = app;
    }

    public static void startLog() {
        EmptyTask exportLogTask = new EmptyTask(new Executable() {
            @Override
            public void execute() throws Exception {
                try {
                    String dirPath = FileUtil.getExternalAppPath() + File.separator + "LOG";
                    File dir = new File(dirPath);
                    dir.mkdirs();
                    if (logFilePath == null || sendEmail) {
                        logFilePath = dirPath + File.separator + "KBRLog_" + DateUtil.getRequestId() + ".txt";
                    }

                    File logFile = new File(logFilePath);
                    if (!logFile.exists()) {
                        logFile.createNewFile();
                    }

                    exportLog = false;
                    sendEmail = true;

                    Process process = Runtime.getRuntime().exec("logcat -v long");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                    StringBuilder log = new StringBuilder();
                    String line;
                    int i = 0;
                    while ((line = bufferedReader.readLine()) != null && !exportLog) {
                        if (line.startsWith("[ ")) {
                            log.append("\n");
                        }
                        log.append(line);
                        if (i > 300) {
                            exportLog = true;
                            sendEmail = false;
                            i = 0;
                        } else {
                            i++;
                        }
                    }

                    FileOutputStream outStream = new FileOutputStream(logFile, true);
                    byte[] buffer = log.toString().getBytes();
                    outStream.write(buffer);
                    outStream.close();
                } catch (Exception e) {
                    Log.e(TAG, "exportLog", e);
                    sendEmail = false;
                }
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                List<String> pathList = new ArrayList<String>();
                pathList.add(logFilePath);
                if (sendEmail) {
                    EmailUtil.sendMailWithAttachments(new String[]{KbrApplicationUtil.getSupportEmail()}, "[KBR2][LOG] " + app.getBiraloNev(), null, pathList);
                }
                startLog();
            }
        });

        app.startMyTask(exportLogTask);
    }

    public static void exportLog() {
        exportLog = true;
        sendEmail = true;
    }

}
