package hu.flexisys.kbr.view.biralat.biral.vp;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class VPTypeUtil {

    private static Map<String, List<String>> szempontSzarmaztatasMap;
    private static List<String> editables;

    public static List<String> getSzempontSzarmaztatasList(String szempontKod) {
        return szempontSzarmaztatasMap.get(szempontKod);
    }

    public static boolean isSzarmaztatottSzempontEditable(String kod) {
        return editables.contains(kod);
    }

    public static void initVPTypeUtil(Context context) {
        InputStream inputStream = context.getResources().openRawResource(R.raw.biralat_szempontok_szarmaztatas);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        try {
            szempontSzarmaztatasMap = new HashMap<String, List<String>>();
            editables = new ArrayList<String>();
            while ((line = buffreader.readLine()) != null) {
                String[] arr = line.split("=");
                String kod = arr[0];

                String[] parts = arr[1].split(";");
                String[] values = parts[0].split(",");
                List<String> list = Arrays.asList(values);
                szempontSzarmaztatasMap.put(kod, list);
                if (parts.length > 1 && "E".equals(parts[1])) {
                    editables.add(kod);
                }
            }
        } catch (IOException e) {
            Log.e(LogUtil.TAG, "loadBiralatSzempontSzarmaztatas", e);
        }
    }
}
