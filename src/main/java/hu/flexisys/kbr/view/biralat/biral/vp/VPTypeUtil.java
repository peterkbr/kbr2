package hu.flexisys.kbr.view.biralat.biral.vp;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VPTypeUtil {

    private static Map<String, List<String>> szempontSzarmaztatasMap;

    public static List<String> getSzempontSzarmaztatasList(String szempontKod) {
        return szempontSzarmaztatasMap.get(szempontKod);
    }

    public static void initVPTypeUtil(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.biralat_szempontok_szarmaztatas);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        try {
            szempontSzarmaztatasMap = new HashMap<String, List<String>>();
            while ((line = buffreader.readLine()) != null) {
                String[] arr = line.split("=");
                String id = arr[0];
                String[] values = arr[1].split(",");
                List<String> list = Arrays.asList(values);
                szempontSzarmaztatasMap.put(id, list);

            }
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "loadBiralatSzempontSzarmaztatas", e);
        }
    }
}
