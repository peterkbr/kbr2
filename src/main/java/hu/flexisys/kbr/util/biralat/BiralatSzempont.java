package hu.flexisys.kbr.util.biralat;

/**
 * Created by peter on 28/07/14.
 */
public class BiralatSzempont {

    public String kod;
    public String rovidNev;
    public String nev;
    public String keszletStart;
    public String keszletEnd;
    //    public String kategoriaStart;
//    public String kategoriaMiddle;
//    public String kategoriaEnd;
    public Integer[] kategoriaBounds;

    public BiralatSzempont(String kod, String valuesString) {
        String[] values = valuesString.split(",");
        this.kod = kod;
        this.rovidNev = values[0];
        this.nev = values[1];
        this.keszletStart = values[2];
        this.keszletEnd = values[3];
        kategoriaBounds = new Integer[values.length - 4];
        for (int i = 4, j = 0; i < values.length; i++, j++) {
            kategoriaBounds[j] = Integer.parseInt(values[i]);
        }
//        this.kategoriaStart = values[4];
//        this.kategoriaMiddle = values[5];
//        this.kategoriaEnd = values[6];
    }
}
