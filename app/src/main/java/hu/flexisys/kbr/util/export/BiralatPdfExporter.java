package hu.flexisys.kbr.util.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.util.biralat.BiralatSzempont;
import hu.flexisys.kbr.util.biralat.BiralatSzempontUtil;
import hu.flexisys.kbr.util.biralat.BiralatTipus;
import hu.flexisys.kbr.util.biralat.BiralatTipusUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BiralatPdfExporter extends PdfExporter {

    public static String export(String basePath, String biralatTipus, List<Biralat> biralatList,
                                Map<String, Egyed> egyedMap) throws DocumentException, FileNotFoundException {

        String path = basePath + File.separator + "pdfExport_" +
                DateUtil.formatTimestampFileName(new Date()) + ".pdf";
        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(biralatTipus);
        List<BiralatSzempont> szempontList = new ArrayList<>();
        for (String szempontId : tipus.szempontList) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(szempontId);
            szempontList.add(szempont);
        }

        Rectangle page = PageSize.A4.rotate();
        Document document = new Document(page, contentMarging_horizontal, contentMarging_horizontal,
                contentMargin_top, contentMargin_bottom);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        writer.setBoxSize("art", new Rectangle(page.getLeft() + margin, page.getBottom() + margin,
                page.getRight() - margin, page.getTop() - margin));
        writer.setPageEvent(new HeaderFooter("Szarvasmarha bírálati lap", null));
        document.open();
        document.add(createPdfTable(szempontList, biralatList, egyedMap));
        document.close();
        return path;
    }

    private static PdfPTable createPdfTable(List<BiralatSzempont> szempontList, List<Biralat> biralatList,
                                            Map<String, Egyed> egyedMap) throws DocumentException {
        int tableSize = 7 + szempontList.size();
        PdfPTable table = new PdfPTable(tableSize);
        table.setWidthPercentage(100f);

        table.addCell(getCell("#", true, false));
        table.addCell(getCell("OK"));
        table.addCell(getCell("ENAR"));
        table.addCell(getCell("Dátum"));
        table.addCell(getCell("Ell"));
        table.addCell(getCell("Sz"));
        for (BiralatSzempont szempont : szempontList) {
            table.addCell(getCell(szempont.rovidNev));
        }
        table.addCell(getCell("A", false, true));

        table.setHeaderRows(1);

        int i = 1;
        for (Biralat biralat : biralatList) {
            table.addCell(getCell(String.valueOf(i++), true, false));
            table.addCell(getCell(biralat.getORSKO()));

            String azono = biralat.getAZONO();
            String orsko = egyedMap.get(biralat.getAZONO()).getORSKO();
            if ("HU".equals(orsko) && azono.length() == 10) {
                Font font = getFont(9f);

                Font enarFont = getFont(9f);
                enarFont.setColor(BaseColor.RED);
                enarFont.setStyle(Font.BOLD);

                Phrase phrase = new Phrase();
                phrase.add(new Chunk(azono.substring(0, 5), font));
                phrase.add(new Chunk(" " + azono.substring(5, 9) + " ", enarFont));
                phrase.add(new Chunk(azono.substring(9), font));

                PdfPCell cell = new PdfPCell(phrase);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorderWidthLeft(0L);
                cell.setBorderWidthRight(0L);

                table.addCell(cell);
            } else {
                table.addCell(getCell(azono));
            }

            table.addCell(getCell(DateUtil.formatDate(biralat.getBIRDA())));

            Egyed egyed = egyedMap.get(biralat.getAZONO());
            table.addCell(getCell(String.valueOf(egyed.getELLSO())));
            table.addCell(getCell(String.valueOf(egyed.getSZINE())));
            for (BiralatSzempont szempont : szempontList) {
                table.addCell(getCell(biralat.getErtByKod(szempont.kod)));
            }
            table.addCell(getCell(String.valueOf(biralat.getAKAKO()), false, true));
        }

        float[] columnWidths = new float[tableSize];
        int j = 0;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 100f;
        columnWidths[j++] = 90f;
        columnWidths[j++] = 25f;
        columnWidths[j++] = 29f;
        for (int k = 0; k < szempontList.size(); k++) {
            columnWidths[j++] = 29f;
        }
        columnWidths[j] = 20f;
        table.setWidths(columnWidths);

        return table;
    }

    private static PdfPCell getCell(String value, boolean left, boolean right) {
        PdfPCell cell = new PdfPCell(new Phrase(value, getFont(9f)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        if (!left) {
            cell.setBorderWidthLeft(0L);
        }
        if (!right) {
            cell.setBorderWidthRight(0L);
        }

        return cell;
    }

    private static PdfPCell getCell(String value) {
        return getCell(value, false, false);
    }
}