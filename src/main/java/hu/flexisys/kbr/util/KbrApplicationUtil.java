package hu.flexisys.kbr.util;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by peter on 31/08/14.
 */
public class KbrApplicationUtil {

    public static String TAG = "KBR2_KbrApplicationUtil";
    private static Context context;
    private static String biralatTipus;
    private static String biraloAzonosito;
    private static String biraloNev;
    private static String biraloUserName;

    public static void initKbrApplicationUtil(Context context) {
        KbrApplicationUtil.context = context;
    }

    private static void init() {
        try {
            Properties properties = PropertiesUtil.loadProperties(context, R.raw.init);
            biralatTipus = properties.getProperty("biralat_tipus");
            biraloAzonosito = properties.getProperty("biralo_azonosito");

            Properties biraloProperties = PropertiesUtil.loadProperties(context, R.raw.biralok);
            String biraloString = biraloProperties.getProperty(biraloAzonosito);
            String[] biraloValues = biraloString.split(",");
            biraloNev = biraloValues[0];
            biraloUserName = biraloValues[1];
        } catch (IOException e) {
            Log.e(TAG, "loadBiralatSzempontMap", e);
        }
    }

    public static String getBiraloNev() {
        if (biraloNev == null) {
            init();
        }
        return biraloNev;
    }

    public static String getBiraloAzonosito() {
        if (biraloAzonosito == null) {
            init();
        }
        return biraloAzonosito;
    }

    public static String getBiralatTipus() {
        if (biralatTipus == null) {
            init();
        }
        return biralatTipus;
    }

    public static String getBiraloUserName() {
        if (biraloUserName == null) {
            init();
        }
        return biraloUserName;
    }
}
