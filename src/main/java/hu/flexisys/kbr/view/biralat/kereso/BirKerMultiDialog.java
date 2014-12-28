package hu.flexisys.kbr.view.biralat.kereso;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrDialog;

import java.util.List;

/**
 * Created by Peter on 2014.07.08..
 */
public class BirKerMultiDialog extends KbrDialog {

    private BirKerMultiListener listener;
    private List<Egyed> selectedEgyedList;
    private Egyed selectedEgyed;


    public static BirKerMultiDialog newInstance(BirKerMultiListener listener, List<Egyed> selectedEgyedList) {
        BirKerMultiDialog f = new BirKerMultiDialog();
        f.layoutResId = R.layout.dialog_bir_ker_multi;
        f.listener = listener;
        f.selectedEgyedList = selectedEgyedList;
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        ListView list = (ListView) v.findViewById(R.id.bir_ker_dialog_multi_list);
        list.setAdapter(new BirKerMultiListAdapter(getActivity(), android.R.layout.simple_expandable_list_item_1));

        Button ok = (Button) v.findViewById(R.id.bir_ker_dialog_multi_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedEgyed != null) {
                    listener.onSelect(selectedEgyed);
                }
            }
        });

        Button cancel = (Button) v.findViewById(R.id.bir_ker_dialog_multi_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCancel();
            }
        });
        return v;
    }

    class BirKerMultiListAdapter extends ArrayAdapter<Egyed> {

        public BirKerMultiListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Egyed egyed = selectedEgyedList.get(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_bir_ker_multi, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.bir_ker_dialog_multi_list_text);

            if (selectedEgyed != null && egyed.getAZONO().equals(selectedEgyed.getAZONO())) {
                textView.setText(Html.fromHtml("<b>" + egyed.getAZONO() + "</b>"));
            } else {
                textView.setText(String.valueOf(egyed.getAZONO()));
            }

            Boolean biralt = false;
            for (Biralat biralat : egyed.getBiralatList()) {
                if (biralat.getFELTOLTETLEN()) {
                    biralt = true;
                    break;
                }
            }
            if (egyed.getUJ()) {
                textView.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (biralt) {
                textView.setBackgroundColor(getResources().getColor(R.color.light_blue));
            } else if (egyed.getKIVALASZTOTT()) {
                textView.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }

            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedEgyed = selectedEgyedList.get(position);
                    notifyDataSetChanged();
                }
            });
            return rowView;
        }

        @Override
        public int getCount() {
            return selectedEgyedList.size();
        }

        @Override
        public long getItemId(int position) {
            return Long.valueOf(selectedEgyedList.get(position).getAZONO());
        }
    }

}
