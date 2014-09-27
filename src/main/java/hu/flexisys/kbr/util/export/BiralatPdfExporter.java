package hu.flexisys.kbr.util.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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

/**
 * 0Created by peter on 31/08/14.
 */
public class BiralatPdfExporter {

    private static String tenaz;
    private static String tarto;
    private static String biralo;

    public static void initBiralatPdfExporter(String TENAZ, String TARTO, String BIRALO) {
        tenaz = TENAZ;
        tarto = TARTO;
        biralo = BIRALO;
    }

    public static void export(String basePath, String biralatTipus, List<Biralat> biralatList,
                              Map<String, Egyed> egyedMap) throws DocumentException, FileNotFoundException {
        String path = basePath + File.separator + "pdfExport_" + DateUtil.formatTimestampFileName(new Date()) + ".pdf";
        BiralatTipus tipus = BiralatTipusUtil.getBiralatTipus(biralatTipus);
        List<BiralatSzempont> szempontList = new ArrayList<BiralatSzempont>();
        for (String szempontId : tipus.szempontList) {
            BiralatSzempont szempont = BiralatSzempontUtil.getBiralatSzempont(szempontId);
            szempontList.add(szempont);
        }

        Integer margin = 10;
        Integer contentMarging_vertical = 65;
        Integer contentMarging_horizontal = 20;
        Rectangle page = PageSize.A4.rotate();
        Document document = new Document(page, contentMarging_horizontal, contentMarging_horizontal, contentMarging_vertical, contentMarging_vertical);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        writer.setBoxSize("art", new Rectangle(page.getLeft() + margin, page.getBottom() + margin, page.getRight() - margin, page.getTop() - margin));
        writer.setPageEvent(new HeaderFooter());
        document.open();
        document.add(createPdfTable(szempontList, biralatList, egyedMap));
        document.close();
    }

    private static PdfPTable createPdfTable(List<BiralatSzempont> szempontList, List<Biralat> biralatList, Map<String, Egyed> egyedMap)
            throws DocumentException {
        int tableSize = 8 + szempontList.size();
        PdfPTable table = new PdfPTable(tableSize);
        table.setWidthPercentage(100f);

        // t.setBorderColor(BaseColor.GRAY);
        // t.setPadding(4);
        // t.setSpacing(4);
        // t.setBorderWidth(1);

        // header
        table.addCell(getCell("#"));
        table.addCell(getCell("OK"));
        table.addCell(getCell("ENAR"));
        table.addCell(getCell("Telep"));
        table.addCell(getCell("Dátum"));
        table.addCell(getCell("Ell"));
        table.addCell(getCell("Sz"));
        for (BiralatSzempont szempont : szempontList) {
            table.addCell(getCell(szempont.rovidNev));
        }
        table.addCell(getCell("A"));

        table.setHeaderRows(1);

        // values
        int i = 1;
        for (Biralat biralat : biralatList) {
            table.addCell(getCell(String.valueOf(i++)));
            table.addCell(getCell(biralat.getORSKO()));

            String azono = biralat.getAZONO();
            String orsko = egyedMap.get(biralat.getAZONO()).getORSKO();
            if ("HU".equals(orsko) && azono.length() == 10) {
                Font font = new Font();
                font.setSize(9f);

                Font enarFont = new Font();
                enarFont.setColor(BaseColor.RED);
                enarFont.setSize(9f);

//                Phrase p1 = new Phrase(azono.substring(0, 5), font);
//                Phrase p2 = new Phrase(azono.substring(5, 9), enarFont);
//                Phrase p3 = new Phrase(azono.substring(9), font);

                Phrase phrase = new Phrase();
                phrase.add(new Chunk(azono.substring(0, 5), font));
                phrase.add(new Chunk(azono.substring(5, 9), enarFont));
                phrase.add(new Chunk(azono.substring(9), font));

//                PdfPRow row = new PdfPRow(new PdfPCell[]{new PdfPCell(p1), new PdfPCell(p2), new PdfPCell(p3)});
//                cell.addElement(p1);
//                cell.addElement(p2);
//                cell.addElement(p3);

                PdfPCell cell = new PdfPCell(phrase);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            } else {
                table.addCell(getCell(azono));
            }

            table.addCell(getCell(biralat.getTENAZ()));
            table.addCell(getCell(DateUtil.formatDate(biralat.getBIRDA())));
            
            Egyed egyed = egyedMap.get(biralat.getAZONO());
            table.addCell(getCell(String.valueOf(egyed.getELLSO())));
            table.addCell(getCell(String.valueOf(egyed.getSZINE())));
            for (BiralatSzempont szempont : szempontList) {
                table.addCell(getCell(biralat.getErtByKod(szempont.kod)));
            }
            table.addCell(getCell(String.valueOf(biralat.getAKAKO())));
        }

        float[] columnWidths = new float[tableSize];
        int j = 0;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 100f;
        columnWidths[j++] = 75f;
        columnWidths[j++] = 90f;
        columnWidths[j++] = 25f;
        columnWidths[j++] = 90f;
        for (int k = 0; k < szempontList.size(); k++) {
            columnWidths[j++] = 29f;
        }
        columnWidths[j++] = 20f;
        table.setWidths(columnWidths);

        return table;
    }

    private static PdfPCell getCell(String value) {
        Font font = new Font();
        font.setSize(9f);
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static class HeaderFooter extends PdfPageEventHelper {
        private Phrase header;
        private Phrase footer;
        private int pagenumber;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            String headerText = tenaz + " - " + tarto + ", " + biralo;
            header = new Phrase(headerText);
            footer = new Phrase("A bírálati adatokat tartalmazó listát átvettem:.......................................................P.H.");
            pagenumber = 0;
        }

        public void onStartPage(PdfWriter writer, Document document) {
            ++pagenumber;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            Integer boxMargin_top = 35;
            Integer boxMargin_bottom = 50;
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle content = writer.getBoxSize("art");

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, header, content.getLeft(), content.getTop() - 20, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer, content.getLeft(), content.getBottom() + 25, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(String.format("- %d -", pagenumber)),
                    (content.getLeft() + content.getRight()) / 2, content.getBottom(), 0);

            Rectangle box = new Rectangle(content.getLeft(), content.getBottom() + boxMargin_bottom, content.getRight(), content.getTop() - boxMargin_top);
            box.setBorder(Rectangle.BOX);
            box.setBorderWidth(1);
            canvas.rectangle(box);
        }
    }
}
