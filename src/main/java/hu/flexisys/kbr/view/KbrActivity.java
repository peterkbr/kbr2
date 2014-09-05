package hu.flexisys.kbr.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.util.SoundUtil;
import hu.flexisys.kbr.view.levalogatas.DatePickedListener;
import hu.flexisys.kbr.view.levalogatas.KbrDatePickerDialog;

import java.util.Calendar;

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

    public void pickDate(final View view) {
        final TextView dateEditText = (TextView) view;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        FragmentTransaction ft = getFragmentTransactionWithTag("datePickerDialog");
        dialog = KbrDatePickerDialog.newInstance(new DatePickedListener() {
            @Override
            public void onClear() {
                dateEditText.setText("");
                dismissDialog();
            }

            @Override
            public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
                dateEditText.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                dismissDialog();
            }
        }, mYear, mMonth, mDay);
        dialog.show(ft, "datePickerDialog");

    }

    // PROGRESS

    public void startProgressDialog(String title) {
        FragmentTransaction ft = getFragmentTransactionWithTag("progress");
        dialog = ProgressDialog.newInstance(title);
        dialog.show(ft, "progress");
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isVisible()) {
            dialog.dismiss();
        }
    }

    @Override
    public void onProgressEnded() {
        dismissDialog();
    }

    // MESSAGING

    public void toast(int msgId) {
        Toast.makeText(this, getString(msgId), Toast.LENGTH_SHORT).show();
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void errorBeep() {
        SoundUtil.errorBeep();
    }
}
