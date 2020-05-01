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

public class PdfExporter {

    private static BaseFont baseFont;

    private static String tenaz;
    private static String tarto;
    private static String biralo;

    static Integer margin = 10;
    static Integer contentMargin_top = 20;
    static Integer contentMargin_bottom = 70;
    static Integer contentMarging_horizontal = 20;
    static Integer boxMargin_bottom = 55;

    public static void initPdfExporter(String TENAZ, String TARTO, String BIRALO, Context context) {
        tenaz = TENAZ;
        tarto = TARTO;
        biralo = BIRALO;

        baseFont = buildBaseFont(context);
    }

    private static BaseFont buildBaseFont(Context context) {
        try (InputStream inputStream = context.getResources().openRawResource(R.raw.opensans)) {
            File fontFile = File.createTempFile("font_", ".ttf");
            FileOutputStream outputStream = new FileOutputStream(fontFile);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            outputStream.write(buffer);
            outputStream.close();
            return BaseFont.createFont(fontFile.getAbsolutePath(),
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException e) {
            Log.e("PdfExporter", "loading font file failed", e);
        } catch (DocumentException e) {
            Log.e("PdfExporter", "building baseFont failed", e);
        }
        return null;
    }

    static Font getFont(float size) {
        if (baseFont == null) {
            Font font = new Font();
            font.setSize(size);
            return font;
        }
        return new Font(baseFont, size);
    }

    protected static class HeaderFooter extends PdfPageEventHelper {

        private String title;
        private Phrase footer;
        private int pagenumber;

        HeaderFooter(String title, String footerValue) {
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

            Phrase p = new Phrase("Holstein-fríz Tenyésztők Egyesülete", getFont(20f));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p,
                    content.getLeft() + 20, content.getTop() - 30, 0);

            p = new Phrase(title, getFont(25f));
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, p,
                    content.getLeft() + 20, content.getTop() - 65, 0);

            if (footer != null) {
                ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer,
                        content.getLeft(), content.getBottom() + 25, 0);
            }
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(String.format("- %d -", pagenumber), getFont(12f)),
                    (content.getLeft() + content.getRight()) / 2, content.getBottom(), 0);
        }

        PdfPCell getHeaderCell(String value, boolean bold) {
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

        PdfPCell getHeaderCell(String value) {
            return getHeaderCell(value, false);
        }
    }
}
