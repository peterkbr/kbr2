package hu.flexisys.kbr.view.bongeszo.tenyeszet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import hu.flexisys.kbr.R;
import hu.flexisys.kbr.controller.emptytask.EmptyTask;
import hu.flexisys.kbr.controller.emptytask.Executable;
import hu.flexisys.kbr.controller.emptytask.ExecutableFinishedListener;
import hu.flexisys.kbr.model.Biralat;
import hu.flexisys.kbr.model.Egyed;
import hu.flexisys.kbr.util.*;
import hu.flexisys.kbr.view.KbrActivity;
import hu.flexisys.kbr.view.NotificationDialog;
import hu.flexisys.kbr.view.bongeszo.BongeszoActivity;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModel;
import hu.flexisys.kbr.view.tenyeszet.TenyeszetListModelComparatorByLetda;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Peter on 2014.07.04..
 */
public class BongeszoTenyeszetActivity extends KbrActivity {

    public static String EXTRAKEY_SELECTEDTENAZLIST = "selectedTenazArray";
    private final List<TenyeszetListModel> tenyeszetList = new ArrayList<TenyeszetListModel>();
    private final List<String> selectedList = new ArrayList<String>();
    private BongeszoTenyeszetListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bongeszo_tenyeszet);
        // TODO
        setUpTabBar();

        ListView listView = (ListView) findViewById(R.id.teny_list);
        adapter = new BongeszoTenyeszetListAdapter(this, R.layout.list_tenyeszet, tenyeszetList, selectedList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadData();
        adapter.notifyDataSetChanged();
    }

    private void reloadData() {
        tenyeszetList.clear();
        selectedList.clear();

        List<TenyeszetListModel> rawList = app.getTenyeszetListModels(true, true, true);
        List<TenyeszetListModel> oldList = new ArrayList<TenyeszetListModel>();

        for (TenyeszetListModel model : rawList) {
            if (model.getERVENYES()) {
                Boolean hasBiralat = model.getBiralatCount() > 0;
                if (!hasBiralat) {
                    for (Egyed egyed : model.getTenyeszet().getEgyedList()) {
                        if (egyed.getBiralatList().size() > 0) {
                            hasBiralat = true;
                            break;
                        }
                    }
                }
                if (hasBiralat) {
                    if (model.getBiralatWaitingForUpload() < 1) {
                        oldList.add(model);
                    } else {
                        tenyeszetList.add(model);
                    }
                }
            }
        }

        Collections.sort(tenyeszetList, new TenyeszetListModelComparatorByLetda());
        tenyeszetList.addAll(oldList);
    }

    // MENU IN ACTIONBAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_bongeszo_tenyeszet, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tovabb:
                bongeszes();
                return true;
            case R.id.kuld:
                feltoltes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // LEVÁLOGATÁS

    public void bongeszes() {
        Intent intent = new Intent(this, BongeszoActivity.class);
        if (selectedList.isEmpty()) {
            return;
        }
        Bundle extras = new Bundle();
        String[] selectedTenazArray = new String[selectedList.size()];
        for (int i = 0; i < selectedList.size(); i++) {
            selectedTenazArray[i] = selectedList.get(i);
        }
        extras.putStringArray(EXTRAKEY_SELECTEDTENAZLIST, selectedTenazArray);
        intent.putExtras(extras);
        startActivity(intent);
    }

    // FELTÖLTÉS

    public void feltoltes() {
        if (selectedList.isEmpty()) {
            return;
        }

        Boolean hasBiralatlessTenyeszet = false;
        Boolean hasUnexportedBiralat = false;

        final List<String> selectedTenyeszetList = new ArrayList<String>();
        for (TenyeszetListModel model : tenyeszetList) {
            if (selectedList.contains(model.getTENAZ())) {
                if (model.getBiralatWaitingForUpload() < 1) {
                    hasBiralatlessTenyeszet = true;
                    break;
                } else if (model.getBiralatUnexportedCount() > 0) {
                    hasUnexportedBiralat = true;
                    break;
                } else {
                    selectedTenyeszetList.add(model.getTenyeszet().getTENAZ());
                }
            }
        }
        if (hasBiralatlessTenyeszet || hasUnexportedBiralat) {
            String title = null;
            if (hasBiralatlessTenyeszet) {
                title = getString(R.string.bong_teny_kuld_error_nincs_friss_biralat);
            } else if (hasUnexportedBiralat) {
                title = getString(R.string.bong_teny_kuld_error_unexported_biralat);
            }
            if (title != null) {
                FragmentTransaction ft = getFragmentTransactionWithTag("notificationDialog");
                dialog = NotificationDialog.newInstance(title, null);
                dialog.show(ft, "notificationDialog");
            }
            return;
        }

        startProgressDialog(getString(R.string.bong_teny_progress_kuldes));
        final Boolean[] success = {null};
        EmptyTask task = new EmptyTask(new Executable() {
            @Override
            public void execute() {
                List<Biralat> feltoltetlenBiralatList = app.getFeltoltetlenBiralatListByTenazList(selectedTenyeszetList);
                String requestBody = NetworkUtil.getKullembirRequestBody(app.getBiraloUserId(), feltoltetlenBiralatList);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost post = new HttpPost(KbrApplicationUtil.getServerUrl());
                String responseValue;
                try {
                    post.setEntity(new StringEntity(requestBody));
                    HttpResponse response = httpclient.execute(post);
                    responseValue = EntityUtils.toString(response.getEntity());

                    success[0] = XmlUtil.parseKullembirXml(responseValue);
                    if (success[0]) {
                        for (Biralat biralat : feltoltetlenBiralatList) {
                            biralat.setFELTOLTETLEN(false);
                            app.updateBiralat(biralat);
                            app.updateEgyedWithSelection(biralat.getAZONO(), false);
                        }
                        reloadData();
                    }
                } catch (IOException e) {
                    Log.e(LogUtil.TAG, "accessing network", e);
                } catch (XmlUtilException e) {
                    Log.e(LogUtil.TAG, "send/server error\n" + e.getMessage(), e);
                } catch (XmlPullParserException e) {
                    Log.e(LogUtil.TAG, "parsing response", e);
                }
            }
        }, new ExecutableFinishedListener() {
            @Override
            public void onFinished() {
                adapter.notifyDataSetChanged();
                dismissDialog();
                String title;
                if (success[0] == null || !success[0]) {
                    title = "Sikertelen feltöltés!";
                } else {
                    title = "Sikeres feltöltés!";
                }

                FragmentTransaction ft = getFragmentTransactionWithTag("notificationDialog");
                dialog = NotificationDialog.newInstance(title, null);
                dialog.show(ft, "notificationDialog");
            }
        });
        startMyTask(task);
    }
}