package hu.flexisys.kbr.view;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.KbrApplication;
import hu.flexisys.kbr.util.SoundUtil;
import hu.flexisys.kbr.view.levalogatas.DatePickedListener;
import hu.flexisys.kbr.view.levalogatas.KbrDatePickerDialog;

import java.util.Calendar;

public class KbrActivity extends AppCompatActivity implements ProgressHandler {

    protected KbrApplication app;
    protected ActionBar actionBar;
    protected KbrDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (KbrApplication) getApplication();
        app.setCurrentActivity(this);

        if (KbrApplication.errorOnInit != null) {
            FragmentTransaction ft = getFragmentTransactionWithTag("error");
            String[] arr = KbrApplication.errorOnInit.split(";");
            dialog = NotificationDialog.newInstance(arr[0], arr[1], new NotificationDialog.OkListener() {
                @Override
                public void onOkClicked() {
                    finish();
                }
            });
            dialog.show(ft, "error");
        }
    }

    protected void setUpTabBar() {
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        app.activityDestroyed();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
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

        int mYear;
        int mMonth;
        int mDay;

        String dateString = dateEditText.getText().toString();
        String[] dateStringArray = dateString.split("\\.");
        if (dateStringArray.length == 3) {
            mYear = Integer.parseInt(dateStringArray[0]);
            mMonth = Integer.parseInt(dateStringArray[1]) - 1;
            mDay = Integer.parseInt(dateStringArray[2]);
        } else {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        FragmentTransaction ft = getFragmentTransactionWithTag("datePickerDialog");
        dialog = KbrDatePickerDialog.newInstance(getTitleForDatePickerDialog(view.getId()),
                new DatePickedListener() {
                    @Override
                    public void onClear() {
                        dateEditText.setText("");
                        dismissDialog();
                    }

                    @Override
                    public void onDatePicked(int year, int monthOfYear, int dayOfMonth) {
                        dateEditText.setText(year + "." + getNulledString(monthOfYear + 1) + "." +
                                getNulledString(dayOfMonth));
                        dismissDialog();
                    }
                }, mYear, mMonth, mDay);
        dialog.show(ft, "datePickerDialog");
    }

    private String getNulledString(int intValue) {
        if (intValue < 10) {
            return "0" + intValue;
        } else {
            return String.valueOf(intValue);
        }
    }

    private String getTitleForDatePickerDialog(int id) {
        String title;
        switch (id) {
            case R.id.lev_szuk_utolso_elles_tol:
                title = "Utolsó ellés dátuma -tól";
                break;
            case R.id.lev_szuk_utolso_elles_ig:
                title = "Utolsó ellés dátuma -ig";
                break;
            case R.id.lev_szuk_szuletes_tol:
                title = "Születés dátuma -tól";
                break;
            case R.id.lev_szuk_szuletes_ig:
                title = "Születés dátuma -ig";
                break;
            case R.id.bong_szuk_datum_tol:
                title = "Bírálat időpontja -tól";
                break;
            case R.id.bong_szuk_datum_ig:
                title = "Bírálat időpontja -ig";
                break;
            default:
                title = "Dátumválasztó";
        }
        return title;
    }

    public void startProgressDialog(String title) {
        FragmentTransaction ft = getFragmentTransactionWithTag("progress");
        dialog = ProgressDialog.newInstance(title);
        dialog.show(ft, "progress");
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onProgressEnded() {
        dismissDialog();
    }

    protected void startMyTask(AsyncTask asyncTask, Object[] params) {
        app.startMyTask(asyncTask, params);
    }

    protected void startMyTask(AsyncTask asyncTask) {
        app.startMyTask(asyncTask);
    }

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
