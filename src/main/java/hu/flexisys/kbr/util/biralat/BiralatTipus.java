package hu.flexisys.kbr.util.biralat;

import java.util.List;

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
