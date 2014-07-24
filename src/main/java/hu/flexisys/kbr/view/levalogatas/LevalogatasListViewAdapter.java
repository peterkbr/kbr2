package hu.flexisys.kbr.view.levalogatas;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;

import java.util.List;

/**
 * Created by Peter on 2014.07.21..
 */
public class LevalogatasListViewAdapter extends ArrayAdapter<Egyed> {

    protected final Context context;
    protected final List<Egyed> egyedList;
    private final OnSelectionChangedListener listener;

    public LevalogatasListViewAdapter(Context context, int resource, List<Egyed> egyedList, OnSelectionChangedListener listener) {
        super(context, resource);
        this.context = context;
        this.egyedList = egyedList;
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_levalogatas, parent, false);

        Egyed egyed = egyedList.get(position);

        Integer color = null;
        if (!egyed.getBiralatList().isEmpty()) {
            color = context.getResources().getColor(R.color.green);
        }

        CheckBox selected = (CheckBox) v.findViewById(R.id.lev_selected);
        selected.setChecked(egyed.getKIVALASZTOTT());
        selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                egyedList.get(position).setKIVALASZTOTT(isChecked);
                listener.onSelectionChanged();
            }
        });
        if (color != null) {
            colorParent(selected, color);
        }

        TextView num = (TextView) v.findViewById(R.id.lev_num);
        num.setText(String.valueOf(position));
        if (color != null) {
            colorParent(num, color);
        }

        TextView orsko = (TextView) v.findViewById(R.id.lev_orsko);
        orsko.setText(egyed.getORSKO());
        if (color != null) {
            colorParent(orsko, color);
        }

        TextView enar = (TextView) v.findViewById(R.id.lev_enar);
        String text = String.valueOf(egyed.getAZONO());
        if (text.length() == 10 && egyed.getORSKO().equals("HU")) {
            Spanned spanned = Html.fromHtml(text.substring(0, 5) + " <b><font color='red'>" + text.substring(5, 9) + "</font></b> " + text.substring(9));

            enar.setText(spanned);
        } else {
            enar.setText(text);
        }
        if (color != null) {
            colorParent(enar, color);
        }

        TextView es = (TextView) v.findViewById(R.id.lev_es);
        es.setText(String.valueOf(egyed.getELLSO()));
        if (color != null) {
            colorParent(es, color);
        }

        TextView ellda = (TextView) v.findViewById(R.id.lev_ellda);
        text = "Nem ellett";
        if (egyed.getELLDA() != null && egyed.getELLDA().getTime() > 1) {
            text = DateUtil.formatDate(egyed.getELLDA());
        }
        ellda.setText(text);
        if (color != null) {
            colorParent(ellda, color);
        }

        TextView szuld = (TextView) v.findViewById(R.id.lev_szuletes);
        text = "-";
        if (egyed.getELLDA() != null) {
            text = DateUtil.formatDate(egyed.getSZULD());
        }
        szuld.setText(text);
        if (color != null) {
            colorParent(szuld, color);
        }

        TextView kon = (TextView) v.findViewById(R.id.lev_kon);
        kon.setText(String.valueOf(egyed.getKONSK()));
        if (color != null) {
            colorParent(kon, color);
        }

        CheckBox itv = (CheckBox) v.findViewById(R.id.lev_itv);
        itv.setChecked(egyed.getITVJE());
        if (color != null) {
            colorParent(itv, color);
        }

        return v;
    }

    private void colorParent(View view, int color) {
        ((View) view.getParent()).setBackgroundColor(color);
    }

    @Override
    public int getCount() {
        return egyedList.size();
    }

    @Override
    public long getItemId(int position) {
        return Long.valueOf(egyedList.get(position).getAZONO());
    }
}
