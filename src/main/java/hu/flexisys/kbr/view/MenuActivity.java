package hu.flexisys.kbr.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.bongeszo.tenyeszet.BongeszoTenyeszetActivity;
import hu.flexisys.kbr.view.levalogatas.LevalogatasTenyeszetActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetActivity;

public class MenuActivity extends KbrActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.hide();
        setContentView(R.layout.activity_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView counter = (TextView) findViewById(R.id.menu_biralat_counter);
        int counterValue = app.getFeltoltetlenBiralatList().size();
        counter.setText(getString(R.string.menu_biralat_counter, counterValue));
    }

    public void startTenyeszet(View view) {
        Intent intent = new Intent(this, TenyeszetActivity.class);
        startActivity(intent);
    }

    public void startLevalogatas(View view) {
        Intent intent = new Intent(this, LevalogatasTenyeszetActivity.class);
        startActivity(intent);
    }

    public void startBiralatok(View view) {
        Intent intent = new Intent(this, BiralatTenyeszetActivity.class);
        startActivity(intent);
    }

    public void startBiralatbongeszo(View view) {
        Intent intent = new Intent(this, BongeszoTenyeszetActivity.class);
        startActivity(intent);
    }

}

