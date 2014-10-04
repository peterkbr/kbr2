package hu.flexisys.kbr.util.biralat;

import android.content.Context;
import android.util.Log;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.LogUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 28/07/14.
 */
public class BiralatTipusUtil {

    private static Map<String, BiralatTipus> biralatTipusMap;

    public static void initBiralatTipusUtil(Context context) {
        loadBiralatTipusMap(context);
    }

    public static BiralatTipus getBiralatTipus(String biralatTipusId) {
        return biralatTipusMap.get(biralatTipusId);
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

                    List<String> vpFormulaList = new ArrayList<String>();
                    for (String string : values[2].split(",")) {
                        vpFormulaList.add(string);
                    }
                    BiralatTipus biralatTipus = new BiralatTipus(id, szempontList, akakoList, vpFormulaList);
                    biralatTipusMap.put(id, biralatTipus);

                }
            } catch (IOException e) {
                Log.e(LogUtil.TAG, "loadBiralatSzempontMap", e);
            }
        }
    }
}
