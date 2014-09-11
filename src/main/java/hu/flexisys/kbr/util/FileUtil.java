package hu.flexisys.kbr.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by peter on 06/09/14.
 */
public class FileUtil {

    private static Context context;
    private static String extarnalAppPath;

    public static void initFileUtil(Context context) {
        FileUtil.context = context;
        String externalBaseDir = System.getenv("SECONDARY_STORAGE");
        if (externalBaseDir == null || externalBaseDir.isEmpty() || externalBaseDir.equals("null")) {
            externalBaseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        extarnalAppPath = externalBaseDir + File.separator + "KBR2";
    }

    public static String getExternalAppPath() {
        return extarnalAppPath;
    }

    public static String getInnerAppPath() {
        return context.getFilesDir().getAbsolutePath();
    }

    public static void copyFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }
}
