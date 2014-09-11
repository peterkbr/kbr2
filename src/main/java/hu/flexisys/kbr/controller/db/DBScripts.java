package hu.flexisys.kbr.controller.db;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Peter on 2014.07.01..
 */
public class DBScripts {

    public static final String TABLE_TENYESZET = "tenyeszet";
    public static final String COLUMN_TENYESZET_TENAZ = "TENAZ";
    public static final String COLUMN_TENYESZET_TARTO = "TARTO";
    public static final String COLUMN_TENYESZET_TECIM = "TECIM";
    public static final String COLUMN_TENYESZET_LEDAT = "LEDAT";
    public static final String COLUMN_TENYESZET_ERVENYES = "ERVENYES";
    public static final String[] COLUMNS_TENYESZET =
            new String[]{COLUMN_TENYESZET_TENAZ, COLUMN_TENYESZET_TARTO, COLUMN_TENYESZET_TECIM, COLUMN_TENYESZET_LEDAT, COLUMN_TENYESZET_ERVENYES};

    public static final String TABLE_EGYED = "egyed";
    public static final String COLUMN_EGYED_AZONO = "AZONO";
    public static final String COLUMN_EGYED_TENAZ = "TENAZ";
    public static final String COLUMN_EGYED_ORSKO = "ORSKO";
    public static final String COLUMN_EGYED_ELLSO = "ELLSO";
    public static final String COLUMN_EGYED_ELLDA = "ELLDA";
    public static final String COLUMN_EGYED_SZULD = "SZULD";
    public static final String COLUMN_EGYED_FAJKO = "FAJKO";
    public static final String COLUMN_EGYED_KONSK = "KONSK";
    public static final String COLUMN_EGYED_SZINE = "SZINE";
    public static final String COLUMN_EGYED_ITVJE = "ITVJE";
    public static final String COLUMN_EGYED_KIVALASZTOTT = "KIVALASZTOTT";
    public static final String COLUMN_EGYED_UJ = "UJ";
    public static final String[] COLUMNS_EGYED =
            new String[]{COLUMN_EGYED_AZONO, COLUMN_EGYED_TENAZ, COLUMN_EGYED_ORSKO, COLUMN_EGYED_ELLSO, COLUMN_EGYED_ELLDA, COLUMN_EGYED_SZULD,
                    COLUMN_EGYED_FAJKO, COLUMN_EGYED_KONSK, COLUMN_EGYED_SZINE, COLUMN_EGYED_ITVJE, COLUMN_EGYED_KIVALASZTOTT, COLUMN_EGYED_UJ};

    public static final String TABLE_BIRALAT = "biralat";
    public static final String COLUMN_BIRALAT_AZONO = "AZONO";
    public static final String COLUMN_BIRALAT_TENAZ = "TENAZ";
    public static final String COLUMN_BIRALAT_ORSKO = "ORSKO";
    public static final String COLUMN_BIRALAT_BIRDA = "BIRDA";
    public static final String COLUMN_BIRALAT_BIRTI = "BIRTI";
    public static final String COLUMN_BIRALAT_KULAZ = "KULAZ";
    public static final String COLUMN_BIRALAT_AKAKO = "AKAKO";
    public static final String COLUMN_BIRALAT_FELTOLTETLEN = "FELTOLTETLEN";
    public static final String COLUMN_BIRALAT_EXPORTALT = "EXPORTALT";
    public static final String COLUMN_BIRALAT_LETOLTOTT = "LETOLTOTT";
    public static final String COLUMN_BIRALAT_KOD01 = "KOD01";
    public static final String COLUMN_BIRALAT_ERT01 = "ERT01";
    public static final String COLUMN_BIRALAT_KOD02 = "KOD02";
    public static final String COLUMN_BIRALAT_ERT02 = "ERT02";
    public static final String COLUMN_BIRALAT_KOD03 = "KOD03";
    public static final String COLUMN_BIRALAT_ERT03 = "ERT03";
    public static final String COLUMN_BIRALAT_KOD04 = "KOD04";
    public static final String COLUMN_BIRALAT_ERT04 = "ERT04";
    public static final String COLUMN_BIRALAT_KOD05 = "KOD05";
    public static final String COLUMN_BIRALAT_ERT05 = "ERT05";
    public static final String COLUMN_BIRALAT_KOD06 = "KOD06";
    public static final String COLUMN_BIRALAT_ERT06 = "ERT06";
    public static final String COLUMN_BIRALAT_KOD07 = "KOD07";
    public static final String COLUMN_BIRALAT_ERT07 = "ERT07";
    public static final String COLUMN_BIRALAT_KOD08 = "KOD08";
    public static final String COLUMN_BIRALAT_ERT08 = "ERT08";
    public static final String COLUMN_BIRALAT_KOD09 = "KOD09";
    public static final String COLUMN_BIRALAT_ERT09 = "ERT09";
    public static final String COLUMN_BIRALAT_KOD10 = "KOD10";
    public static final String COLUMN_BIRALAT_ERT10 = "ERT10";
    public static final String COLUMN_BIRALAT_KOD11 = "KOD11";
    public static final String COLUMN_BIRALAT_ERT11 = "ERT11";
    public static final String COLUMN_BIRALAT_KOD12 = "KOD12";
    public static final String COLUMN_BIRALAT_ERT12 = "ERT12";
    public static final String COLUMN_BIRALAT_KOD13 = "KOD13";
    public static final String COLUMN_BIRALAT_ERT13 = "ERT13";
    public static final String COLUMN_BIRALAT_KOD14 = "KOD14";
    public static final String COLUMN_BIRALAT_ERT14 = "ERT14";
    public static final String COLUMN_BIRALAT_KOD15 = "KOD15";
    public static final String COLUMN_BIRALAT_ERT15 = "ERT15";
    public static final String COLUMN_BIRALAT_KOD16 = "KOD16";
    public static final String COLUMN_BIRALAT_ERT16 = "ERT16";
    public static final String COLUMN_BIRALAT_KOD17 = "KOD17";
    public static final String COLUMN_BIRALAT_ERT17 = "ERT17";
    public static final String COLUMN_BIRALAT_KOD18 = "KOD18";
    public static final String COLUMN_BIRALAT_ERT18 = "ERT18";
    public static final String COLUMN_BIRALAT_KOD19 = "KOD19";
    public static final String COLUMN_BIRALAT_ERT19 = "ERT19";
    public static final String COLUMN_BIRALAT_KOD20 = "KOD20";
    public static final String COLUMN_BIRALAT_ERT20 = "ERT20";
    public static final String COLUMN_BIRALAT_KOD21 = "KOD21";
    public static final String COLUMN_BIRALAT_ERT21 = "ERT21";
    public static final String COLUMN_BIRALAT_KOD22 = "KOD22";
    public static final String COLUMN_BIRALAT_ERT22 = "ERT22";
    public static final String COLUMN_BIRALAT_KOD23 = "KOD23";
    public static final String COLUMN_BIRALAT_ERT23 = "ERT23";
    public static final String COLUMN_BIRALAT_KOD24 = "KOD24";
    public static final String COLUMN_BIRALAT_ERT24 = "ERT24";
    public static final String COLUMN_BIRALAT_KOD25 = "KOD25";
    public static final String COLUMN_BIRALAT_ERT25 = "ERT25";
    public static final String COLUMN_BIRALAT_KOD26 = "KOD26";
    public static final String COLUMN_BIRALAT_ERT26 = "ERT26";
    public static final String COLUMN_BIRALAT_KOD27 = "KOD27";
    public static final String COLUMN_BIRALAT_ERT27 = "ERT27";
    public static final String COLUMN_BIRALAT_KOD28 = "KOD28";
    public static final String COLUMN_BIRALAT_ERT28 = "ERT28";
    public static final String COLUMN_BIRALAT_KOD29 = "KOD29";
    public static final String COLUMN_BIRALAT_ERT29 = "ERT29";
    public static final String COLUMN_BIRALAT_KOD30 = "KOD30";
    public static final String COLUMN_BIRALAT_ERT30 = "ERT30";
    public static final String[] COLUMNS_BIRALAT =
            new String[]{COLUMN_BIRALAT_AZONO, COLUMN_BIRALAT_TENAZ, COLUMN_BIRALAT_ORSKO, COLUMN_BIRALAT_BIRDA, COLUMN_BIRALAT_BIRTI, COLUMN_BIRALAT_KULAZ,
                    COLUMN_BIRALAT_AKAKO, COLUMN_BIRALAT_FELTOLTETLEN, COLUMN_BIRALAT_EXPORTALT, COLUMN_BIRALAT_LETOLTOTT, COLUMN_BIRALAT_KOD01,
                    COLUMN_BIRALAT_ERT01, COLUMN_BIRALAT_KOD02, COLUMN_BIRALAT_ERT02, COLUMN_BIRALAT_KOD03, COLUMN_BIRALAT_ERT03, COLUMN_BIRALAT_KOD04,
                    COLUMN_BIRALAT_ERT04, COLUMN_BIRALAT_KOD05, COLUMN_BIRALAT_ERT05, COLUMN_BIRALAT_KOD06, COLUMN_BIRALAT_ERT06, COLUMN_BIRALAT_KOD07,
                    COLUMN_BIRALAT_ERT07, COLUMN_BIRALAT_KOD08, COLUMN_BIRALAT_ERT08, COLUMN_BIRALAT_KOD09, COLUMN_BIRALAT_ERT09, COLUMN_BIRALAT_KOD10,
                    COLUMN_BIRALAT_ERT10, COLUMN_BIRALAT_KOD11, COLUMN_BIRALAT_ERT11, COLUMN_BIRALAT_KOD12, COLUMN_BIRALAT_ERT12, COLUMN_BIRALAT_KOD13,
                    COLUMN_BIRALAT_ERT13, COLUMN_BIRALAT_KOD14, COLUMN_BIRALAT_ERT14, COLUMN_BIRALAT_KOD15, COLUMN_BIRALAT_ERT15, COLUMN_BIRALAT_KOD16,
                    COLUMN_BIRALAT_ERT16, COLUMN_BIRALAT_KOD17, COLUMN_BIRALAT_ERT17, COLUMN_BIRALAT_KOD18, COLUMN_BIRALAT_ERT18, COLUMN_BIRALAT_KOD19,
                    COLUMN_BIRALAT_ERT19, COLUMN_BIRALAT_KOD20, COLUMN_BIRALAT_ERT20, COLUMN_BIRALAT_KOD21, COLUMN_BIRALAT_ERT21, COLUMN_BIRALAT_KOD22,
                    COLUMN_BIRALAT_ERT22, COLUMN_BIRALAT_KOD23, COLUMN_BIRALAT_ERT23, COLUMN_BIRALAT_KOD24, COLUMN_BIRALAT_ERT24, COLUMN_BIRALAT_KOD25,
                    COLUMN_BIRALAT_ERT25, COLUMN_BIRALAT_KOD26, COLUMN_BIRALAT_ERT26, COLUMN_BIRALAT_KOD27, COLUMN_BIRALAT_ERT27, COLUMN_BIRALAT_KOD28,
                    COLUMN_BIRALAT_ERT28, COLUMN_BIRALAT_KOD29, COLUMN_BIRALAT_ERT29, COLUMN_BIRALAT_KOD30, COLUMN_BIRALAT_ERT30};

    public static List<String> createDBStrings() {
        List<String> strings = new ArrayList<String>();

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(TABLE_TENYESZET).append(" ( ");
        builder.append(COLUMN_TENYESZET_TENAZ).append(" TEXT PRIMARY KEY, ");
        builder.append(COLUMN_TENYESZET_TARTO).append(" TEXT, ");
        builder.append(COLUMN_TENYESZET_TECIM).append(" TEXT, ");
        builder.append(COLUMN_TENYESZET_LEDAT).append(" INTEGER, ");
        builder.append(COLUMN_TENYESZET_ERVENYES).append(" BOOLEAN ");
        builder.append(");");
        strings.add(builder.toString());

        builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(TABLE_EGYED).append(" ( ");
        builder.append(COLUMN_EGYED_AZONO).append(" TEXT PRIMARY KEY, ");
        builder.append(COLUMN_EGYED_TENAZ).append(" TEXT, ");
        builder.append(COLUMN_EGYED_ORSKO).append(" TEXT, ");
        builder.append(COLUMN_EGYED_ELLSO).append(" INTEGER, ");
        builder.append(COLUMN_EGYED_ELLDA).append(" INTEGER, ");
        builder.append(COLUMN_EGYED_SZULD).append(" INTEGER, ");
        builder.append(COLUMN_EGYED_FAJKO).append(" INTEGER, ");
        builder.append(COLUMN_EGYED_KONSK).append(" INTEGER, ");
        builder.append(COLUMN_EGYED_SZINE).append(" INTEGER, ");
        builder.append(COLUMN_EGYED_ITVJE).append(" BOOLEAN, ");
        builder.append(COLUMN_EGYED_KIVALASZTOTT).append(" BOOLEAN, ");
        builder.append(COLUMN_EGYED_UJ).append(" BOOLEAN ");
        builder.append(");");
        strings.add(builder.toString());

        builder = new StringBuilder();
        builder.append("CREATE TABLE ").append(TABLE_BIRALAT).append(" ( ");
        builder.append(COLUMN_BIRALAT_TENAZ).append(" TEXT, ");
        builder.append(COLUMN_BIRALAT_ORSKO).append(" TEXT, ");
        builder.append(COLUMN_BIRALAT_AZONO).append(" TEXT, ");
        builder.append(COLUMN_BIRALAT_BIRDA).append(" INTEGER, ");
        builder.append(COLUMN_BIRALAT_BIRTI).append(" INTEGER, ");
        builder.append(COLUMN_BIRALAT_KULAZ).append(" TEXT, ");
        builder.append(COLUMN_BIRALAT_AKAKO).append(" INTEGER, ");
        builder.append(COLUMN_BIRALAT_FELTOLTETLEN).append(" BOOLEAN, ");
        builder.append(COLUMN_BIRALAT_EXPORTALT).append(" BOOLEAN, ");
        builder.append(COLUMN_BIRALAT_LETOLTOTT).append(" BOOLEAN ");
        for (int i = 10; i < COLUMNS_BIRALAT.length; i++) {
            builder.append(", ").append(COLUMNS_BIRALAT[i]).append(" TEXT ");
        }
        builder.append(");");
        strings.add(builder.toString());

        return strings;
    }

}
