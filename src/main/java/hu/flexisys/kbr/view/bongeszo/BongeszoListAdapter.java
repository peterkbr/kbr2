package hu.flexisys.kbr.view.bongeszo;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by peter on 19/08/14.
 */
public class BongeszoListAdapter extends ArrayAdapter<Biralat> {

    private final Map<String, Egyed> egyedMap;
    private final BongeszoListContainer container;
    private List<Biralat> biralatList;
    private Context context;

    public BongeszoListAdapter(Context context, int resource, List<Biralat> biralatList, Map<String, Egyed> egyedMap, BongeszoListContainer container) {
        super(context, resource);
        this.context = context;
        this.biralatList = biralatList;
        this.egyedMap = egyedMap;
        this.container = container;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.list_bongeszo, parent, false);
        final Biralat currentBiralat = biralatList.get(position);
        final Egyed currentEgyed = egyedMap.get(currentBiralat.getAZONO());

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                container.onLongClick(currentEgyed, currentBiralat);
                return true;
            }
        });

        TextView textView;
        Integer color = null;
        if (!currentBiralat.getEXPORTALT()) {
            color = context.getResources().getColor(R.color.green);
        } else if (currentBiralat.getFELTOLTETLEN()) {
            color = context.getResources().getColor(R.color.yellow);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_num);
        textView.setText(String.valueOf(position + 1));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_ok);
        textView.setText(String.valueOf(currentEgyed.getORSKO()));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_enar);
        String text = String.valueOf(currentEgyed.getAZONO());
        if (text.length() == 10 && currentEgyed.getORSKO().equals("HU")) {
            Spanned spanned = Html.fromHtml(text.substring(0, 5) + " <b><font color='red'>" + text.substring(5, 9) + "</font></b> " + text.substring(9));
            textView.setText(spanned);
        } else {
            textView.setText(text);
        }
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_datum);
        textView.setText(DateUtil.formatDate(currentBiralat.getBIRDA()));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_es);
        textView.setText(String.valueOf(currentEgyed.getELLSO()));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_ert1);
        // 16 : TP : Testpont
        textView.setText(currentBiralat.getErtByKod("16"));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_ert2);
        // 47 : TE : Tejelő erősség
        textView.setText(currentBiralat.getErtByKod("47"));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_ert3);
        // 17 : LP : Lábpont
        textView.setText(currentBiralat.getErtByKod("17"));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_ert4);
        // 21 : TO : Tőgypont
        textView.setText(currentBiralat.getErtByKod("21"));
        if (color != null) {
            colorParent(textView, color);
        }

//        textView = (TextView) v.findViewById(R.id.bong_list_ert5);
//        // 15 : BH : Elülső bimbó hossza
//        textView.setText(currentBiralat.getErtByKod("15"));
//        if (color != null) {
//            colorParent(textView, color);
//        }

        textView = (TextView) v.findViewById(R.id.bong_list_ert6);
        // 25 : VP : végpont
        textView.setText(currentBiralat.getErtByKod("25"));
        if (color != null) {
            colorParent(textView, color);
        }

        textView = (TextView) v.findViewById(R.id.bong_list_a);
        textView.setText(String.valueOf(currentBiralat.getAKAKO()));
        if (color != null) {
            colorParent(textView, color);
        }

        CheckBox itvBox = (CheckBox) v.findViewById(R.id.bong_list_itv);
        itvBox.setChecked(currentEgyed.getITVJE());
        if (color != null) {
            colorParent(itvBox, color);
        }

        return v;
    }

    private void colorParent(View view, int color) {
        ((View) view.getParent()).setBackgroundColor(color);
    }

    @Override
    public int getCount() {
        return biralatList.size();
    }

    public interface BongeszoListContainer {
        public void onLongClick(Egyed currentEgyed, Biralat currentBiralat);
    }
}
