package hu.flexisys.kbr.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import hu.flexisys.kbr.controller.KbrApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by peter on 06/09/14.
 */
public class EmailUtil {

    private static final String TAG = "KBR2_EmailUtil";
    private static KbrApplication app;

    public static void initEmailUtil(KbrApplication app) {
        EmailUtil.app = app;
    }

    public static void sendMailWithAttachments(String[] addresses, String subject, String content, List<String> pathList) {
        Context context = app.getApplicationContext();
        if (context == null) {
            Log.e(TAG, "EmailUtil got no context from KbrApplication");
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("message/rfc822");

        if (addresses != null && addresses.length > 0) {
            intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        }
        if (subject != null && !subject.isEmpty()) {
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        }
        if (content != null && !content.isEmpty()) {
            intent.putExtra(Intent.EXTRA_TEXT, content);
        }

        if (pathList != null && !pathList.isEmpty()) {
            ArrayList<Uri> uris = new ArrayList<Uri>();
            for (String path : pathList) {
                File fileIn = new File(path);
                Uri u = Uri.fromFile(fileIn);
                uris.add(u);
            }
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        }

        Intent mailer = Intent.createChooser(intent, null);
        app.geActivityContext().startActivity(mailer);
    }
}
