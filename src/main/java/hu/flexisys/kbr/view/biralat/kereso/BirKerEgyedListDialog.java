package hu.flexisys.kbr.view.biralat.kereso;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.view.KbrDialog;

import java.util.List;

/**
 * Created by Peter on 2014.07.08..
 */
public class BirKerEgyedListDialog extends KbrDialog {

    private List<Egyed> selectedEgyedList;
    private Boolean clickable;
    private EgyedClickListener listener;

    public static BirKerEgyedListDialog newInstance(List<Egyed> selectedEgyedList, Boolean clickable, EgyedClickListener listener) {
        BirKerEgyedListDialog f = new BirKerEgyedListDialog();
        f.layoutResId = R.layout.dialog_bir_ker_egyed_list;
        f.selectedEgyedList = selectedEgyedList;
        f.clickable = clickable;
        f.listener = listener;
        return f;
    }

    public static BirKerEgyedListDialog newInstance(List<Egyed> selectedEgyedList) {
        return newInstance(selectedEgyedList, false, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);
        ListView list = (ListView) v.findViewById(R.id.bir_ker_dialog_egyed_list);
        list.setAdapter(new BirKerEgyedListAdapter(getActivity(), R.layout.list_bir_ker_egyed_list));

        Button ok = (Button) v.findViewById(R.id.bir_ker_dialog_egyed_list_ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

    public interface EgyedClickListener {
        public void onEgyedClick(String AZONO, String ORSKO);
    }

    public class BirKerEgyedListAdapter extends ArrayAdapter<Egyed> {

        public BirKerEgyedListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Egyed egyed = selectedEgyedList.get(position);
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_bir_ker_egyed_list, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.bir_ker_dialog_egyed_list_enar);
            String enar = String.valueOf(egyed.getAZONO());
            if (enar.length() == 10) {
                Spanned spanned = Html.fromHtml(enar.substring(0, 5) + "  <b>" + enar.substring(5, 9) + "</b>  " + enar.substring(9));
                textView.setText(spanned);
            } else {
                textView.setText(enar);
            }
            if (clickable) {
                textView.setClickable(true);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onEgyedClick(egyed.getAZONO(), egyed.getORSKO());
                    }
                });
            }
            // if (egyed.getUJ()) {
            //     textView.setBackgroundColor(getResources().getColor(R.color.red));
            // } else if (!egyed.getBiralatList().isEmpty()) {
            //     textView.setBackgroundColor(getResources().getColor(R.color.blue));
            // } else if (egyed.getKIVALASZTOTT()) {
            //     textView.setBackgroundColor(getResources().getColor(R.color.green));
            // } else {
            //     textView.setBackgroundColor(getResources().getColor(R.color.gray));
            // }

            CheckBox itvCheckBox = (CheckBox) rowView.findViewById(R.id.bir_ker_dialog_egyed_list_itv);
            itvCheckBox.setChecked(egyed.getITVJE());
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
