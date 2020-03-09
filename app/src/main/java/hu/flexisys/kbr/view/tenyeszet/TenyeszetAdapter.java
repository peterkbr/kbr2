package hu.flexisys.kbr.view.tenyeszet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import hu.flexisys.kbr.R;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class TenyeszetAdapter extends ArrayAdapter<TenyeszetListModel> {

    protected final Context context;
    protected final List<TenyeszetListModel> tenyeszetList;
    protected final List<String> selectedList;

    public TenyeszetAdapter(Context context, int resource, List<TenyeszetListModel> tenyeszetList, List<String> selectedList) {
        super(context, resource);
        this.context = context;
        this.tenyeszetList = tenyeszetList;
        this.selectedList = selectedList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_tenyeszet, parent, false);

        final TenyeszetListModel model = tenyeszetList.get(position);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -14);
        boolean old = model.getLEDAT() != null && model.getLEDAT().before(cal.getTime());

        if (old) {
            rowView.setBackgroundColor(context.getResources().getColor(R.color.pink));
        }

        final CheckBox checkBox = (CheckBox) rowView.findViewById(R.id.tenyeszetList_checkBox);
        checkBox.setChecked(selectedList.contains(model.getTENAZ()));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!selectedList.contains(model.getTENAZ())) {
                        selectedList.add(model.getTENAZ());
                    }
                } else {
                    selectedList.remove(model.getTENAZ());
                }
            }
        });
        View view = rowView.findViewById(R.id.tenyeszetList_textLayout);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.setChecked(!checkBox.isChecked());
            }
        });

        TextView textView_0 = (TextView) rowView.findViewById(R.id.tenyeszetList_0);
        TextView textView_1 = (TextView) rowView.findViewById(R.id.tenyeszetList_1);
        TextView textView_2 = (TextView) rowView.findViewById(R.id.tenyeszetList_2);

        String text = "";
        if (model.getERVENYES() != null && model.getERVENYES() && model.getTARTO() != null) {
            text = model.getTARTO();
        }
        textView_0.setText(text);

        text = String.valueOf(model.getTENAZ());
        if (model.getERVENYES() != null && model.getERVENYES() && model.getTelepules() != null) {
            text = model.getTENAZ() + ", " + model.getTelepules();
        }
        textView_1.setText(text);

        text = "";
        if (model.getERVENYES() != null && model.getERVENYES() && model.getEgyedCount() != null) {
            if (old) {
                text = "-/-/-";
            } else {
                text = model.getEgyedCount() + "/" + model.getSelectedEgyedCount() + "/" + model.getBiralatWaitingForUpload();
            }
        }
        textView_2.setText(text);

        return rowView;
    }

    @Override
    public int getCount() {
        return tenyeszetList.size();
    }

    @Override
    public long getItemId(int i) {
        return Long.valueOf(tenyeszetList.get(i).getTENAZ());
    }
}
