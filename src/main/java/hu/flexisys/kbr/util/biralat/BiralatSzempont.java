package hu.flexisys.kbr.util.biralat;

/**
 * Created by peter on 28/07/14.
 */
public class BiralatSzempont {

    public String id;
    public String kod;
    public String nev;
    public String keszletStart;
    public String keszletEnd;
    public String kategoriaStart;
    public String kategoriaMiddle;
    public String kategoriaEnd;

    public BiralatSzempont(String id, String kod, String nev, String keszletStart, String keszletEnd, String kategoriaStart, String kategoriaMiddle,
                           String kategoriaEnd) {
        this.id = id;
        this.kod = kod;
        this.nev = nev;
        this.keszletStart = keszletStart;
        this.keszletEnd = keszletEnd;
        this.kategoriaStart = kategoriaStart;
        this.kategoriaMiddle = kategoriaMiddle;
        this.kategoriaEnd = kategoriaEnd;
    }

    public BiralatSzempont(String id, String valuesString) {
        String[] values = valuesString.split(",");
        this.id = id;
        this.kod = values[0];
        this.nev = values[1];
        this.keszletStart = values[2];
        this.keszletEnd = values[3];
        this.kategoriaStart = values[4];
        this.kategoriaMiddle = values[5];
        this.kategoriaEnd = values[6];
    }
}
