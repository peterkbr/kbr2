package hu.flexisys.kbr.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
