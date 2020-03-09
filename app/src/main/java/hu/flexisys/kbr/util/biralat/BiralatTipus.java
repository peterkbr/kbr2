package hu.flexisys.kbr.util.biralat;

import java.util.List;

/**
 * Created by peter on 28/07/14.
 */
public class BiralatTipus {

    public String id;
    public List<String> szempontList;
    public List<String> akakoList;
    public List<String> vpFormulaList;

    public BiralatTipus(String id, List<String> szempontList, List<String> akakoList, List<String> vpFormulaList) {
        this.id = id;
        this.szempontList = szempontList;
        this.akakoList = akakoList;
        this.vpFormulaList = vpFormulaList;
    }
}
