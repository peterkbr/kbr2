package hu.flexisys.kbr.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.widget.Toast;
import hu.flexisys.kbr.controller.KbrApplication;

/**
 * Created by Peter on 2014.07.04..
 */
public class KbrActivity extends ActionBarActivity implements ProgressHandler {

    protected KbrApplication app;
    protected ActionBar actionBar;
    protected KbrDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        app = (KbrApplication) getApplication();
        actionBar = getSupportActionBar();
    }

    public FragmentTransaction getFragmentTransactionWithTag(String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(tag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        return ft;
    }

    protected void dismissDialog() {
        if (dialog != null && dialog.isVisible()) {
            dialog.dismiss();
        }
    }

    // PROGRESS

    protected void startProgressDialog() {
        FragmentTransaction ft = getFragmentTransactionWithTag("progress");
        dialog = ProgressDialog.newInstance();
        dialog.show(ft, "progress");
    }

    @Override
    public void onProgressEnded() {
        dismissDialog();
    }

    // MESSAGING

    protected void toast(int msgId) {
        Toast.makeText(this, getString(msgId), Toast.LENGTH_SHORT).show();
    }

    protected void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}