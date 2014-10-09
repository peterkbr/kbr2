package hu.flexisys.kbr.controller.network.tenyeszet;

import android.os.AsyncTask;
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

/**
 * Created by Peter on 2014.07.02..
 */
public class DownloadTenyeszetArrayTask extends AsyncTask<Object, Void, Void> {

    private final DownloadTenyeszetHandler downloadTenyeszetHandler;
    private HashMap<String, String> resultMap;
    private KbrApplication app;

    public DownloadTenyeszetArrayTask(KbrApplication app, DownloadTenyeszetHandler downloadTenyeszetHandler) {
        this.app = app;
        resultMap = new HashMap<String, String>();
        this.downloadTenyeszetHandler = downloadTenyeszetHandler;
    }

    @Override
    protected Void doInBackground(Object... params) {
        for (Object param : params) {
            String TENAZ = (String) param;
            Log.i(LogUtil.TAG, "DOWNLOAD STARTED:" + TENAZ + ":" + DateUtil.getRequestId());
            String requestXml = NetworkUtil.getKullemtenyRequestBody(app.getBiraloUserId(), TENAZ);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(KbrApplicationUtil.getServerUrl());
            String responseValue = null;
            try {
                post.setEntity(new StringEntity(requestXml));
                HttpResponse response = httpclient.execute(post);
                responseValue = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                Log.e(LogUtil.TAG, "accessing network", e);
            }
            if (responseValue != null && !responseValue.isEmpty()) {
                try {
                    Log.i(LogUtil.TAG, "DOWNLOADED:" + TENAZ + ":" + DateUtil.getRequestId());
                    Tenyeszet tenyeszet = XmlUtil.parseKullemtenyXml(responseValue);
                    Log.i(LogUtil.TAG, "PARSED:" + TENAZ + ":" + DateUtil.getRequestId());
                    app.deleteTenyeszet(tenyeszet.getTENAZ());
                    Log.i(LogUtil.TAG, "OLD TENYESZET DATA REMOVED:" + TENAZ + ":" + DateUtil.getRequestId());
                    app.insertTenyeszetWithChildren(tenyeszet);
                    Log.i(LogUtil.TAG, "INSERTED:" + TENAZ + ":" + DateUtil.getRequestId());
                    resultMap.put(TENAZ, "Sikeres letöltés");
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
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        downloadTenyeszetHandler.onDownloadFinished(resultMap);
    }
}
