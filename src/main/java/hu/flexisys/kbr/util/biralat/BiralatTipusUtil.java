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

    public static BiralatTipus getBiralatTipusByBiralat(Biralat biralat) {

        List<String> rawSzempontList =
                Arrays.asList(biralat.getKOD01(), biralat.getKOD02(), biralat.getKOD03(), biralat.getKOD04(), biralat.getKOD05(), biralat.getKOD06(),
                        biralat.getKOD07(), biralat.getKOD08(), biralat.getKOD09(), biralat.getKOD10(), biralat.getKOD11(), biralat.getKOD12(),
                        biralat.getKOD13(), biralat.getKOD14(), biralat.getKOD15(), biralat.getKOD16(), biralat.getKOD17(), biralat.getKOD18(),
                        biralat.getKOD19(), biralat.getKOD20(), biralat.getKOD21(), biralat.getKOD22(), biralat.getKOD23(), biralat.getKOD24(),
                        biralat.getKOD25(), biralat.getKOD26(), biralat.getKOD27(), biralat.getKOD28(), biralat.getKOD29(), biralat.getKOD30());

        List<String> szempontList = new ArrayList<String>();
        for (String szempont : rawSzempontList) {
            if (szempont != null) { szempontList.add(szempont); }
        }

        String tipusKod = null;

        for (String key : biralatTipusMap.keySet()) {
            BiralatTipus biralatTipus = biralatTipusMap.get(key);

            if (biralatTipus.szempontList.size() == szempontList.size()) {
                tipusKod = biralatTipus.id;

                for (String kod : biralatTipus.szempontList) {
                    if (!szempontList.contains(kod)) {
                        tipusKod = null;
                        break;
                    }
                }

                if (tipusKod != null) {
                    break;
                }
            }
        }

        return getBiralatTipus(tipusKod);
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
