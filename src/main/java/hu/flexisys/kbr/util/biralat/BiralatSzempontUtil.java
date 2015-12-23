package hu.flexisys.kbr.util.biralat;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class BiralatSzempontUtil {

    private static Map<String, BiralatSzempont> biralatSzempontMap;

    public static void initBiralatSzempontUtil(Context context) {
        loadBiralatSzempontMap(context);
    }

    public static BiralatSzempont getBiralatSzempont(String biralatSzempontId) {
        return biralatSzempontMap.get(biralatSzempontId);
    }

    private static void loadBiralatSzempontMap(Context context) {
        if (biralatSzempontMap == null) {
            biralatSzempontMap = new HashMap<String, BiralatSzempont>();
            InputStream inputStream = context.getResources().openRawResource(R.raw.biralat_szempontok);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            try {
                while ((line = buffreader.readLine()) != null) {
                    String[] arr = line.split("=");
                    String id = arr[0];
                    String values = arr[1];
                    BiralatSzempont szempont = new BiralatSzempont(id, values);
                    biralatSzempontMap.put(id, szempont);
                }
            } catch (IOException e) {
                Log.e(LogUtil.TAG, "loadBiralatSzempontMap", e);
            }
        }
    }
}
