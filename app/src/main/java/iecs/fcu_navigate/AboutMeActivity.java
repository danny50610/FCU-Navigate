package iecs.fcu_navigate;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Map;


public class AboutMeActivity extends ActionBarActivity {

    private static final String Item_TITLE = "title";
    private static final String Item_VALUE = "value";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        initListView();
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

    private void initListView() {
        ListView aboutMeList = (ListView) findViewById(R.id.listView3);

        ArrayList<Map<String, String>> data = Lists.newArrayList();
        data.add(ImmutableMap.of(Item_TITLE, "版本", Item_VALUE, BuildConfig.VERSION_NAME));

        aboutMeList.setAdapter(new SimpleAdapter(
                this,
                data,
                android.R.layout.simple_list_item_2,
                new String[]{Item_TITLE, Item_VALUE},
                new int[]{android.R.id.text1, android.R.id.text2}
        ));
    }

}
