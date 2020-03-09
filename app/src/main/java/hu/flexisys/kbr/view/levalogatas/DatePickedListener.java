package hu.flexisys.kbr.view.levalogatas;

/**
 * Created by Peter on 2014.07.08..
 */
public interface DatePickedListener {

    public void onClear();

    public void onDatePicked(int year, int monthOfYear, int dayOfMonth);
}
