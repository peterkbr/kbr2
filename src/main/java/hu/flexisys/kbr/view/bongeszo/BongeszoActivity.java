package hu.flexisys.kbr.view.bongeszo;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.*;
import android.widget.TextView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.model.Tenyeszet;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.biralat.BiralatTenyeszetActivity;

import java.util.List;

/**
 * Created by Peter on 2014.07.21..
 */
public class BongeszoActivity extends KbrActivity {

    private static final String TAG = "KBR_BongeszoActivity";
    private String[] selectedTenazArray;

    private SlidingPaneLayout pane;

    // MENU IN ACTIONBAR

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bongeszo);

        View customView = LayoutInflater.from(this).inflate(R.layout.activity_bongeszo_actionbar, null);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(customView);

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        pane = (SlidingPaneLayout) findViewById(R.id.sp);
        pane.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelOpened(View panel) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPanelClosed(View panel) {
                invalidateOptionsMenu();
            }
        });

        selectedTenazArray = getIntent().getExtras().getStringArray(BiralatTenyeszetActivity.EXTRAKEY_SELECTEDTENAZLIST);
        TextView telep = (TextView) findViewById(R.id.bong_szuk_telep_name);
        List<Tenyeszet> tenyeszetList = app.getTenyeszetListByTENAZArray(selectedTenazArray);
        String text = tenyeszetList.size() + " db telep";
        if (tenyeszetList.size() == 1) {
            text = tenyeszetList.get(0).getTARTO();
        }
        telep.setText(text);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        int resId = pane.isOpen() ? R.menu.menu_empty : R.menu.menu_activity_bongeszo;
        inflater.inflate(resId, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.szukites:
                pane.openPane();
                return true;
            case R.id.bongeszo_export:
                return true;
            case R.id.bongeszo_linearis:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
