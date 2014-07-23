package hu.flexisys.kbr.view.levalogatas;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetAdapter;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;

import java.util.List;

/**
 * Created by Peter on 2014.07.23..
 */
public class LevalogatasTenyeszetAdapter extends TenyeszetAdapter {

    public LevalogatasTenyeszetAdapter(Context context, int resource, List<TenyeszetListModel> tenyeszetList, List<String> selectedList) {
        super(context, resource, tenyeszetList, selectedList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = super.getView(position, convertView, parent);
        TextView textView_1 = (TextView) rowView.findViewById(R.id.tenyeszetList_1);
        String text = textView_1.getText().toString();
        text = text + ", " + DateUtil.formatDate(tenyeszetList.get(position).getLEDAT());
        textView_1.setText(text);
        return rowView;
    }
}
