package hu.flexisys.kbr.controller.network.tenyeszet;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;

import static hu.flexisys.kbr.controller.KbrApplication.DbCheckType.FULL;

/**
 * Created by peter on 10/02/15.
 */
public class DownloadTenyeszetArrayService extends IntentService {

    public static final String NAME = "DownloadTenyeszetArrayService";

    public static final String TENAZ_LIST_KEY = "TENAZ_LIST_KEY";
    public static final String RESULT_MAP_KEY = "RESULT_MAP_KEY";
    public static final String BROADCAST_ACTION = "hu.flexisys.kbr.downloadtenyeszetarrayservice.finished.BROADCAST";

    private KbrApplication app;
    private HashMap<String, String> resultMap;
    private String[] tenazArray;

    public DownloadTenyeszetArrayService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        app = (KbrApplication) getApplication();

        String tenazListString = intent.getStringExtra(TENAZ_LIST_KEY);
        tenazArray = tenazListString.split(",");

        resultMap = new HashMap<String, String>();
        startDownload();
        onFinish();
    }

    private void startDownload() {
        for (String TENAZ : tenazArray) {
            Log.i(LogUtil.TAG, "DOWNLOAD STARTED:" + TENAZ + ":" + DateUtil.getRequestId());
            String requestXml = NetworkUtil.getKullemtenyRequestBody(app.getBiraloUserId(), TENAZ);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(KbrApplicationUtil.getServerUrl());
            try {
                post.setEntity(new StringEntity(requestXml));
                HttpResponse response = httpclient.execute(post);
                String responseValue = EntityUtils.toString(response.getEntity());
                if (responseValue != null && !responseValue.isEmpty()) {
                    Log.i(LogUtil.TAG, "DOWNLOADED:" + TENAZ + ":" + DateUtil.getRequestId());
                    Tenyeszet tenyeszet = XmlUtil.parseKullemtenyXml(responseValue);
                    Log.i(LogUtil.TAG, "PARSED:" + TENAZ + ":" + DateUtil.getRequestId());
                    app.deleteTenyeszet(tenyeszet.getTENAZ());
                    Log.i(LogUtil.TAG, "OLD TENYESZET DATA REMOVED:" + TENAZ + ":" + DateUtil.getRequestId());
                    app.insertTenyeszetWithChildren(tenyeszet);
                    Log.i(LogUtil.TAG, "INSERTED:" + TENAZ + ":" + DateUtil.getRequestId());
                    resultMap.put(TENAZ, "Sikeres letöltés");
                }
            } catch (IOException e) {
                Log.e(LogUtil.TAG, "accessing network", e);
                resultMap.put(TENAZ, "Sikertelen letöltés : \n" + "Hiba az internet hozzáférés során!");
            } catch (XmlUtilException xmlE) {
                app.updateTenyeszetByTENAZWithERVENYES(TENAZ, false);
                resultMap.put(TENAZ, "Sikertelen letöltés : \n" + xmlE.getMessage());
                Log.e(LogUtil.TAG, "parsing xml", xmlE);
            } catch (Exception e) {
                app.updateTenyeszetByTENAZWithERVENYES(TENAZ, false);
                resultMap.put(TENAZ, "Sikertelen letöltés");
                Log.e(LogUtil.TAG, "parsing xml", e);
            }
        }
        app.checkDbConsistency(FULL);
    }

    private void onFinish() {
        Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(RESULT_MAP_KEY, resultMap);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
