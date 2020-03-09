package hu.flexisys.kbr.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.KbrApplication;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by peter on 31/08/14.
 */
public class KbrApplicationUtil {

    private static KbrApplication context;
    private static String biralatTipus;
    private static String serverUrl;
    private static String supportEmail;
    private static String biraloAzonosito;
    private static String biraloNev;
    private static String biraloUserName;
    private static String testName;

    private static String SHARED_PREF_KEY = "KBR2_SHARED_PREF_KEY";
    private static String KEY_BIRALAT_TIPUS = "KEY_BIRALAT_TIPUS";
    private static String KEY_USER_ID = "KEY_USER_ID";
    private static String KEY_URL = "KEY_URL";
    private static String KEY_EMAIL = "KEY_EMAIL";

    private static void getData() {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        biralatTipus = sharedPref.getString(KEY_BIRALAT_TIPUS, context.getString(R.string.biralat_tipus));
        biraloAzonosito = sharedPref.getString(KEY_USER_ID, null);
        serverUrl = sharedPref.getString(KEY_URL, context.getString(R.string.server_url));
        supportEmail = sharedPref.getString(KEY_EMAIL, context.getString(R.string.support_email));
    }

    private static void setData() {
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(KEY_BIRALAT_TIPUS, biralatTipus);
        editor.putString(KEY_USER_ID, biraloAzonosito);
        editor.putString(KEY_URL, serverUrl);
        editor.putString(KEY_EMAIL, supportEmail);
        editor.commit();
    }

    public static void initKbrApplicationUtil(KbrApplication context) {
        KbrApplicationUtil.context = context;
    }

    public static void init() {
        try {
            getData();
            if (biraloAzonosito != null) {
                Properties biraloProperties = PropertiesUtil.loadProperties(context, R.raw.biralok);
                String biraloString = biraloProperties.getProperty(biraloAzonosito);
                String[] biraloValues = biraloString.split(",");
                biraloNev = biraloValues[0];
                biraloUserName = biraloValues[1];
            }
            String productionUrl = context.getString(R.string.production_url);
            if (productionUrl.equals(serverUrl)) {
                testName = null;
            } else {
                testName = context.getString(R.string.app_test_name);
                // TODO kell ez élesben is? szerintem nem :)
                biraloUserName="tst.teszt";
            }
            setData();
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "loadBiralatSzempontMap", e);
        }
    }


    public static void saveData(String _biraloAzonosito, String _serverUrl, String _supportEmail) {
        biraloAzonosito = _biraloAzonosito;
        serverUrl = _serverUrl;
        supportEmail = _supportEmail;
        setData();
        init();
        context.init();
    }

    public static String getServerUrl() {
        if (serverUrl == null) {
            init();
        }
        return serverUrl;
    }

    public static String getTestName() {
        if (serverUrl == null) {
            init();
        }
        return testName;
    }

    public static String getSupportEmail() {
        if (supportEmail == null) {
            init();
        }
        return supportEmail;
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

    public static boolean initialized() {
        if (biraloAzonosito == null || supportEmail == null || serverUrl == null) {
            init();
        }
        if (biraloAzonosito != null && !biraloAzonosito.isEmpty() && serverUrl != null && !serverUrl.isEmpty() && supportEmail != null &&
                !supportEmail.isEmpty()) {
            return true;
        }
        return false;
    }
}
