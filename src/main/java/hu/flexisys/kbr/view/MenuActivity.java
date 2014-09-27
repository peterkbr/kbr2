package hu.flexisys.kbr.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;
import hu.flexisys.kbr.view.bongeszo.tenyeszet.BongeszoTenyeszetActivity;
import hu.flexisys.kbr.view.levalogatas.LevalogatasTenyeszetActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetActivity;

public class MenuActivity extends KbrActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        View customView = LayoutInflater.from(this).inflate(R.layout.activity_menu_actionbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(customView);

        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView counter = (TextView) findViewById(R.id.menu_biralat_counter);
        int counterValue = app.getFeltoltetlenBiralatList().size();
        counter.setText(getString(R.string.menu_biralat_counter, counterValue));
    }

    // APP MENU

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

    // ACTIONBAR MENU

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.db:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu:
                Toast.makeText(this, "Not implemented yet!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

