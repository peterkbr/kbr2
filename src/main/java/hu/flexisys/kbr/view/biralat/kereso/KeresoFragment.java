package hu.flexisys.kbr.view.biralat.kereso;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.DateUtil;
import hu.flexisys.kbr.view.biralat.BiralatActivity;

import java.util.Date;
import java.util.List;

public class KeresoFragment extends Fragment {

    private BiralatActivity activity;

    public static KeresoFragment newInstance(BiralatActivity activity) {
        KeresoFragment fragment = new KeresoFragment();
        fragment.activity = activity;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_biralat_kereso, container, false);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activity != null) {
            activity.onKeresoFragmentResume();
        }
    }

    // UPDATE THE VIEWS

    public void updateHURadio(Boolean hu) {
        View view = getView();
        RadioButton radioHu = (RadioButton) view.findViewById(R.id.bir_ker_check_hu);
        RadioButton radioKu = (RadioButton) view.findViewById(R.id.bir_ker_check_ku);

        if (hu == null) {
            radioHu.setChecked(false);
            radioKu.setChecked(false);
        } else {
            radioHu.setChecked(hu);
            radioKu.setChecked(!hu);
        }
    }

    public void updateKeresoButtons(List<Egyed> egyedList) {
        Integer biraltValue = 0;
        Integer biraltPlusValue = 0;
        Integer biralandoValue = 0;
        for (Egyed egyed : egyedList) {
            Boolean biralt = false;
            for (Biralat biralat : egyed.getBiralatList()) {
                if (biralat.getFELTOLTETLEN() && egyed.getKIVALASZTOTT()) {
                    biralt = true;
                    ++biraltValue;
                    break;
                } else if (biralat.getFELTOLTETLEN() && !egyed.getKIVALASZTOTT()) {
                    biralt = true;
                    ++biraltPlusValue;
                    break;
                }
            }
            if (egyed.getKIVALASZTOTT() && !biralt) {
                ++biralandoValue;
            }
        }

        View view = getView();
        Button button = (Button) view.findViewById(R.id.bir_ker_b1);
        button.setText(biraltValue + (biraltPlusValue > 0 ? ("+" + biraltPlusValue) : ""));
        button = (Button) view.findViewById(R.id.bir_ker_b2);
        button.setText(String.valueOf(biralandoValue));
    }

    public void updateDetails(Egyed selectedEgyedForKereso) {
        View view = getView();
        LinearLayout itvLayout = (LinearLayout) view.findViewById(R.id.bir_ker_itvLayout);
        LinearLayout detailsLayout = (LinearLayout) view.findViewById(R.id.bir_ker_details);
        itvLayout.setVisibility(View.INVISIBLE);
        detailsLayout.setVisibility(View.INVISIBLE);
        if (selectedEgyedForKereso == null) {
            TextView textView = (TextView) view.findViewById(R.id.bir_ker_enar);
            textView.setBackgroundColor(getResources().getColor(R.color.green));
            textView.setText("");
        } else {
            TextView textView;
            String text;

            textView = (TextView) view.findViewById(R.id.bir_ker_enar);
            text = String.valueOf(selectedEgyedForKereso.getAZONO());
            if ("HU".equals(selectedEgyedForKereso.getORSKO()) && text.length() == 10) {
                Spanned spanned = Html.fromHtml(text.substring(0, 5) + " <b>" + text.substring(5, 9) + "</b> " + text.substring(9));
                textView.setText(spanned);
            } else {
                textView.setText(text);
            }

            Boolean biralt = false;
            for (Biralat biralat : selectedEgyedForKereso.getBiralatList()) {
                if (biralat.getFELTOLTETLEN()) {
                    biralt = true;
                    break;
                }
            }
            if (selectedEgyedForKereso.getUJ()) {
                textView.setBackgroundColor(getResources().getColor(R.color.red));
            } else if (biralt) {
                textView.setBackgroundColor(getResources().getColor(R.color.light_blue));
            } else if (selectedEgyedForKereso.getKIVALASZTOTT()) {
                textView.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                textView.setBackgroundColor(getResources().getColor(R.color.transparent));
            }

            if (!selectedEgyedForKereso.getUJ()) {
                itvLayout.setVisibility(View.VISIBLE);
                detailsLayout.setVisibility(View.VISIBLE);

                CheckBox itvCheckBox = (CheckBox) view.findViewById(R.id.bir_ker_itvCheckBox);
                if (selectedEgyedForKereso.getITVJE() == null) {
                    itvLayout.setVisibility(View.INVISIBLE);
                } else {
                    itvCheckBox.setChecked(selectedEgyedForKereso.getITVJE());
                }

                textView = (TextView) view.findViewById(R.id.bir_ker_laktNapok);
                text = "0";
                if (selectedEgyedForKereso.getELLDA() != null && selectedEgyedForKereso.getELLDA().getTime() > 1) {
                    long diff = (new Date().getTime() - selectedEgyedForKereso.getELLDA().getTime()) / (1000 * 60 * 60 * 24);
                    int diffInDays = Math.round(diff);
                    text = String.valueOf(diffInDays);
                }
                textView.setText(text);

                textView = (TextView) view.findViewById(R.id.bir_ker_laktSzam);
                text = "-";
                if (selectedEgyedForKereso.getELLSO() != null) {
                    text = String.valueOf(selectedEgyedForKereso.getELLSO());
                }
                textView.setText(text);

                textView = (TextView) view.findViewById(R.id.bir_ker_ellesDatuma);
                text = "Nem ellett";
                if (selectedEgyedForKereso.getELLDA() != null && selectedEgyedForKereso.getELLDA().getTime() > 1) {
                    text = DateUtil.formatDate(selectedEgyedForKereso.getELLDA());
                }
                textView.setText(text);

                textView = (TextView) view.findViewById(R.id.bir_ker_szuletes);
                text = "-";
                if (selectedEgyedForKereso.getSZULD() != null) {
                    text = DateUtil.formatDate(selectedEgyedForKereso.getSZULD());
                }
                textView.setText(text);

                textView = (TextView) view.findViewById(R.id.bir_ker_konstrKod);
                text = "-";
                if (selectedEgyedForKereso.getKONSK() != null) {
                    text = String.valueOf(selectedEgyedForKereso.getKONSK());
                }
                textView.setText(text);

                textView = (TextView) view.findViewById(R.id.bir_ker_szinkod);
                text = "-";
                if (selectedEgyedForKereso.getSZINE() != null) {
                    text = String.valueOf(selectedEgyedForKereso.getSZINE());
                }
                textView.setText(text);
            }
        }
    }
}
