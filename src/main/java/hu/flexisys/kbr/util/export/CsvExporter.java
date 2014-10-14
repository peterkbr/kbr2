package hu.flexisys.kbr.util.export;

import hu.flexisys.kbr.util.DateUtil;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * Created by peter on 07/10/14.
 */
public class CsvExporter {


    protected static String csvSeparator = ";";

    public static String writeToFile(String basePath, String content) throws IOException {
        String path = basePath + File.separator + "csvExport_" + DateUtil.formatTimestampFileName(new Date()) + ".csv";
        OutputStream os = new FileOutputStream(path);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os, Charset.forName("UTF-8").newEncoder());
        outputStreamWriter.write(content);
        outputStreamWriter.flush();
        outputStreamWriter.close();
        return path;
    }
}
