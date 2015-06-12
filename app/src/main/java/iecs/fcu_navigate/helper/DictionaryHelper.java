package iecs.fcu_navigate.helper;

import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.Lists;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import iecs.fcu_navigate.database.MarkerContract;

/**
 * Google Dictionary Api Helper
 */
public class DictionaryHelper {

    private static String generateURL(MarkerContract.Item origin, MarkerContract.Item destination) {
        //https://maps.googleapis.com/maps/api/directions/json?
        return new Uri.Builder()
                .scheme("https")
                .authority("maps.googleapis.com")
                .appendPath("maps")
                .appendPath("api")
                .appendPath("directions")
                .appendPath("json")
                .appendQueryParameter("origin", origin.getLatitude() + "," + origin.getLongitude())
                .appendQueryParameter("destination", destination.getLatitude() + "," + destination.getLongitude())
                .appendQueryParameter("mode", "walking")
                .build().toString();
    }

    public static void startNavigate(GoogleMap mMap, MarkerContract.Item origin, MarkerContract.Item destination) {
        new DownLoadDataTask(mMap).execute(generateURL(origin, destination));
    }

    private static class DownLoadDataTask extends AsyncTask<String, Void, String> {

        private static ArrayList<Polyline> mPolylines = new ArrayList<>();

        private GoogleMap mMap;

        public DownLoadDataTask(GoogleMap mMap) {
            this.mMap = mMap;
        }

        @Override
        protected String doInBackground(String... params) {
            return getData(params[0]);
        }

        private String getData(String uri) {
            try {
                URL url = new URL(uri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();

                Log.d("Google Directions API", "The response is: " + conn.getResponseCode());

                InputStream is = conn.getInputStream();

                // Convert the InputStream into a string
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                return sb.toString();
            }
            catch (Exception e) {
                Log.e("Google Directions API", e.getMessage());
                Log.e("Google Directions API", Log.getStackTraceString(e));
            }
            return "{}";
        }

        @Override
        protected void onPostExecute(String result) {
            Map<String, Object> data = new Gson().fromJson(result, Map.class);
            if (data.get("status").equals("OK")) {
                String overview_polyline = (String) ((Map)((Map)((List)data.get("routes")).get(0)).get("overview_polyline")).get("points");

                List<LatLng> points = decodePolylines(overview_polyline);

                PolylineOptions lineOptions = new PolylineOptions();

                lineOptions.addAll(points);
                lineOptions.width(10);  //導航路徑寬度
                lineOptions.color(Color.BLUE); //導航路徑顏色

                for (Polyline polyline : mPolylines) {
                    polyline.remove();
                }
                mPolylines.add(mMap.addPolyline(lineOptions));
            }
        }

        private List<LatLng> decodePolylines(String poly) {
            List<LatLng> point = Lists.newArrayList();

            int len = poly.length();
            int index = 0;
            int lat = 0;
            int lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;

                do {
                    b = poly.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);

                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                point.add(new LatLng((((double) lat / 1E5)),(((double) lng / 1E5))));
            }

            return point;
        }
    }
}
