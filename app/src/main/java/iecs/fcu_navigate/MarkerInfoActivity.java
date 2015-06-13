package iecs.fcu_navigate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Map;

import iecs.fcu_navigate.database.MarkerContract;
import iecs.fcu_navigate.helper.ListViewHelper;


public class MarkerInfoActivity extends ActionBarActivity {

    private static final String Item_TITLE = "title";

    private static final String Item_VALUE = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info);

        Intent intent = this.getIntent();
        if (intent != null) {
            Bundle args = intent.getExtras();
            MarkerContract.Item item = (MarkerContract.Item) args.getSerializable("Item");
            if (item != null) {
                updateUI(item);
            }
        }
    }

    private void updateUI(MarkerContract.Item item) {
        ListView infoList = (ListView) findViewById(R.id.listView2);

        ArrayList<Map<String, String>> data = Lists.newArrayList();
        data.add(ImmutableMap.of(Item_TITLE, "名稱", Item_VALUE, item.getName()));
        data.add(ImmutableMap.of(Item_TITLE, "位置", Item_VALUE, item.getBuildingName() + " " + getFloorString(item.getFloor())));
        data.add(ImmutableMap.of(Item_TITLE, "經緯度", Item_VALUE, String.format("%f, %f", item.getLatitude(), item.getLongitude())));

        infoList.setAdapter(new SimpleAdapter(
                this,
                data,
                android.R.layout.simple_list_item_2,
                new String[]{Item_TITLE, Item_VALUE},
                new int[]{android.R.id.text1, android.R.id.text2}
        ));
        ListViewHelper.updateHeight(infoList);
    }

    private String getFloorString(int floor) {
        if (floor > 0) {
            return floor + "F";
        }
        else if (floor < 0) {
            return "B" + floor * -1;
        }
        else {
            return "";
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nothing, menu);
        restoreActionBar();
        return true;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
