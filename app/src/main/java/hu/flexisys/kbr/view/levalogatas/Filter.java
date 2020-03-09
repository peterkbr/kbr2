package hu.flexisys.kbr.view.levalogatas;

import java.util.HashMap;

/**
 * Created by Peter on 2014.07.22..
 */
public class Filter {

    public static final String MAR_ELLETT = "MAR_ELLETT";
    public static final String NEM_BIRALT = "NEM_BIRALT";
    public static final String ITV = "ITV";
    public static final String KIVALASZTOTTAK = "KIVALASZTOTTAK";
    public static final String ELLES_SORSZAMAI = "ELLES_SORSZAMAI";
    public static final String HU = "HU";
    public static final String ENAR = "ENAR";
    public static final String UTOLSO_ELLES_TOL = "UTOLSO_ELLES_TOL";
    public static final String UTOLSO_ELLES_IG = "UTOLSO_ELLES_IG";
    public static final String SZULETES_TOL = "SZULETES_TOL";
    public static final String SZULETES_IG = "SZULETES_IG";
    public static final String KONSTRUKCIOS_TOL = "KONSTRUKCIOS_TOL";
    public static final String KONSTRUKCIOS_IG = "KONSTRUKCIOS_IG";

    public HashMap<String, Object> filterMap;

    public Filter() {
        filterMap = new HashMap<String, Object>();
    }

    public Object get(String key) {
        return filterMap.get(key);
    }

    public void put(String key, Object value) {
        filterMap.put(key, value);
    }

    public void clear(){
        filterMap.clear();
    }

}
