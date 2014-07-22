package hu.flexisys.kbr.controller.db;

import android.os.AsyncTask;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.view.ProgressHandler;

/**
 * Created by Peter on 2014.07.09..
 */
public class RemoveTenyeszetArrayTask extends AsyncTask<Object, Void, Void> {

    private KbrApplication app;
    private ProgressHandler progressHandler;

    public RemoveTenyeszetArrayTask(KbrApplication app, ProgressHandler progressHandler) {
        this.app = app;
        this.progressHandler = progressHandler;
    }

    @Override
    protected Void doInBackground(Object... params) {
        for (Object param : params) {
            app.deleteTenyeszet((String) param);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressHandler.onProgressEnded();
    }
}
