package hu.flexisys.kbr.util.export;

import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 01/09/14.
 */
public class LevalogatasCvsExporter {
    public static String export(String basePath, List<Egyed> selectedEgyedList) throws IOException {
        String path = basePath + File.separator + "csvExport_" + DateUtil.formatTimestampFileName(new Date()) + ".csv";
        FileOutputStream fOut = new FileOutputStream(new File(path));
        OutputStreamWriter osw = new OutputStreamWriter(fOut);

        StringBuilder builder = new StringBuilder();
        String csvSeparator = ";";

        // header
        builder.append("#");
        builder.append(csvSeparator).append("OK");
        builder.append(csvSeparator).append("ENAR");
        builder.append(csvSeparator).append("Haszn");
        builder.append(csvSeparator).append("ES");
        builder.append(csvSeparator).append("Ellés dátum");
        builder.append(csvSeparator).append("Született");
        builder.append(csvSeparator).append("Konstr");
        builder.append(csvSeparator).append("ITV");
        builder.append(csvSeparator).append("FK");
        builder.append("\n");

        // values
        int i = 1;
        for (Egyed egyed : selectedEgyedList) {
            builder.append(String.valueOf(i++));
            builder.append(csvSeparator).append(egyed.getORSKO());


            String azono = egyed.getAZONO();
            String orsko = egyed.getORSKO();
            if ("HU".equals(orsko) && azono.length() == 10) {
                builder.append(csvSeparator).append(azono);
                builder.append(csvSeparator).append(azono.substring(5, 9));
            } else {
                builder.append(csvSeparator).append(azono);
                builder.append(csvSeparator).append("");
            }

            builder.append(csvSeparator).append(String.valueOf(egyed.getELLSO()));
            builder.append(csvSeparator).append(DateUtil.formatDate(egyed.getELLDA()));
            builder.append(csvSeparator).append(DateUtil.formatDate(egyed.getSZULD()));
            builder.append(csvSeparator).append(String.valueOf(egyed.getKONSK()));
            builder.append(csvSeparator).append(egyed.getITVJE() ? "i" : "n");
            builder.append(csvSeparator).append(String.valueOf(egyed.getFAJKO()));
            builder.append("\n");
        }

        osw.write(builder.toString());
        osw.flush();
        osw.close();
        return path;
    }
}