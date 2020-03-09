package hu.flexisys.kbr.view.bongeszo.tenyeszet;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetAdapter;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;

import java.util.List;

/**
 * Created by Peter on 2014.07.23..
 */
public class BongeszoTenyeszetListAdapter extends TenyeszetAdapter {

    public BongeszoTenyeszetListAdapter(Context context, int resource, List<TenyeszetListModel> tenyeszetList, List<String> selectedList) {
        super(context, resource, tenyeszetList, selectedList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = super.getView(position, convertView, parent);
        TenyeszetListModel model = tenyeszetList.get(position);
        if (model.getBiralatWaitingForUpload() > 0) {
            rowView.setBackgroundColor(getContext().getResources().getColor(R.color.green));
        }
        return rowView;
    }
}
