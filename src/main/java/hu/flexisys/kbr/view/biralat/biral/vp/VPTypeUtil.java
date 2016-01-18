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

    private static Integer STAT_INDEX_MEAN = 1;
    private static Integer STAT_INDEX_STDDEV = 2;

    private static Map<String, String> namesMap;
    private static Map<String, List<Double>> converterMap;
    private static Map<String, List<Double>> statMap;
    private static Map<Integer, List<Double>> euterItervalMap;
    private static Map<Integer, List<Double>> fundItervalMap;

    private static Map<String, List<String>> szempontSzarmaztatasMap;
    private static List<String> editables;

    public static List<String> getSzempontSzarmaztatasList(String szempontKod) {
        return szempontSzarmaztatasMap.get(szempontKod);
    }

    public static boolean isSzarmaztatottSzempontEditable(String kod) {
        return editables.contains(kod);
    }

    public static Double getConvertedParamByName(String name, Integer rawValue) {
        List<Double> list = converterMap.get(name);
        Double convertedParam = list.get(rawValue);
        return convertedParam;
    }

    public static String getParamNameByKod(String kod) {
        return namesMap.get(kod);
    }

    public static Double getStatValueByNameFromConvertedValue(String name, Double convertedValue) {
        List<Double> statList = statMap.get(name);

        Double paramMean = statList.get(STAT_INDEX_MEAN);
        Double paramStdDev = statList.get(STAT_INDEX_STDDEV);

        Double statValue = (convertedValue - paramMean) / paramStdDev;
        return statValue;
    }

    private static String VP_NAME_EUTER = "EUTER";
    private static String VP_NAME_FUND = "FUND";

    public static Integer getIntervalValueForEuter(Double evaluatedValue) {
        return getIntervalValue(VP_NAME_EUTER, evaluatedValue);
    }

    public static Integer getIntervalValueForFund(Double evaluatedValue) {
        return getIntervalValue(VP_NAME_FUND, evaluatedValue);
    }

    private static Integer getIntervalValue(String vpName, Double evaluatedValue) {
        Map<Integer, List<Double>> intervalMap;
        if (VP_NAME_EUTER.equals(vpName)) {
            intervalMap = euterItervalMap;
        } else if (VP_NAME_FUND.equals(vpName)) {
            intervalMap = fundItervalMap;
        } else {
            return null;
        }
        for (Integer key : intervalMap.keySet()) {
            List<Double> interval = intervalMap.get(key);
            if (evaluatedValue >= interval.get(0) && evaluatedValue < interval.get(1)) {
                return key;
            }
        }
        return null;
    }

    public static void initVPTypeUtil(Context context) {
        loadBiralatSzempontSzarmaztatas(context);
        loadNames(context);
        loadConverterMap(context);
        loadStatMap(context);
        loadEuterIntervalMap(context);
        loadFundIntervalMap(context);
    }

    private static void loadBiralatSzempontSzarmaztatas(Context context) {
        BufferedReader buffreader = loadProperties(context, R.raw.biralat_szempontok_szarmaztatas);
        szempontSzarmaztatasMap = new HashMap<String, List<String>>();
        editables = new ArrayList<String>();
        parsePropertiesBufferedReader(buffreader, new PropertiesLineParser() {
            public void parsePropertiesLine(String kod, String partsString) {
                String[] parts = partsString.split(";");
                String[] values = parts[0].split(",");
                List<String> list = Arrays.asList(values);
                szempontSzarmaztatasMap.put(kod, list);
                if (parts.length > 1 && "E".equals(parts[1])) {
                    editables.add(kod);
                }
            }
        }, "loadBiralatSzempontSzarmaztatas");
    }

    private static void loadNames(Context context) {
        BufferedReader buffreader = loadProperties(context, R.raw.vp_0_names);
        namesMap = new HashMap<String, String>();
        parsePropertiesBufferedReader(buffreader, new PropertiesLineParser() {
            public void parsePropertiesLine(String kod, String value) {
                namesMap.put(kod, value);
            }
        }, "loadNamesMap");
    }

    private static void loadConverterMap(Context context) {
        BufferedReader buffreader = loadProperties(context, R.raw.vp_1_convert);
        converterMap = new HashMap<String, List<Double>>();
        parsePropertiesBufferedReader(buffreader, new PropertiesLineParser() {
            public void parsePropertiesLine(String kod, String value) {
                List<Double> converterValues = new ArrayList<Double>();
                String[] values = value.split(",");
                for (String converterValueString : values) {
                    Double converterValue = Double.valueOf(converterValueString);
                    converterValues.add(converterValue);
                }
                converterMap.put(kod, converterValues);
            }
        }, "loadConverterMap");
    }

    private static void loadStatMap(Context context) {
        BufferedReader buffreader = loadProperties(context, R.raw.vp_2_stat);
        statMap = new HashMap<String, List<Double>>();
        parsePropertiesBufferedReader(buffreader, new PropertiesLineParser() {
            public void parsePropertiesLine(String kod, String value) {
                List<Double> statValues = new ArrayList<Double>();
                String[] values = value.split(",");
                for (String statValueString : values) {
                    Double statValue = Double.valueOf(statValueString);
                    statValues.add(statValue);
                }
                statMap.put(kod, statValues);
            }
        }, "loadStatMap");
    }

    private static void loadEuterIntervalMap(Context context) {
        BufferedReader buffreader = loadProperties(context, R.raw.vp_3_euter_interval);
        euterItervalMap = new HashMap<Integer, List<Double>>();
        parsePropertiesBufferedReader(buffreader, new PropertiesLineParser() {
            public void parsePropertiesLine(String value, String intervalBounds) {
                String[] values = intervalBounds.split(",");
                List<Double> euterIntervalValues = Arrays.asList(Double.valueOf(values[0]), Double.valueOf(values[1]));
                euterItervalMap.put(Integer.valueOf(value), euterIntervalValues);
            }
        }, "loadEuterIntervalMap");
    }

    private static void loadFundIntervalMap(Context context) {
        BufferedReader buffreader = loadProperties(context, R.raw.vp_4_fund_interval);
        fundItervalMap = new HashMap<Integer, List<Double>>();
        parsePropertiesBufferedReader(buffreader, new PropertiesLineParser() {
            public void parsePropertiesLine(String value, String intervalBounds) {
                String[] values = intervalBounds.split(",");
                List<Double> fundIntervalValues = Arrays.asList(Double.valueOf(values[0]), Double.valueOf(values[1]));
                fundItervalMap.put(Integer.valueOf(value), fundIntervalValues);
            }
        }, "loadFundIntervalMap");
    }

    private static BufferedReader loadProperties(Context context, int resId) {
        InputStream inputStream = context.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        return buffreader;
    }

    private static void parsePropertiesBufferedReader(BufferedReader bufferedReader, PropertiesLineParser lineParser, String errorMessage) {
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                String[] arr = line.split("=");
                lineParser.parsePropertiesLine(arr[0], arr[1]);
            }
        } catch (IOException e) {
            Log.e(LogUtil.TAG, errorMessage, e);
        }
    }
}
