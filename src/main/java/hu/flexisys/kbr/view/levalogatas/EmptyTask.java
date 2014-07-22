package hu.flexisys.kbr.view.levalogatas;

import android.os.AsyncTask;

/**
 * Created by Peter on 2014.07.21..
 */
public class EmptyTask extends AsyncTask<Void, Void, Void> {

    private Executable executable;
    private ExecutableFinishedListener listener;

    public EmptyTask(Executable executable, ExecutableFinishedListener listener) {
        this.executable = executable;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        executable.execute();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFinished();
    }
}
