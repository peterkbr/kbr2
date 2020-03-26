package hu.flexisys.kbr.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileUtil {

    public static String externalAppPath;
    public static String innerAppPath;

    public static void initFileUtil(Context context) throws Exception {
        FileUtil.innerAppPath = context.getFilesDir().getAbsolutePath();
        String externalAppPath = createExternalAppDir(context);
        if (externalAppPath == null) {
            throw new Exception("External directory creation error.");
        }
        FileUtil.externalAppPath = externalAppPath;
    }

    private static String createExternalAppDir(Context context) throws Exception {
        externalAppPath = findExternalBaseDir(context);
        File dir = new File(externalAppPath);
        if (dir.mkdirs() || dir.exists()) {
            return externalAppPath;
        }

        String externalBaseDir = System.getenv("SECONDARY_STORAGE");
        externalAppPath = externalBaseDir + File.separator + "KBR2";
        dir = new File(externalAppPath);
        if (dir.mkdirs() || dir.exists()) {
            return externalAppPath;
        }

        externalBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        externalAppPath = externalBaseDir + File.separator + "KBR2";
        dir = new File(externalAppPath);
        if (!dir.mkdirs() && !dir.exists()) {
            throw new Exception("External directory creation error.");
        }

        return externalAppPath;
    }

    private static String findExternalBaseDir(Context context) {
        String externalBaseDir = null;
        for (File dir : context.getExternalFilesDirs(null)) {
            externalBaseDir = dir.getAbsolutePath();
            if (!externalBaseDir.contains("emulated")) {
                break;
            }
        }
        return externalBaseDir;
    }

    public static void copyFile(File src, File dst) throws IOException {
        try (FileChannel inChannel = new FileInputStream(src).getChannel();
             FileChannel outChannel = new FileOutputStream(dst).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
    }
}