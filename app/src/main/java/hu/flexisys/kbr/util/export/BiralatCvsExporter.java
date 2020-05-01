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

public class BiralatCvsExporter extends CsvExporter {

    public static String export(String basePath, String biralatTipus, List<Biralat> biralatList,
                                Map<String, Egyed> egyedMap) throws IOException {

        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(biralatTipus);
        List<BiralatSzempont> szempontList = new ArrayList<>();
        for (String szempontId : tipus.szempontList) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(szempontId);
            szempontList.add(szempont);
        }

        StringBuilder builder = new StringBuilder();

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

        int i = 1;
        for (Biralat biralat : biralatList) {
            builder.append(i++);
            builder.append(csvSeparator).append(biralat.getORSKO());
            builder.append(csvSeparator).append(biralat.getAZONO());
            builder.append(csvSeparator).append(biralat.getTENAZ());
            builder.append(csvSeparator).append(DateUtil.formatDate(biralat.getBIRDA()));

            Egyed egyed = egyedMap.get(biralat.getAZONO());
            builder.append(csvSeparator).append(egyed.getELLSO());
            builder.append(csvSeparator).append(egyed.getSZINE());
            for (BiralatSzempont szempont : szempontList) {
                builder.append(csvSeparator).append(biralat.getErtByKod(szempont.kod));
            }
            builder.append(csvSeparator).append(biralat.getAKAKO());
            builder.append("\n");
        }
        return writeToFile(basePath, builder.toString());
    }
}
