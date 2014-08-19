package hu.flexisys.kbr.util;

import hu.flexisys.kbr.model.Biralat;

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

    public static String getKullembirRequestBody(String userid, List<Biralat> biralatList) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<request type=\"kullembir\" ");
        requestBuilder.append("id=\"").append(DateUtil.getRequestId()).append("\" ");
        requestBuilder.append("userid=\"").append(userid).append("\" >");
        requestBuilder.append("<body>");
        for (Biralat biralat : biralatList) {
            requestBuilder.append("<biral ");
            requestBuilder.append(" tenaz=\"").append(biralat.getTENAZ()).append("\"");
            requestBuilder.append(" orsko=\"").append(biralat.getORSKO()).append("\"");
            requestBuilder.append(" azono=\"").append(biralat.getAZONO()).append("\"");
            requestBuilder.append(" birda=\"").append(DateUtil.formatDate(biralat.getBIRDA())).append("\"");
            requestBuilder.append(" birti=\"").append(biralat.getBIRTI()).append("\"");
            requestBuilder.append(" kulaz=\"").append(biralat.getKULAZ()).append("\"");
            Integer akakko = biralat.getAKAKO();
            if (akakko != null && akakko > 0 && akakko <= 5) {
                requestBuilder.append(" akako=\"").append(biralat.getAKAKO()).append("\"");
            } else {
                requestBuilder.append(" akako=\"").append("null").append("\"");
            }
            if (biralat.getERT01() != null && !biralat.getERT01().isEmpty()) {
                requestBuilder.append(" ert01=\"").append(biralat.getERT01()).append("\"");
            }
            if (biralat.getERT02() != null && !biralat.getERT02().isEmpty()) {
                requestBuilder.append(" ert02=\"").append(biralat.getERT02()).append("\"");
            }
            if (biralat.getERT03() != null && !biralat.getERT03().isEmpty()) {
                requestBuilder.append(" ert03=\"").append(biralat.getERT03()).append("\"");
            }
            if (biralat.getERT04() != null && !biralat.getERT04().isEmpty()) {
                requestBuilder.append(" ert04=\"").append(biralat.getERT04()).append("\"");
            }
            if (biralat.getERT05() != null && !biralat.getERT05().isEmpty()) {
                requestBuilder.append(" ert05=\"").append(biralat.getERT05()).append("\"");
            }
            if (biralat.getERT06() != null && !biralat.getERT06().isEmpty()) {
                requestBuilder.append(" ert06=\"").append(biralat.getERT06()).append("\"");
            }
            if (biralat.getERT07() != null && !biralat.getERT07().isEmpty()) {
                requestBuilder.append(" ert07=\"").append(biralat.getERT07()).append("\"");
            }
            if (biralat.getERT08() != null && !biralat.getERT08().isEmpty()) {
                requestBuilder.append(" ert08=\"").append(biralat.getERT08()).append("\"");
            }
            if (biralat.getERT09() != null && !biralat.getERT09().isEmpty()) {
                requestBuilder.append(" ert09=\"").append(biralat.getERT09()).append("\"");
            }
            if (biralat.getERT10() != null && !biralat.getERT10().isEmpty()) {
                requestBuilder.append(" ert10=\"").append(biralat.getERT10()).append("\"");
            }
            if (biralat.getERT11() != null && !biralat.getERT11().isEmpty()) {
                requestBuilder.append(" ert11=\"").append(biralat.getERT11()).append("\"");
            }
            if (biralat.getERT12() != null && !biralat.getERT12().isEmpty()) {
                requestBuilder.append(" ert12=\"").append(biralat.getERT12()).append("\"");
            }
            if (biralat.getERT13() != null && !biralat.getERT13().isEmpty()) {
                requestBuilder.append(" ert13=\"").append(biralat.getERT13()).append("\"");
            }
            if (biralat.getERT14() != null && !biralat.getERT14().isEmpty()) {
                requestBuilder.append(" ert14=\"").append(biralat.getERT14()).append("\"");
            }
            if (biralat.getERT15() != null && !biralat.getERT15().isEmpty()) {
                requestBuilder.append(" ert15=\"").append(biralat.getERT15()).append("\"");
            }
            if (biralat.getERT16() != null && !biralat.getERT16().isEmpty()) {
                requestBuilder.append(" ert16=\"").append(biralat.getERT16()).append("\"");
            }
            if (biralat.getERT17() != null && !biralat.getERT17().isEmpty()) {
                requestBuilder.append(" ert17=\"").append(biralat.getERT17()).append("\"");
            }
            if (biralat.getERT18() != null && !biralat.getERT18().isEmpty()) {
                requestBuilder.append(" ert18=\"").append(biralat.getERT18()).append("\"");
            }
            if (biralat.getERT19() != null && !biralat.getERT19().isEmpty()) {
                requestBuilder.append(" ert19=\"").append(biralat.getERT19()).append("\"");
            }
            if (biralat.getERT20() != null && !biralat.getERT20().isEmpty()) {
                requestBuilder.append(" ert20=\"").append(biralat.getERT20()).append("\"");
            }
            if (biralat.getERT21() != null && !biralat.getERT21().isEmpty()) {
                requestBuilder.append(" ert21=\"").append(biralat.getERT21()).append("\"");
            }
            if (biralat.getERT22() != null && !biralat.getERT22().isEmpty()) {
                requestBuilder.append(" ert22=\"").append(biralat.getERT22()).append("\"");
            }
            if (biralat.getERT23() != null && !biralat.getERT23().isEmpty()) {
                requestBuilder.append(" ert23=\"").append(biralat.getERT23()).append("\"");
            }
            if (biralat.getERT24() != null && !biralat.getERT24().isEmpty()) {
                requestBuilder.append(" ert24=\"").append(biralat.getERT24()).append("\"");
            }
            if (biralat.getERT25() != null && !biralat.getERT25().isEmpty()) {
                requestBuilder.append(" ert25=\"").append(biralat.getERT25()).append("\"");
            }
            if (biralat.getERT26() != null && !biralat.getERT26().isEmpty()) {
                requestBuilder.append(" ert26=\"").append(biralat.getERT26()).append("\"");
            }
            if (biralat.getERT27() != null && !biralat.getERT27().isEmpty()) {
                requestBuilder.append(" ert27=\"").append(biralat.getERT27()).append("\"");
            }
            if (biralat.getERT28() != null && !biralat.getERT28().isEmpty()) {
                requestBuilder.append(" ert28=\"").append(biralat.getERT28()).append("\"");
            }
            if (biralat.getERT29() != null && !biralat.getERT29().isEmpty()) {
                requestBuilder.append(" ert29=\"").append(biralat.getERT29()).append("\"");
            }
            if (biralat.getERT30() != null && !biralat.getERT30().isEmpty()) {
                requestBuilder.append(" ert30=\"").append(biralat.getERT30()).append("\"");
            }
            if (biralat.getKOD01() != null && !biralat.getKOD01().isEmpty()) {
                requestBuilder.append(" kod01=\"").append(biralat.getKOD01()).append("\"");
            }
            if (biralat.getKOD02() != null && !biralat.getKOD02().isEmpty()) {
                requestBuilder.append(" kod02=\"").append(biralat.getKOD02()).append("\"");
            }
            if (biralat.getKOD03() != null && !biralat.getKOD03().isEmpty()) {
                requestBuilder.append(" kod03=\"").append(biralat.getKOD03()).append("\"");
            }
            if (biralat.getKOD04() != null && !biralat.getKOD04().isEmpty()) {
                requestBuilder.append(" kod04=\"").append(biralat.getKOD04()).append("\"");
            }
            if (biralat.getKOD05() != null && !biralat.getKOD05().isEmpty()) {
                requestBuilder.append(" kod05=\"").append(biralat.getKOD05()).append("\"");
            }
            if (biralat.getKOD06() != null && !biralat.getKOD06().isEmpty()) {
                requestBuilder.append(" kod06=\"").append(biralat.getKOD06()).append("\"");
            }
            if (biralat.getKOD07() != null && !biralat.getKOD07().isEmpty()) {
                requestBuilder.append(" kod07=\"").append(biralat.getKOD07()).append("\"");
            }
            if (biralat.getKOD08() != null && !biralat.getKOD08().isEmpty()) {
                requestBuilder.append(" kod08=\"").append(biralat.getKOD08()).append("\"");
            }
            if (biralat.getKOD09() != null && !biralat.getKOD09().isEmpty()) {
                requestBuilder.append(" kod09=\"").append(biralat.getKOD09()).append("\"");
            }
            if (biralat.getKOD10() != null && !biralat.getKOD10().isEmpty()) {
                requestBuilder.append(" kod10=\"").append(biralat.getKOD10()).append("\"");
            }
            if (biralat.getKOD11() != null && !biralat.getKOD11().isEmpty()) {
                requestBuilder.append(" kod11=\"").append(biralat.getKOD11()).append("\"");
            }
            if (biralat.getKOD12() != null && !biralat.getKOD12().isEmpty()) {
                requestBuilder.append(" kod12=\"").append(biralat.getKOD12()).append("\"");
            }
            if (biralat.getKOD13() != null && !biralat.getKOD13().isEmpty()) {
                requestBuilder.append(" kod13=\"").append(biralat.getKOD13()).append("\"");
            }
            if (biralat.getKOD14() != null && !biralat.getKOD14().isEmpty()) {
                requestBuilder.append(" kod14=\"").append(biralat.getKOD14()).append("\"");
            }
            if (biralat.getKOD15() != null && !biralat.getKOD15().isEmpty()) {
                requestBuilder.append(" kod15=\"").append(biralat.getKOD15()).append("\"");
            }
            if (biralat.getKOD16() != null && !biralat.getKOD16().isEmpty()) {
                requestBuilder.append(" kod16=\"").append(biralat.getKOD16()).append("\"");
            }
            if (biralat.getKOD17() != null && !biralat.getKOD17().isEmpty()) {
                requestBuilder.append(" kod17=\"").append(biralat.getKOD17()).append("\"");
            }
            if (biralat.getKOD18() != null && !biralat.getKOD18().isEmpty()) {
                requestBuilder.append(" kod18=\"").append(biralat.getKOD18()).append("\"");
            }
            if (biralat.getKOD19() != null && !biralat.getKOD19().isEmpty()) {
                requestBuilder.append(" kod19=\"").append(biralat.getKOD19()).append("\"");
            }
            if (biralat.getKOD20() != null && !biralat.getKOD20().isEmpty()) {
                requestBuilder.append(" kod20=\"").append(biralat.getKOD20()).append("\"");
            }
            if (biralat.getKOD21() != null && !biralat.getKOD21().isEmpty()) {
                requestBuilder.append(" kod21=\"").append(biralat.getKOD21()).append("\"");
            }
            if (biralat.getKOD22() != null && !biralat.getKOD22().isEmpty()) {
                requestBuilder.append(" kod22=\"").append(biralat.getKOD22()).append("\"");
            }
            if (biralat.getKOD23() != null && !biralat.getKOD23().isEmpty()) {
                requestBuilder.append(" kod23=\"").append(biralat.getKOD23()).append("\"");
            }
            if (biralat.getKOD24() != null && !biralat.getKOD24().isEmpty()) {
                requestBuilder.append(" kod24=\"").append(biralat.getKOD24()).append("\"");
            }
            if (biralat.getKOD25() != null && !biralat.getKOD25().isEmpty()) {
                requestBuilder.append(" kod25=\"").append(biralat.getKOD25()).append("\"");
            }
            if (biralat.getKOD26() != null && !biralat.getKOD26().isEmpty()) {
                requestBuilder.append(" kod26=\"").append(biralat.getKOD26()).append("\"");
            }
            if (biralat.getKOD27() != null && !biralat.getKOD27().isEmpty()) {
                requestBuilder.append(" kod27=\"").append(biralat.getKOD27()).append("\"");
            }
            if (biralat.getKOD28() != null && !biralat.getKOD28().isEmpty()) {
                requestBuilder.append(" kod28=\"").append(biralat.getKOD28()).append("\"");
            }
            if (biralat.getKOD29() != null && !biralat.getKOD29().isEmpty()) {
                requestBuilder.append(" kod29=\"").append(biralat.getKOD29()).append("\"");
            }
            if (biralat.getKOD30() != null && !biralat.getKOD30().isEmpty()) {
                requestBuilder.append(" kod30=\"").append(biralat.getKOD30()).append("\"");
            }
            requestBuilder.append(" ></biral>");
        }
        requestBuilder.append("</body></request>");
        return requestBuilder.toString();
    }
}
