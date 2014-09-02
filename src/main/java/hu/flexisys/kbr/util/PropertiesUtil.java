package hu.flexisys.kbr.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * Created by peter on 02/09/14.
 */
public class PropertiesUtil {

    public static Properties loadProperties(Context context, int rawResId) throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = context.getResources().openRawResource(rawResId);
        InputStreamReader inputreader = new InputStreamReader(inputStream, "UTF-8");
        properties.load(inputreader);
        return properties;
    }
}
