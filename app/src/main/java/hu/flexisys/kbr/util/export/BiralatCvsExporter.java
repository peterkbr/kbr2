package hu.flexisys.kbr.util.export;

import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 31/08/14.
 */
public class BiralatCvsExporter extends CsvExporter {

    public static void export(String basePath, String biralatTipus, List<Biralat> biralatList, Map<String, Egyed> egyedMap) throws IOException {
        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(biralatTipus);
        List<BiralatSzempont> szempontList = new ArrayList<BiralatSzempont>();
        for (String szempontId : tipus.szempontList) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(szempontId);
            szempontList.add(szempont);
        }

        StringBuilder builder = new StringBuilder();

        // header
        builder.append("#");
        builder.append(csvSeparator).append("OK");
        builder.append(csvSeparator).append("ENAR");
        builder.append(csvSeparator).append("Telep");
        builder.append(csvSeparator).append("Dátum");
        builder.append(csvSeparator).append("Ell");
        builder.append(csvSeparator).append("Sz");
        for (BiralatSzempont szempont : szempontList) {
            builder.append(csvSeparator).append(szempont.rovidNev);
        }
        builder.append(csvSeparator).append("A");
        builder.append("\n");

        // values
        int i = 1;
        for (Biralat biralat : biralatList) {
            builder.append(String.valueOf(i++));
            builder.append(csvSeparator).append(biralat.getORSKO());
            builder.append(csvSeparator).append(biralat.getAZONO());
            builder.append(csvSeparator).append(biralat.getTENAZ());
            builder.append(csvSeparator).append(DateUtil.formatDate(biralat.getBIRDA()));

            Egyed egyed = egyedMap.get(biralat.getAZONO());
            builder.append(csvSeparator).append(String.valueOf(egyed.getELLSO()));
            builder.append(csvSeparator).append(String.valueOf(egyed.getSZINE()));
            for (BiralatSzempont szempont : szempontList) {
                builder.append(csvSeparator).append(biralat.getErtByKod(szempont.kod));
            }
            builder.append(csvSeparator).append(String.valueOf(biralat.getAKAKO()));
            builder.append("\n");
        }

//        String path = basePath + File.separator + "csvExport_" + DateUtil.formatTimestampFileName(new Date()) + ".csv";
//        FileOutputStream fOut = new FileOutputStream(new File(path));
//        OutputStreamWriter osw = new OutputStreamWriter(fOut);
//
//        osw.write(builder.toString());
//        osw.flush();
//        osw.close();

        writeToFile(basePath, builder.toString());
    }
}
