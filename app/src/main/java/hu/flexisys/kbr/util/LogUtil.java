package hu.flexisys.kbr.util;

import android.util.Log;

import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogUtil {

    public static final String TAG = "KBR2";

    private static String logFilePath = null;
    private static Boolean stop = false;
    private static Boolean exportLog = false;
    private static Boolean sendEmail = false;

    private static KbrApplication app;
    private static Process process;
    private static String separator;

    public static void initLogUtil(KbrApplication app) {
        LogUtil.app = app;
        try {
            process = Runtime.getRuntime().exec(new String[]{"logcat", "-v", "long",
                    "AndroidRuntime:E " + TAG + ":V *:S"});
        } catch (IOException e) {
            Log.e(TAG, "init log process", e);
        }
        separator = System.getProperty("line.separator");
    }

    public static void startLog() {
        EmptyTask exportLogTask = new EmptyTask(new Executable() {
            @Override
            public void execute() throws Exception {
                try {
                    exportLog = false;
                    sendEmail = false;

                    StringBuilder log = new StringBuilder();
                    if (logFilePath == null) {
                        String dirPath = FileUtil.externalAppPath + File.separator + "LOG";
                        File dir = new File(dirPath);
                        dir.mkdirs();
                        logFilePath = dirPath + File.separator + "KBRLog_" + DateUtil.getRequestId() + ".txt";
                        log.append("KBR2 log started : ").append(DateUtil.formatTimestampFileName(new Date()));
                    }

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    int i = 0;
                    while ((line = bufferedReader.readLine()) != null && !exportLog) {
                        log.append(separator).append(line);
                        if (i > 300) {
                            exportLog = true;
                            sendEmail = false;
                            i = 0;
                        } else {
                            i++;
                        }
                    }

                    if (exportLog && sendEmail) {
                        log.append(separator).append("KBR2 log exported : ").append(DateUtil.
                                formatTimestampFileName(new Date()));
                    }

                    File logFile = new File(logFilePath);
                    if (!logFile.exists()) {
                        logFile.getParentFile().mkdirs();
                         logFile.createNewFile();
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
                if (stop) {
                    return;
                }
                List<String> pathList = new ArrayList<>();
                pathList.add(logFilePath);
                if (sendEmail) {
                    EmailUtil.sendMailWithAttachments(new String[]{KbrApplicationUtil.getSupportEmail()},
                            "[KBR2][LOG] " + app.getBiraloNev(), null, pathList);
                    logFilePath = null;
                }
                startLog();
            }
        });

        app.startMyTask(exportLogTask);
    }

    public static void stopLog() {
        process.destroy();
        stop = true;
    }

    public static void exportLog() {
        Log.i(TAG, "--- export started:" + DateUtil.formatTimestampFileName(new Date()));
        exportLog = true;
        sendEmail = true;
    }

}
