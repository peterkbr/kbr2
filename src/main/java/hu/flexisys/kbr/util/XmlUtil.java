package hu.flexisys.kbr.util;

import hu.flexisys.kbr.controller.network.model.KT_Response;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.model.Tenyeszet;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;

/**
 * Created by Peter on 2014.07.02..
 */
public class XmlUtil {

    private static final String TN_RESPONSE = "response";
    private static final String TN_RESPONSE_ID = "id";
    private static final String TN_RESPONSE_RESULT_CODE = "result_code";
    private static final String TN_RESPONSE_RESULT_MESSAGE = "result_message";
    private static final String TN_RESPONSE_TYPE = "type";
    private static final String TN_RESPONSE_USERID = "userid";

    private static final String TN_TENY = "teny";
    private static final String TN_TENY_LEDAT = "ledat";
    private static final String TN_TENY_TARTO = "tarto";
    private static final String TN_TENY_TECIM = "tecim";
    private static final String TN_TENY_TENAZ = "tenaz";

    private static final String TN_EGYED = "egyed";
    private static final String TN_EGYED_TENAZ = "tenaz";
    private static final String TN_EGYED_ORSKO = "orsko";
    private static final String TN_EGYED_AZONO = "azono";
    private static final String TN_EGYED_ELLSO = "ellso";
    private static final String TN_EGYED_ELLDA = "ellda";
    private static final String TN_EGYED_SZULD = "szuld";
    private static final String TN_EGYED_FAJKO = "fajko";
    private static final String TN_EGYED_KONSK = "konsk";
    private static final String TN_EGYED_SZINE = "szine";
    private static final String TN_EGYED_ITVJE = "itvje";
    // egyed bírálata
    private static final String TN_EGYED_BIRDA = "birda";
    private static final String TN_EGYED_BIRTI = "birti";
    private static final String TN_EGYED_KULAZ = "kulaz";
    private static final String TN_EGYED_AKAKO = "akako";
    private static final String TN_EGYED_KOD01 = "kod01";
    private static final String TN_EGYED_ERT01 = "ert01";
    private static final String TN_EGYED_KOD02 = "kod02";
    private static final String TN_EGYED_ERT02 = "ert02";
    private static final String TN_EGYED_KOD03 = "kod03";
    private static final String TN_EGYED_ERT03 = "ert03";
    private static final String TN_EGYED_KOD04 = "kod04";
    private static final String TN_EGYED_ERT04 = "ert04";
    private static final String TN_EGYED_KOD05 = "kod05";
    private static final String TN_EGYED_ERT05 = "ert05";
    private static final String TN_EGYED_KOD06 = "kod06";
    private static final String TN_EGYED_ERT06 = "ert06";
    private static final String TN_EGYED_KOD07 = "kod07";
    private static final String TN_EGYED_ERT07 = "ert07";
    private static final String TN_EGYED_KOD08 = "kod08";
    private static final String TN_EGYED_ERT08 = "ert08";
    private static final String TN_EGYED_KOD09 = "kod09";
    private static final String TN_EGYED_ERT09 = "ert09";
    private static final String TN_EGYED_KOD10 = "kod10";
    private static final String TN_EGYED_ERT10 = "ert10";
    private static final String TN_EGYED_KOD11 = "kod11";
    private static final String TN_EGYED_ERT11 = "ert11";
    private static final String TN_EGYED_KOD12 = "kod12";
    private static final String TN_EGYED_ERT12 = "ert12";
    private static final String TN_EGYED_KOD13 = "kod13";
    private static final String TN_EGYED_ERT13 = "ert13";
    private static final String TN_EGYED_KOD14 = "kod14";
    private static final String TN_EGYED_ERT14 = "ert14";
    private static final String TN_EGYED_KOD15 = "kod15";
    private static final String TN_EGYED_ERT15 = "ert15";
    private static final String TN_EGYED_KOD16 = "kod16";
    private static final String TN_EGYED_ERT16 = "ert16";
    private static final String TN_EGYED_KOD17 = "kod17";
    private static final String TN_EGYED_ERT17 = "ert17";
    private static final String TN_EGYED_KOD18 = "kod18";
    private static final String TN_EGYED_ERT18 = "ert18";
    private static final String TN_EGYED_KOD19 = "kod19";
    private static final String TN_EGYED_ERT19 = "ert19";
    private static final String TN_EGYED_KOD20 = "kod20";
    private static final String TN_EGYED_ERT20 = "ert20";
    private static final String TN_EGYED_KOD21 = "kod21";
    private static final String TN_EGYED_ERT21 = "ert21";
    private static final String TN_EGYED_KOD22 = "kod22";
    private static final String TN_EGYED_ERT22 = "ert22";
    private static final String TN_EGYED_KOD23 = "kod23";
    private static final String TN_EGYED_ERT23 = "ert23";
    private static final String TN_EGYED_KOD24 = "kod24";
    private static final String TN_EGYED_ERT24 = "ert24";
    private static final String TN_EGYED_KOD25 = "kod25";
    private static final String TN_EGYED_ERT25 = "ert25";
    private static final String TN_EGYED_KOD26 = "kod26";
    private static final String TN_EGYED_ERT26 = "ert26";
    private static final String TN_EGYED_KOD27 = "kod27";
    private static final String TN_EGYED_ERT27 = "ert27";
    private static final String TN_EGYED_KOD28 = "kod28";
    private static final String TN_EGYED_ERT28 = "ert28";
    private static final String TN_EGYED_KOD29 = "kod29";
    private static final String TN_EGYED_ERT29 = "ert29";
    private static final String TN_EGYED_KOD30 = "kod30";
    private static final String TN_EGYED_ERT30 = "ert30";

    public static Tenyeszet parseKullemtenyXml(String xml) throws XmlUtilException, XmlPullParserException, ParseException, IOException {
        Tenyeszet tenyeszet = new Tenyeszet();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(xml));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
            } else if (eventType == XmlPullParser.START_TAG) {

                if (xpp.getName().equals(TN_RESPONSE)) {
                    KT_Response response = new KT_Response();
                    int count = xpp.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        if (xpp.getAttributeName(i).equals(TN_RESPONSE_ID)) {
                            response.setId(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_RESULT_CODE)) {
                            response.setResult_code(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_RESULT_MESSAGE)) {
                            response.setResult_message(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_TYPE)) {
                            response.setType(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_USERID)) {
                            response.setUserid(xpp.getAttributeValue(i));
                        }
                    }
                    if (response.getResult_code() == null || !response.getResult_code().equals("0")) {
                        throw new XmlUtilException(response.getResult_code() + " \"" + response.getResult_message() + "\"");
                    }
                } else if (xpp.getName().equals(TN_TENY)) {
                    int count = xpp.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        if (xpp.getAttributeName(i).equals(TN_TENY_TENAZ)) {
                            tenyeszet.setTENAZ(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_TENY_LEDAT)) {
                            tenyeszet.setLEDAT(DateUtil.getDateFromTimestampString(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_TENY_TARTO)) {
                            tenyeszet.setTARTO(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_TENY_TECIM)) {
                            tenyeszet.setTECIM(xpp.getAttributeValue(i));
                        }
                    }
                } else if (xpp.getName().equals(TN_EGYED)) {
                    Egyed egyed = new Egyed();
                    Biralat biralat = new Biralat();
                    int count = xpp.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        if (xpp.getAttributeName(i).equals(TN_EGYED_TENAZ)) {
                            egyed.setTENAZ(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ORSKO)) {
                            egyed.setORSKO(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_AZONO)) {
                            egyed.setAZONO(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ELLSO)) {
                            egyed.setELLSO(Integer.valueOf(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ELLDA)) {
                            egyed.setELLDA(DateUtil.getDateFromDateString(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_SZULD)) {
                            egyed.setSZULD(DateUtil.getDateFromDateString(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_FAJKO)) {
                            egyed.setFAJKO(Integer.valueOf(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KONSK)) {
                            egyed.setKONSK(Integer.valueOf(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_SZINE)) {
                            egyed.setSZINE(Integer.valueOf(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ITVJE)) {
                            String itv = xpp.getAttributeValue(i);
                            egyed.setITVJE(itv.equals("i") || itv.equals("I"));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_BIRDA)) {
                            biralat.setBIRDA(DateUtil.getDateFromDateString(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_BIRTI)) {
                            biralat.setBIRTI(Integer.valueOf(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KULAZ)) {
                            biralat.setKULAZ(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_AKAKO)) {
                            biralat.setAKAKO(Integer.valueOf(xpp.getAttributeValue(i)));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD01)) {
                            biralat.setKOD01(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT01)) {
                            biralat.setERT01(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD02)) {
                            biralat.setKOD02(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT02)) {
                            biralat.setERT02(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD03)) {
                            biralat.setKOD03(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT03)) {
                            biralat.setERT03(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD04)) {
                            biralat.setKOD04(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT04)) {
                            biralat.setERT04(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD05)) {
                            biralat.setKOD05(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT05)) {
                            biralat.setERT05(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD06)) {
                            biralat.setKOD06(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT06)) {
                            biralat.setERT06(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD07)) {
                            biralat.setKOD07(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT07)) {
                            biralat.setERT07(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD08)) {
                            biralat.setKOD08(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT08)) {
                            biralat.setERT08(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD09)) {
                            biralat.setKOD09(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT09)) {
                            biralat.setERT09(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD10)) {
                            biralat.setKOD10(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT10)) {
                            biralat.setERT10(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD11)) {
                            biralat.setKOD11(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT11)) {
                            biralat.setERT11(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD12)) {
                            biralat.setKOD12(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT12)) {
                            biralat.setERT12(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD13)) {
                            biralat.setKOD13(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT13)) {
                            biralat.setERT13(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD14)) {
                            biralat.setKOD14(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT14)) {
                            biralat.setERT14(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD15)) {
                            biralat.setKOD15(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT15)) {
                            biralat.setERT15(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD16)) {
                            biralat.setKOD16(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT16)) {
                            biralat.setERT16(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD17)) {
                            biralat.setKOD17(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT17)) {
                            biralat.setERT17(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD18)) {
                            biralat.setKOD18(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT18)) {
                            biralat.setERT18(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD19)) {
                            biralat.setKOD19(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT19)) {
                            biralat.setERT19(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD20)) {
                            biralat.setKOD20(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT20)) {
                            biralat.setERT20(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD21)) {
                            biralat.setKOD21(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT21)) {
                            biralat.setERT21(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD22)) {
                            biralat.setKOD22(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT22)) {
                            biralat.setERT22(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD23)) {
                            biralat.setKOD23(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT23)) {
                            biralat.setERT23(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD24)) {
                            biralat.setKOD24(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT24)) {
                            biralat.setERT24(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD25)) {
                            biralat.setKOD25(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT25)) {
                            biralat.setERT25(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD26)) {
                            biralat.setKOD26(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT26)) {
                            biralat.setERT26(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD27)) {
                            biralat.setKOD27(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT27)) {
                            biralat.setERT27(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD28)) {
                            biralat.setKOD28(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT28)) {
                            biralat.setERT28(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD29)) {
                            biralat.setKOD29(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT29)) {
                            biralat.setERT29(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_KOD30)) {
                            biralat.setKOD30(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_EGYED_ERT30)) {
                            biralat.setERT30(xpp.getAttributeValue(i));
                        }
                    }
                    if (biralat.getBIRDA() != null && biralat.getKULAZ() != null) {
                        biralat.setAZONO(egyed.getAZONO());
                        biralat.setTENAZ(egyed.getTENAZ());
                        biralat.setORSKO(egyed.getORSKO());
                        biralat.setFELTOLTETLEN(false);
                        biralat.setEXPORTALT(true);
                        biralat.setLETOLTOTT(true);
                        egyed.addBiralat(biralat);
                    }
                    egyed.setKIVALASZTOTT(false);
                    egyed.setUJ(false);
                    tenyeszet.addEgyed(egyed);
                }
            } else if (eventType == XmlPullParser.END_TAG) {
            } else if (eventType == XmlPullParser.TEXT) {
            }
            eventType = xpp.next();
        }
        tenyeszet.setERVENYES(true);
        return tenyeszet;
    }


    public static Boolean parseKullembirXml(String xml) throws XmlPullParserException, XmlUtilException, IOException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(xml));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
            } else if (eventType == XmlPullParser.START_TAG) {

                if (xpp.getName().equals(TN_RESPONSE)) {
                    KT_Response response = new KT_Response();
                    int count = xpp.getAttributeCount();
                    for (int i = 0; i < count; i++) {
                        if (xpp.getAttributeName(i).equals(TN_RESPONSE_ID)) {
                            response.setId(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_RESULT_CODE)) {
                            response.setResult_code(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_RESULT_MESSAGE)) {
                            response.setResult_message(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_TYPE)) {
                            response.setType(xpp.getAttributeValue(i));
                        } else if (xpp.getAttributeName(i).equals(TN_RESPONSE_USERID)) {
                            response.setUserid(xpp.getAttributeValue(i));
                        }
                    }
                    if (response.getResult_code() == null || !response.getResult_code().equals("0")) {
                        throw new XmlUtilException(response.getResult_code() + " \"" + response.getResult_message() + "\"");
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG) {
            } else if (eventType == XmlPullParser.TEXT) {
            }
            eventType = xpp.next();
        }
        return true;
    }
}
