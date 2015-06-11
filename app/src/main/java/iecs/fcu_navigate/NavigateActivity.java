package iecs.fcu_navigate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import iecs.fcu_navigate.database.MarkerContract;


public class NavigateActivity extends ActionBarActivity implements OnClickListener {

    TextView mOriginName;

    TextView mDestinationName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        mOriginName = (TextView) findViewById(R.id.textView7);
        mOriginName.setOnClickListener(this);

        mDestinationName = (TextView) findViewById(R.id.textView8);
        mDestinationName.setOnClickListener(this);

        findViewById(R.id.imageButton).setOnClickListener(this);
        findViewById(R.id.imageButton3).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            Bundle args = data.getExtras();
            MarkerContract.Item item = (MarkerContract.Item) args.getSerializable("Item");

            if (requestCode == 0) {
                mOriginName.setText(item.getName());
            }
            else if (requestCode == 1) {
                mDestinationName.setText(item.getName());
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_nothing, menu);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        return true;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView6:
            case R.id.textView7:
                startActivityForResult(new Intent().setClass(
                        NavigateActivity.this,
                        MarkerSelectorActivity.class
                ), (v.getId() == R.id.textView7) ? 0 : 1);
                break;
            case R.id.imageButton:
                mOriginName.setText(getString(R.string.navigation_my_location));
                break;
            case R.id.imageButton3:
                mDestinationName.setText(getString(R.string.navigation_my_location));
                break;
        }

    }
}
