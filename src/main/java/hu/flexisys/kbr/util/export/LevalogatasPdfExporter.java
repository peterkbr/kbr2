package hu.flexisys.kbr.util.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
public class LevalogatasPdfExporter extends PdfExporter {

    public static String export(String basePath, List<Egyed> selectedEgyedList) throws DocumentException, FileNotFoundException {
        String path = basePath + File.separator + "pdfExport_" + DateUtil.formatTimestampFileName(new Date()) + ".pdf";
        Rectangle page = PageSize.A4.rotate();
        Document document = new Document(page, contentMarging_horizontal, contentMarging_horizontal, 2 * margin, contentMargin_bottom);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
        writer.setBoxSize("art", new Rectangle(page.getLeft() + margin, page.getBottom() + margin, page.getRight() - margin, page.getTop() - margin));
        boxMargin_bottom = 35;
        contentMargin_bottom = 50;
        writer.setPageEvent(new HeaderFooter("Bírálatra leválogatott egyedek", null));
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
                Font font = getFont(12f);

                // enar szám
                Font enarFont = getFont(12f);
                enarFont.setColor(BaseColor.RED);
                enarFont.setStyle(Font.BOLD);

                Phrase phrase = new Phrase();
                phrase.add(new Chunk(azono.substring(0, 5), font));
                phrase.add(new Chunk(" " + azono.substring(5, 9) + " ", enarFont));
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
        Font font = getFont(12f);
        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

}
