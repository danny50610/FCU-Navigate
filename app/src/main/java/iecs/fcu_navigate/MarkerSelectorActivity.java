package iecs.fcu_navigate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.Arrays;

import iecs.fcu_navigate.database.CategoryContract;
import iecs.fcu_navigate.database.MarkerContract;
import iecs.fcu_navigate.database.MarkerDBHelper;


public class MarkerSelectorActivity extends ActionBarActivity
                                    implements ListFragment.onItemClickCallBacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_selector);

        Bundle receiveArgs = getIntent().getExtras();
        if (receiveArgs != null) {
            //TODO: old Bug. http://stackoverflow.com/a/28720450/4983032
            Object[] objs = (Object[]) receiveArgs.getSerializable(ListFragment.Bundle_KEY_ITEM_List);
            MarkerContract.Item[] items = Arrays.copyOf(objs, objs.length, MarkerContract.Item[].class);

            Bundle args = new Bundle();
            args.putInt(ListFragment.Bundle_KEY_LISTVIEW_LEVEL, 2);
            args.putSerializable(ListFragment.Bundle_KEY_ITEM_List, items);

            ListFragment listFragment = new ListFragment();
            listFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_list, listFragment)
                    .commit();

        }
        else {
            Bundle args = new Bundle();
            args.putInt(ListFragment.Bundle_KEY_LISTVIEW_LEVEL, 1);
            args.putSerializable(ListFragment.Bundle_KEY_ITEM_List, CategoryContract.getAllCategoryName(MarkerDBHelper.instance));

            ListFragment listFragment = new ListFragment();
            listFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_list, listFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.marker_selector, menu);
        restoreActionBar();
        return true;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListFragment nowListFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        int level = nowListFragment.getLevel();
        if (level == 1) {
            Bundle args = new Bundle();
            args.putInt(ListFragment.Bundle_KEY_LISTVIEW_LEVEL, level + 1);

            ListFragment subListFragment = new ListFragment();
            subListFragment.setArguments(args);

            String categoryName = parent.getItemAtPosition(position).toString();
            MarkerContract.Item[] items = MarkerContract.getItembyCategory(MarkerDBHelper.instance, categoryName);

            args.putSerializable(ListFragment.Bundle_KEY_ITEM_List, items);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_list, subListFragment)
                    .addToBackStack(null)
                    .commit();
        }
        else if (level == 2) {
            ListFragment.ListItem item = nowListFragment.mItems[position];

            if (item.isVirtual()) {
                //不做任何事，因為使用者點到虛擬的項目
            }
            else {
                Bundle args = new Bundle();
                args.putSerializable("Item", item);

                setResult(1, new Intent().putExtras(args).setClass(
                        MarkerSelectorActivity.this,
                        MapsActivity.class
                ));

                finish();
            }
        }
    }
}
