package hu.flexisys.kbr.util.biralat;

import java.util.List;

public class BiralatTipus {

    public String id;
    public List<String> szempontList;
    public List<String> akakoList;
    public List<String> vpList;

    public BiralatTipus(String id, List<String> szempontList, List<String> akakoList, List<String> vpList) {
        this.id = id;
        this.szempontList = szempontList;
        this.akakoList = akakoList;
        this.vpList = vpList;
    }
}
