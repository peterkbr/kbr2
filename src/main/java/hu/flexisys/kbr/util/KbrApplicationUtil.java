package hu.flexisys.kbr.util;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by peter on 31/08/14.
 */
public class KbrApplicationUtil {

    public static String TAG = "KbrApplicationUtil";
    private static Context context;
    private static String biralatTipus;
    private static String biraloNev;
    private static String biraloAzonosito;
    private static String biraloUserId;

    public static void initKbrApplicationUtil(Context context) {
        KbrApplicationUtil.context = context;
    }

    private static void init() {
        InputStream inputStream = context.getResources().openRawResource(R.raw.init);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        try {
            while ((line = buffreader.readLine()) != null) {
                String[] arr = line.split("=");
                String key = arr[0];
                String value = arr[1];

                String biralat_tipus_key = "biralat_tipus";
                String biralo_azonosito_key = "biralo_azonosito";
                String biralo_nev_key = "biralo_nev";
                String biralo_userid_key = "biralo_userid";

                if (key.equals(biralat_tipus_key)) {
                    biralatTipus = value;
                } else if (key.equals(biralo_azonosito_key)) {
                    biraloAzonosito = value;
                } else if (key.equals(biralo_nev_key)) {
                    biraloNev = value;
                } else if (key.equals(biralo_userid_key)) {
                    biraloUserId = value;
                }
            }
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

    public static String getBiraloUserId() {
        if (biraloUserId == null) {
            init();
        }
        return biraloUserId;
    }
}
