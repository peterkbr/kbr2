package hu.flexisys.kbr.util.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 01/09/14.
 */
public class LevalogatasPdfExporter {

    private static String tenaz;
    private static String tarto;
    private static String biralo;

    public static void initLevalogatasPdfExporter(String TENAZ, String TARTO, String BIRALO) {
        tenaz = TENAZ;
        tarto = TARTO;
        biralo = BIRALO;
    }

    public static String export(String basePath, List<Egyed> selectedEgyedList) throws DocumentException, FileNotFoundException {
        String path = basePath + File.separator + "pdfExport_" + DateUtil.formatTimestampFileName(new Date()) + ".pdf";
        Integer margin = 10;
        Integer contentMarging_top = 40;
        Integer contentMarging_bottom = 30;
        Integer contentMarging_horizontal = 20;
        Rectangle page = PageSize.A4.rotate();
        Document document = new Document(page, contentMarging_horizontal, contentMarging_horizontal, contentMarging_top, contentMarging_bottom);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        writer.setBoxSize("art", new Rectangle(page.getLeft() + margin, page.getBottom() + margin, page.getRight() - margin, page.getTop() - margin));
        writer.setPageEvent(new HeaderFooter());
        document.open();
        document.add(createPdfTable(selectedEgyedList));
        document.close();
        return path;
    }

    private static PdfPTable createPdfTable(List<Egyed> selectedEgyedList) throws DocumentException {
        int tableSize = 10;
        PdfPTable table = new PdfPTable(tableSize);
//        table.setWidthPercentage(100f);

        // table.setBorderColor(BaseColor.GRAY);
        // table.setPadding(4);
        // table.setSpacing(4);
        // table.setBorderWidth(1);

        // header
        table.addCell(getCell("#"));
        table.addCell(getCell("OK"));
        table.addCell(getCell("ENAR"));
        table.addCell(getCell("Haszn"));
        table.addCell(getCell("ES"));
        table.addCell(getCell("Ellés dátum"));
        table.addCell(getCell("Született"));
        table.addCell(getCell("Konstr"));
        table.addCell(getCell("ITV"));
        table.addCell(getCell("FK"));

        table.setHeaderRows(1);

        for (int i = 0; i < selectedEgyedList.size(); i++) {
            table.addCell(getCell(String.valueOf(i + 1)));
            Egyed egyed = selectedEgyedList.get(i);
            table.addCell(getCell(egyed.getORSKO()));

            String azono = egyed.getAZONO();
            String orsko = egyed.getORSKO();
            if ("HU".equals(orsko) && azono.length() == 10) {
                // enar szám
                Font font = new Font();
                font.setSize(12f);

                Font enarFont = new Font();
                enarFont.setColor(BaseColor.RED);
                enarFont.setSize(12f);

                Phrase phrase = new Phrase();
                phrase.add(new Chunk(azono.substring(0, 5), font));
                phrase.add(new Chunk(azono.substring(5, 9), enarFont));
                phrase.add(new Chunk(azono.substring(9), font));

                PdfPCell cell = new PdfPCell(phrase);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);

                // használati szám
                cell = new PdfPCell(new Phrase(azono.substring(5, 9), font));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cell);
            } else {
                table.addCell(getCell(azono));
                table.addCell(getCell(""));
            }

            table.addCell(getCell(String.valueOf(egyed.getELLSO())));
            table.addCell(getCell(DateUtil.formatDate(egyed.getELLDA())));
            table.addCell(getCell(DateUtil.formatDate(egyed.getSZULD())));
            table.addCell(getCell(String.valueOf(egyed.getKONSK())));
            table.addCell(getCell(egyed.getITVJE() ? "i" : "n"));
            table.addCell(getCell(String.valueOf(egyed.getFAJKO())));
        }


        float[] columnWidths = new float[tableSize];
        int j = 0;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 90f;
        columnWidths[j++] = 40f;
        columnWidths[j++] = 30f;
        columnWidths[j++] = 80f;
        columnWidths[j++] = 80f;
        columnWidths[j++] = 40f;
        columnWidths[j++] = 20f;
        columnWidths[j++] = 20f;
        table.setWidths(columnWidths);

        return table;
    }


    private static PdfPCell getCell(String value) {
        Font font = new Font();
        font.setSize(12f);
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static class HeaderFooter extends PdfPageEventHelper {
        private Phrase header;
        private int pagenumber;

        @Override
        public void onOpenDocument(PdfWriter writer, Document document) {
            String headerText = tenaz + " - " + tarto + ", " + biralo;
            header = new Phrase(headerText);
            pagenumber = 0;
        }

        public void onStartPage(PdfWriter writer, Document document) {
            ++pagenumber;
        }

        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle content = writer.getBoxSize("art");
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, header, content.getLeft(), content.getTop() - 20, 0);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(String.format("- %d -", pagenumber)),
                    (content.getLeft() + content.getRight()) / 2, content.getBottom(), 0);
        }
    }
}
