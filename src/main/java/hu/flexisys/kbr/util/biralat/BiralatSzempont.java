package hu.flexisys.kbr.util.biralat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BiralatSzempont {

    public String kod;
    public String rovidNev;
    public String nev;
    public String keszletStart;
    public String keszletEnd;
    public List<String> keszletExtensions;
    public Integer[] kategoriaBounds;

    public BiralatSzempont(String kod, String valuesString) {
        String[] values = valuesString.split(",");
        this.kod = kod;
        this.rovidNev = values[0];
        this.nev = values[1];

        String[] keszletArray = values[2].split(":");

        this.keszletStart = keszletArray[0];
        this.keszletEnd = keszletArray[1];
        this.keszletExtensions = new ArrayList<String>();

        if (keszletArray.length>2) {
            keszletExtensions = Arrays.asList(keszletArray);
        }

        kategoriaBounds = new Integer[values.length - 3];
        for (int i = 3, j = 0; i < values.length; i++, j++) {
            kategoriaBounds[j] = Integer.parseInt(values[i]);
        }
    }
}
