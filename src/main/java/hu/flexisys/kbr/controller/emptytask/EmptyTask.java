package hu.flexisys.kbr.controller.emptytask;

import android.os.AsyncTask;

/**
 * Created by Peter on 2014.07.21..
 */
public class EmptyTask extends AsyncTask<Void, Void, Void> {

    private PreExecutable preExecutable;
    private Executable executable;
    private ExecutableFinishedListener listener;
    private ExecutableErrorListener errorListener;
    private Exception exception;

    public EmptyTask(Executable executable) {
        this(executable, null, null);
    }

    public EmptyTask(Executable executable, ExecutableFinishedListener listener) {
        this(executable, listener, null);
    }

    public EmptyTask(PreExecutable preExecutable, Executable executable, ExecutableFinishedListener listener) {
        this(executable, listener, null);
        this.preExecutable = preExecutable;
    }

    public EmptyTask(Executable executable, ExecutableFinishedListener listener, ExecutableErrorListener errorListener) {
        this.executable = executable;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected void onPreExecute() {
        if (preExecutable != null) {
            preExecutable.preExecute();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            executable.execute();
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (exception != null) {
            errorListener.onError(exception);
        } else if (listener != null) {
            listener.onFinished();
        }
    }

}
