package hu.flexisys.kbr.controller.network.tenyeszet;

import android.os.AsyncTask;
import android.util.Log;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.util.KbrApplicationUtil;
import hu.flexisys.kbr.util.NetworkUtil;
import hu.flexisys.kbr.util.XmlUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by Peter on 2014.07.02..
 */
public class TenyeszetLoader extends AsyncTask<String, Void, Tenyeszet> {

    private TenyeszetLoaderResponseListener onResponseListener;

    public TenyeszetLoader(TenyeszetLoaderResponseListener onResponseListener) {
        this.onResponseListener = onResponseListener;
    }

    @Override
    protected Tenyeszet doInBackground(String... params) {
        Tenyeszet tenyeszet = null;
        String userid = params[0];
        for (int i = 1; i < params.length; i++) {
            String tenaz = params[i];
            String requestXml = NetworkUtil.getKullemtenyRequestBody(userid, tenaz);
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost post = new HttpPost(KbrApplicationUtil.getServerUrl());
            String responseValue = null;
            try {
                post.setEntity(new StringEntity(requestXml));
                HttpResponse response = httpclient.execute(post);
                responseValue = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                Log.d("TenyeszetLoader", "accessing network", e);
            }
            if (responseValue != null && !responseValue.isEmpty()) {
                try {
                    tenyeszet = XmlUtil.parseKullemtenyXml(responseValue);
                } catch (Exception e) {
                    Log.d("TenyeszetLoader", "parsing xml", e);
                }
            }
        }
        return tenyeszet;
    }

    @Override
    protected void onPostExecute(Tenyeszet tenyeszet) {
        super.onPostExecute(tenyeszet);
        onResponseListener.onTenyeszetLoaderResponse(tenyeszet);
    }
}
