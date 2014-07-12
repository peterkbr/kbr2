package hu.flexisys.kbr.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetActivity;

public class MenuActivity extends KbrActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar.hide();
        setContentView(R.layout.activity_menu);
    }

    public void startTenyeszet(View view) {
        Intent intent = new Intent(this, TenyeszetActivity.class);
        startActivity(intent);
    }

    public void startLevalogatas(View view) {
    }

    public void startBiralatok(View view) {
        Intent intent = new Intent(this, BiralatTenyeszetActivity.class);
        startActivity(intent);
    }

    public void startBiralatbongeszo(View view) {
    }

}

