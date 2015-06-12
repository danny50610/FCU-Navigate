package iecs.fcu_navigate;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iecs.fcu_navigate.database.MarkerContract;
import iecs.fcu_navigate.database.MarkerDBHelper;
import iecs.fcu_navigate.helper.DictionaryHelper;

public class MapsActivity extends ActionBarActivity
                          implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                                     GoogleApiClient.ConnectionCallbacks,
                                     GoogleApiClient.OnConnectionFailedListener,
                                     GoogleMap.OnMarkerClickListener,
                                     LocationListener {

    public static final int REQUEST_CODE_NAVIGATE = 2;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;

    // Location請求物件
    private LocationRequest locationRequest;

    private Location mLastLocation;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Flag，當 GoogleApiClient connect 上時，是否要使Map移動到目前位置。
     */
    private boolean moveToMyLocOnConnent = false;

    private ArrayList<Marker> mMarkers = new ArrayList<>();

    private Map<String, MarkerContract.Item> mMarkerInfos = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        configLocationRequest();
        setUpMapIfNeeded();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        moveToMyLocOnConnent = true;

        ImageButton navigateButton = (ImageButton) findViewById(R.id.imageButton2);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent().setClass(
                        MapsActivity.this,
                        NavigateActivity.class
                ), REQUEST_CODE_NAVIGATE);
            }
        });

        MarkerDBHelper.initialize(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        // 連線到Google API用戶端
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1) {
            Bundle args = data.getExtras();
            Object obj = args.getSerializable("Item");
            if (obj != null) {
                MarkerContract.Item item = (MarkerContract.Item) obj;

                for (Marker marker : mMarkers) {
                    marker.remove();
                }
                mMarkers.clear();

                mMarkerInfos.clear();

                LatLng place = new LatLng(item.getLatitude(), item.getLongitude());
                Marker marker = mMap.addMarker(new MarkerOptions().position(place).title(item.getName()));
                mMarkers.add(marker);
                mMarkerInfos.put(marker.getId(), item);

                gotoLocation(place);
            }
        }
        else if (requestCode == REQUEST_CODE_NAVIGATE && resultCode == 0) {
            Bundle args = data.getExtras();
            MarkerContract.Item origin = (MarkerContract.Item) args.getSerializable(NavigateActivity.Bundle_KEY_ORIGIN);
            MarkerContract.Item destination = (MarkerContract.Item) args.getSerializable(NavigateActivity.Bundle_KEY_DESTINATION);

            if (origin.equals(MarkerContract.ItemMyLocation)) {
                origin.setLatitude(mLastLocation.getLatitude());
                origin.setLongitude(mLastLocation.getLongitude());
            }

            if (destination.equals(MarkerContract.ItemMyLocation)) {
                destination.setLatitude(mLastLocation.getLatitude());
                destination.setLongitude(mLastLocation.getLongitude());
            }

            DictionaryHelper.startNavigate(mMap, origin, destination);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 移除位置請求服務
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // 移除Google API用戶端連線
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //先不標記
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMarkerClickListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        restoreActionBar();
        return true;
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.custom_action_bar);

        TextView title = (TextView) findViewById(R.id.action_bar_textview);
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSelectActivity();
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                //尋找地點
                switchToSelectActivity();
                break;
            case 1:

                break;
            case 2:
                //前往逢甲
                gotoLocation(new LatLng(24.179916, 120.648304));
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (moveToMyLocOnConnent) {
            moveToMyLocOnConnent = false;
            moveToLastKnownLocation();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, locationRequest, MapsActivity.this);
    }

    private void moveToLastKnownLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            gotoLocation(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    public void gotoLocation(LatLng place) {
        // 建立地圖攝影機的位置物件，似乎 17 才看得到建物
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void switchToSelectActivity() {
        startActivityForResult(new Intent().setClass(
                MapsActivity.this,
                MarkerSelectorActivity.class
        ), 1);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getApplicationContext(), getString(R.string.onConnectionSuspended_message), Toast.LENGTH_LONG).show();
        Log.e("GoogleApiClient", String.format("onConnectionSuspended(code: %d)", i));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), getString(R.string.onConnectionFailed_message), Toast.LENGTH_LONG).show();
        Log.e("GoogleApiClient", connectionResult.toString());
        //TODO: 可能是沒有安裝Google Play服務

    }

    @Override
    public void onLocationChanged(Location location) {
        TextView textView = (TextView) findViewById(R.id.textView);

        textView.setText(String.format("(%f, %f)", location.getLatitude(), location.getLongitude()));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle args = new Bundle();
        args.putSerializable("Item", mMarkerInfos.get(marker.getId()));

        startActivity(new Intent().putExtras(args).setClass(
                MapsActivity.this,
                MarkerInfoActivity.class
        ));
        return false;
    }
}
