package hu.flexisys.kbr.util.export;

import android.content.Context;
import android.util.Log;

import hu.flexisys.kbr.R;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by peter on 29/09/14.
 */
public class PdfExporter {

    private static File fontFile;

    protected static String tenaz;
    protected static String tarto;
    protected static String biralo;

    protected static Integer margin = 10;
    protected static Integer contentMargin_top = 20;
    protected static Integer contentMargin_bottom = 70;
    protected static Integer contentMarging_horizontal = 20;
    protected static Integer boxMargin_top = 35;
    protected static Integer boxMargin_bottom = 55;

    public static void initPdfExporter(String TENAZ, String TARTO, String BIRALO, Context context) {
        tenaz = TENAZ;
        tarto = TARTO;
        biralo = BIRALO;

        try (InputStream inputStream = context.getResources().openRawResource(R.raw.opensans)) {
            fontFile = File.createTempFile("font_", ".ttf");
            FileOutputStream outputStream = new FileOutputStream(fontFile);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            outputStream.write(buffer);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Font getFont(float size) {
        Font font;
        try {
            BaseFont baseFont = BaseFont.createFont(fontFile.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font = new Font(baseFont, size);
        } catch (Exception e) {
            Log.e("PdfExporter : getBaseFont", e.getMessage(), e);
            font = new Font();
            font.setSize(size);
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

//            PdfContentByte canvas = writer.getDirectContent();
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

            Phrase p = new Phrase("Holstein-fríz Tenyésztők Egyesülete", getFont(20f));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p, content.getLeft() + 20, content.getTop() - 30, 0);

            p = new Phrase(title, getFont(25f));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p, content.getLeft() + 20, content.getTop() - 65, 0);

            if (footer != null) {
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer, content.getLeft(), content.getBottom() + 25, 0);
            }
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, new Phrase(String.format("- %d -", pagenumber), getFont(12f)),
                    (content.getLeft() + content.getRight()) / 2, content.getBottom(), 0);


//            Rectangle box = new Rectangle(content.getLeft(), content.getBottom() + boxMargin_bottom, content.getRight(),
//                    content.getTop() - boxMargin_top - table.getTotalHeight());
//            box.setBorder(Rectangle.BOX);
//            box.setBorderWidth(1);
//            canvas.rectangle(box);
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
