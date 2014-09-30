package hu.flexisys.kbr.view.admin;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.internal.widget.ListPopupWindow;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.util.KbrApplicationUtil;
import hu.flexisys.kbr.util.PropertiesUtil;
import hu.flexisys.kbr.view.KbrActivity;

import java.io.IOException;
import java.util.*;

/**
 * Created by peter on 29/09/14.
 */
public class AdminActivity extends KbrActivity {

    private String selectedId;
    private List<String> idList;
    private Map<String, String> nameMap;
    private Map<String, String> userNameMap;

    private TextView nameTV;
    private EditText urlET;
    private EditText emailET;
    private ListPopupWindow listPopupWindow;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        actionBar.hide();

        nameTV = (TextView) findViewById(R.id.admin_name);
        nameTV.setText(KbrApplicationUtil.getBiraloNev());
        urlET = (EditText) findViewById(R.id.admin_url);
        urlET.setText(KbrApplicationUtil.getServerUrl());
        emailET = (EditText) findViewById(R.id.admin_email);
        emailET.setText(KbrApplicationUtil.getSupportEmail());

        try {
            loadData();
        } catch (IOException e) {
            // TODO
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Kérem mentse az adatokat!", Toast.LENGTH_LONG).show();
    }

    private void loadData() throws IOException {
        idList = new ArrayList<String>();
        idList.add("01");
        idList.add("02");
        idList.add("03");
        idList.add("04");
        idList.add("05");
        idList.add("06");
        idList.add("07");
        idList.add("08");
        idList.add("09");
        idList.add("10");
        idList.add("13");
        idList.add("14");
        idList.add("15");
        idList.add("16");
        idList.add("17");
        idList.add("18");
        idList.add("19");
        nameMap = new HashMap<String, String>();
        userNameMap = new HashMap<String, String>();

        Properties biraloProperties = PropertiesUtil.loadProperties(this, R.raw.biralok);
        for (String id : idList) {
            String biraloString = biraloProperties.getProperty(id);
            String[] biraloValues = biraloString.split(",");
            String biraloNev = biraloValues[0];
            String biraloUserName = biraloValues[1];
            nameMap.put(id, biraloNev);
            userNameMap.put(id, biraloUserName);
        }
    }

    public void openList(View view) {
        listPopupWindow = new ListPopupWindow(this);
        listPopupWindow.setWidth(ListPopupWindow.FILL_PARENT);
        listPopupWindow.setAnchorView(view);
        listPopupWindow.setAdapter(new UserAdapter(this));
        listPopupWindow.setModal(true);
        listPopupWindow.show();
    }

    public void save(View view) {
        String name = nameTV.getText().toString();
        String url = urlET.getText().toString();
        String email = emailET.getText().toString();
        if (name.isEmpty() || url.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Nincs kitöltve minden mező!", Toast.LENGTH_LONG).show();
        } else {
            KbrApplicationUtil.saveData(selectedId, url, email);
            finish();
        }
    }

    public void cancel(View view) {
        finish();
    }

    class UserAdapter extends ArrayAdapter<String> {

        public UserAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final String id, name, userName;
            id = idList.get(position);
            name = nameMap.get(id);
            userName = userNameMap.get(id);

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.list_user, parent, false);

            TextView textView = (TextView) rowView.findViewById(R.id.user_id);
            textView.setText(id);
            textView = (TextView) rowView.findViewById(R.id.user_name);
            textView.setText(name);
            textView = (TextView) rowView.findViewById(R.id.user_userName);
            textView.setText(userName);

            rowView.setClickable(true);
            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedId = id;
                    nameTV.setText(nameMap.get(id));
                    notifyDataSetChanged();
                    listPopupWindow.dismiss();
                }
            });
            return rowView;
        }

        @Override
        public int getCount() {
            return idList.size();
        }

    }
}