package hu.flexisys.kbr.view.levalogatas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by Peter on 2014.07.24..
 */
public class KbrDatePickerDialog extends KbrDialog implements DatePicker.OnDateChangedListener {


    private String title;
    private DatePickedListener listener;
    private int year;
    private int month;
    private int day;

    public static KbrDialog newInstance(String title, DatePickedListener listener, int mYear, int mMonth, int mDay) {
        KbrDatePickerDialog f = new KbrDatePickerDialog();
        f.title = title;
        f.listener = listener;
        f.year = mYear;
        f.month = mMonth;
        f.day = mDay;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layoutResId = R.layout.dialog_date_picker;
        View v = super.onCreateView(inflater, container, savedInstanceState);

        TextView titleView = (TextView) v.findViewById(R.id.datePickerTitle);
        titleView.setText(title);

        DatePicker mDatePicker = (DatePicker) v.findViewById(R.id.datePicker);
        mDatePicker.init(year, month, day, this);
        mDatePicker.setCalendarViewShown(false);

        Button ok = (Button) v.findViewById(R.id.date_picker_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDatePicked(year, month, day);
            }
        });

        Button cancel = (Button) v.findViewById(R.id.date_picker_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button clear = (Button) v.findViewById(R.id.date_picker_clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClear();
            }
        });
        return v;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }
}
