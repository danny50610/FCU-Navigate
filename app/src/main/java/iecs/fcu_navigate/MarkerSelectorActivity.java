package iecs.fcu_navigate;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import iecs.fcu_navigate.database.CategoryContract;
import iecs.fcu_navigate.database.MarkerDBHelper;


public class MarkerSelectorActivity extends ActionBarActivity
                                    implements ListFragment.onItemClickCallBacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_selector);

        Bundle args = new Bundle();
        args.putStringArray(
                ListFragment.Bundle_KEY_String_List,
                CategoryContract.getAllCategoryName(MarkerDBHelper.instance)
        );

        ListFragment listFragment = new ListFragment();
        listFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_list, listFragment)
                .commit();
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
        ListFragment subListFragment = new ListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_list, subListFragment)
                .addToBackStack(null)
                .commit();
    }
}
