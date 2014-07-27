package hu.flexisys.kbr.util;

import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;

import java.util.List;

/**
 * Created by Peter on 2014.07.02..
 */
public class NetworkUtil {

    public static String SERVICE_URL = "http://tomcat7.devapp01.si.hu/EnarPdaServer/sendxml";

    // <?xml version="1.0" encoding="utf-8"?> <request type="kullemteny"
    // id="201105193"
    // userid="jakablk"
    // ><body><teny
    // tenaz="468893"
    // /></body></request>
    public static String getKullemtenyRequestBody(String userid, String tenaz) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<request type=\"kullemteny\" ");
        requestBuilder.append("id=\"").append(DateUtil.getRequestId()).append("\" ");
        requestBuilder.append("userid=\"").append(userid).append("\" ");
        requestBuilder.append("><body><teny ");
        requestBuilder.append(" tenaz=\"").append(tenaz).append("\" ");
        requestBuilder.append("/></body></request>");
        return requestBuilder.toString();
    }

    public static String getKullembirRequestBody(String userid, List<Egyed> egyedList) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<request type=\"kullembir\" ");
        requestBuilder.append("id=\"").append(DateUtil.getRequestId()).append("\" ");
        requestBuilder.append("userid=\"").append(userid).append("\" >");
        requestBuilder.append("<body>");
        for (Egyed egyed : egyedList) {
            for (Biralat biralat : egyed.getBiralatList()) {
                requestBuilder.append("<biral ");
                requestBuilder.append(" tenaz = \"").append(egyed.getTENAZ()).append("\" ");
                requestBuilder.append(" orsko = \"").append(egyed.getORSKO()).append("\" ");
                requestBuilder.append(" azono = \"").append(egyed.getAZONO()).append("\" ");
                requestBuilder.append(" birda = \"").append(biralat.getBIRDA()).append("\" ");
                requestBuilder.append(" birti = \"").append(biralat.getBIRTI()).append("\" ");
                requestBuilder.append(" kulaz = \"").append(biralat.getKULAZ()).append("\" ");
                requestBuilder.append(" akako = \"").append(biralat.getAKAKO()).append("\" ");
                requestBuilder.append(" ert01 = \"").append(biralat.getERT01()).append("\" ");
                requestBuilder.append(" ert02 = \"").append(biralat.getERT02()).append("\" ");
                requestBuilder.append(" ert03 = \"").append(biralat.getERT03()).append("\" ");
                requestBuilder.append(" ert04 = \"").append(biralat.getERT04()).append("\" ");
                requestBuilder.append(" ert05 = \"").append(biralat.getERT05()).append("\" ");
                requestBuilder.append(" ert06 = \"").append(biralat.getERT06()).append("\" ");
                requestBuilder.append(" ert07 = \"").append(biralat.getERT07()).append("\" ");
                requestBuilder.append(" ert08 = \"").append(biralat.getERT08()).append("\" ");
                requestBuilder.append(" ert09 = \"").append(biralat.getERT09()).append("\" ");
                requestBuilder.append(" ert10 = \"").append(biralat.getERT10()).append("\" ");
                requestBuilder.append(" ert11 = \"").append(biralat.getERT11()).append("\" ");
                requestBuilder.append(" ert12 = \"").append(biralat.getERT12()).append("\" ");
                requestBuilder.append(" ert13 = \"").append(biralat.getERT13()).append("\" ");
                requestBuilder.append(" ert14 = \"").append(biralat.getERT14()).append("\" ");
                requestBuilder.append(" ert15 = \"").append(biralat.getERT15()).append("\" ");
                requestBuilder.append(" ert16 = \"").append(biralat.getERT16()).append("\" ");
                requestBuilder.append(" ert17 = \"").append(biralat.getERT17()).append("\" ");
                requestBuilder.append(" ert18 = \"").append(biralat.getERT18()).append("\" ");
                requestBuilder.append(" ert19 = \"").append(biralat.getERT19()).append("\" ");
                requestBuilder.append(" ert20 = \"").append(biralat.getERT20()).append("\" ");
                requestBuilder.append(" ert21 = \"").append(biralat.getERT21()).append("\" ");
                requestBuilder.append(" ert22 = \"").append(biralat.getERT22()).append("\" ");
                requestBuilder.append(" ert23 = \"").append(biralat.getERT23()).append("\" ");
                requestBuilder.append(" ert24 = \"").append(biralat.getERT24()).append("\" ");
                requestBuilder.append(" ert25 = \"").append(biralat.getERT25()).append("\" ");
                requestBuilder.append(" ert26 = \"").append(biralat.getERT26()).append("\" ");
                requestBuilder.append(" ert27 = \"").append(biralat.getERT27()).append("\" ");
                requestBuilder.append(" ert28 = \"").append(biralat.getERT28()).append("\" ");
                requestBuilder.append(" ert29 = \"").append(biralat.getERT29()).append("\" ");
                requestBuilder.append(" ert30 = \"").append(biralat.getERT30()).append("\" ");
                requestBuilder.append(" kod01 = \"").append(biralat.getKOD01()).append("\" ");
                requestBuilder.append(" kod02 = \"").append(biralat.getKOD02()).append("\" ");
                requestBuilder.append(" kod03 = \"").append(biralat.getKOD03()).append("\" ");
                requestBuilder.append(" kod04 = \"").append(biralat.getKOD04()).append("\" ");
                requestBuilder.append(" kod05 = \"").append(biralat.getKOD05()).append("\" ");
                requestBuilder.append(" kod06 = \"").append(biralat.getKOD06()).append("\" ");
                requestBuilder.append(" kod07 = \"").append(biralat.getKOD07()).append("\" ");
                requestBuilder.append(" kod08 = \"").append(biralat.getKOD08()).append("\" ");
                requestBuilder.append(" kod09 = \"").append(biralat.getKOD09()).append("\" ");
                requestBuilder.append(" kod10 = \"").append(biralat.getKOD10()).append("\" ");
                requestBuilder.append(" kod11 = \"").append(biralat.getKOD11()).append("\" ");
                requestBuilder.append(" kod12 = \"").append(biralat.getKOD12()).append("\" ");
                requestBuilder.append(" kod13 = \"").append(biralat.getKOD13()).append("\" ");
                requestBuilder.append(" kod14 = \"").append(biralat.getKOD14()).append("\" ");
                requestBuilder.append(" kod15 = \"").append(biralat.getKOD15()).append("\" ");
                requestBuilder.append(" kod16 = \"").append(biralat.getKOD16()).append("\" ");
                requestBuilder.append(" kod17 = \"").append(biralat.getKOD17()).append("\" ");
                requestBuilder.append(" kod18 = \"").append(biralat.getKOD18()).append("\" ");
                requestBuilder.append(" kod19 = \"").append(biralat.getKOD19()).append("\" ");
                requestBuilder.append(" kod20 = \"").append(biralat.getKOD20()).append("\" ");
                requestBuilder.append(" kod21 = \"").append(biralat.getKOD21()).append("\" ");
                requestBuilder.append(" kod22 = \"").append(biralat.getKOD22()).append("\" ");
                requestBuilder.append(" kod23 = \"").append(biralat.getKOD23()).append("\" ");
                requestBuilder.append(" kod24 = \"").append(biralat.getKOD24()).append("\" ");
                requestBuilder.append(" kod25 = \"").append(biralat.getKOD25()).append("\" ");
                requestBuilder.append(" kod26 = \"").append(biralat.getKOD26()).append("\" ");
                requestBuilder.append(" kod27 = \"").append(biralat.getKOD27()).append("\" ");
                requestBuilder.append(" kod28 = \"").append(biralat.getKOD28()).append("\" ");
                requestBuilder.append(" kod29 = \"").append(biralat.getKOD29()).append("\" ");
                requestBuilder.append(" kod30 = \"").append(biralat.getKOD30()).append("\" ");
                requestBuilder.append(" ></biral>");
            }
        }
        requestBuilder.append("</body></request>");
        return requestBuilder.toString();
    }
}
