package hu.flexisys.kbr.util.biralat;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class BiralatTipusUtil {

    private static Map<String, BiralatTipus> biralatTipusMap;

    public static final String HUS_BIRALAT_TIPUS = "8";
    public static final String TEJ_BIRALAT_TIPUS = "9";

    public static String currentBiralatTipus = HUS_BIRALAT_TIPUS;

    public static String getBiralatTipusByBiralat(Biralat biralat) {
        String birti = String.valueOf(biralat.getBIRTI());
        if (Arrays.asList("6", "8").contains(birti)) {
            return HUS_BIRALAT_TIPUS;
        } else {
            return TEJ_BIRALAT_TIPUS;
        }
    }

    public static BiralatTipus getBiralatTipus(String biralatTipusId) {
        return biralatTipusMap.get(biralatTipusId);
    }

    public static void initBiralatTipusUtil(Context context) {
        loadBiralatTipusMap(context);
    }

    private static void loadBiralatTipusMap(Context context) {
        if (biralatTipusMap == null) {
            biralatTipusMap = new HashMap<String, BiralatTipus>();
            InputStream inputStream = context.getResources().openRawResource(R.raw.biralat_tipusok);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            try {
                while ((line = buffreader.readLine()) != null) {
                    String[] arr = line.split("=");
                    String id = arr[0];
                    String[] values = arr[1].split(";");

                    List<String> szempontList = new ArrayList<String>();
                    for (String string : values[0].split(",")) {
                        szempontList.add(string);
                    }

                    List<String> akakoList = new ArrayList<String>();
                    for (String string : values[1].split(",")) {
                        akakoList.add(string);
                    }

                    List<String> vpList = new ArrayList<String>();
                    for (String string : values[2].split(",")) {
                        vpList.add(string);
                    }
                    BiralatTipus biralatTipus = new BiralatTipus(id, szempontList, akakoList, vpList);
                    biralatTipusMap.put(id, biralatTipus);

                }
            } catch (IOException e) {
                Log.e(LogUtil.TAG, "loadBiralatSzempontMap", e);
            }
        }
    }
}
