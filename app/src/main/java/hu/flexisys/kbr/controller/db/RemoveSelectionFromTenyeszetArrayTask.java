package hu.flexisys.kbr.controller.db;

import android.os.AsyncTask;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.view.ProgressHandler;

import static hu.flexisys.kbr.controller.KbrApplication.DbCheckType.EGYED;

/**
 * Created by Peter on 2014.07.09..
 */
public class RemoveSelectionFromTenyeszetArrayTask extends AsyncTask<Object, Void, Void> {

    private KbrApplication app;
    private ProgressHandler progressHandler;

    public RemoveSelectionFromTenyeszetArrayTask(KbrApplication app, ProgressHandler progressHandler) {
        this.app = app;
        this.progressHandler = progressHandler;
    }

    @Override
    protected Void doInBackground(Object... params) {
        for (Object param : params) {
            app.removeSelectionFromTenyeszet((String) param);
        }
        app.checkDbConsistency(EGYED);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressHandler.onProgressEnded();
    }
}
