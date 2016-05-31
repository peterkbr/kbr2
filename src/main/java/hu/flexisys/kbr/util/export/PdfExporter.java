package hu.flexisys.kbr.util.export;

import android.util.Log;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class PdfExporter {

    protected static String tenaz;
    protected static String tarto;
    protected static String biralo;

    protected static Integer margin = 10;
    protected static Integer contentMargin_top = 20;
    protected static Integer contentMargin_bottom = 70;
    protected static Integer contentMarging_horizontal = 20;
    protected static Integer boxMargin_top = 35;
    protected static Integer boxMargin_bottom = 55;

    public static void initPdfExporter(String TENAZ, String TARTO, String BIRALO) {
        tenaz = TENAZ;
        tarto = TARTO;
        biralo = BIRALO;
    }

    public static Font getFont(float size) {
        Font font;
        try {
            BaseFont baseFont = BaseFont.createFont("assets/opensans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font = new Font(baseFont, size);
        } catch (Exception e) {
            Log.e("PdfExporter:getBaseFont", e.getMessage(), e);
            font = new Font();
        }
        return font;
    }

    protected static class HeaderFooter extends PdfPageEventHelper {

        private String title;
        private Phrase footer;
        private int pagenumber;

        public HeaderFooter(String title, String footerValue) {
            this.title = title;
            if (footerValue != null) {
                footer = new Phrase(footerValue, getFont(10f));
            }
            pagenumber = 0;
        }

        public void onStartPage(PdfWriter writer, Document document) {
            ++pagenumber;

            Rectangle content = writer.getBoxSize("art");

            int tableSize = 2;
            PdfPTable table = new PdfPTable(tableSize);
            table.setWidthPercentage(55f);

            table.addCell(getHeaderCell("Tenyészet azonosító:"));
            table.addCell(getHeaderCell(tenaz, true));
            table.addCell(getHeaderCell("Gazdaság neve:"));
            table.addCell(getHeaderCell(tarto));
            table.addCell(getHeaderCell("Bíráló:"));
            table.addCell(getHeaderCell(biralo, true));
            table.setHorizontalAlignment(Element.ALIGN_RIGHT);

            try {
                table.setSpacingAfter(40f);
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            Phrase p = new Phrase("Magyartarka Tenyésztők Egyesülete", getFont(20f));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p, content.getLeft() + 20, content.getTop() - 30, 0);

            p = new Phrase(title, getFont(25f));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p, content.getLeft() + 20, content.getTop() - 65, 0);

            if (footer != null) {
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer, content.getLeft(), content.getBottom() + 25, 0);
            }
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(String.format("- %d -", pagenumber), getFont(12f)),
                    (content.getLeft() + content.getRight()) / 2, content.getBottom(), 0);
        }

        public PdfPCell getHeaderCell(String value, boolean bold) {
            Font font;
            if (bold) {
                font = getFont(14f);
                font.setStyle(Font.BOLD);
            } else {
                font = getFont(12f);
            }
            PdfPCell cell = new PdfPCell(new Phrase(value, font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBorder(Rectangle.NO_BORDER);
            return cell;
        }

        public PdfPCell getHeaderCell(String value) {
            return getHeaderCell(value, false);
        }
    }
}
