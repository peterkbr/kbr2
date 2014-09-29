package hu.flexisys.kbr.util.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Created by peter on 29/09/14.
 */
public class PdfExporter {

    protected static String tenaz;
    protected static String tarto;
    protected static String biralo;

    public static void initPdfExporter(String TENAZ, String TARTO, String BIRALO) {
        tenaz = TENAZ;
        tarto = TARTO;
        biralo = BIRALO;
    }


    protected static class HeaderFooter extends PdfPageEventHelper {

        private String title;
        private Phrase footer;
        private int pagenumber;

        public HeaderFooter(String title, String footerValue) {
            this.title = title;
            if (footerValue != null) {
                footer = new Phrase(footerValue);
            }
            pagenumber = 0;
        }

        public void onStartPage(PdfWriter writer, Document document) {
            ++pagenumber;

            Integer boxMargin_top = 35;
            Integer boxMargin_bottom = 50;
            PdfContentByte canvas = writer.getDirectContent();
            Rectangle content = writer.getBoxSize("art");

            int tableSize = 2;
            PdfPTable table = new PdfPTable(tableSize);
            table.setWidthPercentage(55f);

            table.addCell(getHeaderCell("Tenyészet azonosító:"));
            table.addCell(getHeaderCell(tenaz));
            table.addCell(getHeaderCell("Gazdaság neve:"));
            table.addCell(getHeaderCell(tarto));
            table.addCell(getHeaderCell("Bíráló:"));
            table.addCell(getHeaderCell(biralo));
            table.setHorizontalAlignment(Element.ALIGN_RIGHT);

            try {
                table.setSpacingAfter(40f);
                document.add(table);
            } catch (DocumentException e) {
                e.printStackTrace();
            }

            Font font = new Font();
            font.setSize(20f);
            Phrase p = new Phrase("Holstein-fríz Tenyésztők Egyesülete", font);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p, content.getLeft() + 20, content.getTop() - 30, 0);

            font.setSize(25f);
            p = new Phrase(title, font);
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p, content.getLeft() + 20, content.getTop() - 65, 0);

            if (footer != null) {
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer, content.getLeft(), content.getBottom() + 25, 0);
            }
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(String.format("- %d -", pagenumber)),
                    (content.getLeft() + content.getRight()) / 2, content.getBottom(), 0);


            Rectangle box = new Rectangle(content.getLeft(), content.getBottom() + boxMargin_bottom, content.getRight(),
                    content.getTop() - boxMargin_top - table.getTotalHeight());
            box.setBorder(Rectangle.BOX);
            box.setBorderWidth(1);
            canvas.rectangle(box);
        }

        public PdfPCell getHeaderCell(String value) {
            Font font = new Font();
            font.setSize(12f);
            PdfPCell cell = new PdfPCell(new Phrase(value, font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            return cell;

        }
    }

}
