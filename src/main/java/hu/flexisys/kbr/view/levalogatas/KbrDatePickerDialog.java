package hu.flexisys.kbr.view.levalogatas;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.KbrDialog;

/**
 * Created by Peter on 2014.07.24..
 */
public class KbrDatePickerDialog extends KbrDialog implements DatePicker.OnDateChangedListener {

    private DatePickedListener listener;
    private int year;
    private int month;
    private int day;

    public static KbrDialog newInstance(DatePickedListener listener, int mYear, int mMonth, int mDay) {
        KbrDatePickerDialog f = new KbrDatePickerDialog();
        f.layoutResId = R.layout.dialog_date_picker;
        f.listener = listener;
        f.year = mYear;
        f.month = mMonth;
        f.day = mDay;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

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
