package jpconstructionlpp.com.supervisor.ui.home;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.model.VolleySingleton;
import me.itangqi.waveloadingview.WaveLoadingView;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private TextView t_amt,e_amt;
    WaveLoadingView mWaveLoadingView;
    String token;
    private static final String SHARED_PREF_NAME = "JPhpref";
    Double longitude,latitude;
    LocationTrack locationTrack;
    String latt,lang,locationss="";
    JSONObject object;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = sp.getString("keyid", "");
        mWaveLoadingView = (WaveLoadingView)root.findViewById(R.id.waveLoadingView);

        locationTrack = new LocationTrack(getActivity());
        object=new JSONObject();

        e_amt=(TextView)root.findViewById(R.id.e_amt);
        t_amt=(TextView)root.findViewById(R.id.t_amt);

        Account();

        return root;
    }
    private void Account(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_accledger,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("status")) {

                                JSONObject object=obj.getJSONObject("list");
                                String total_ex=object.getString("totalexpended");
                                String total=object.getString("totalgiven");

                                e_amt.setText(total_ex);
                                t_amt.setText(total);

                                mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
                                mWaveLoadingView.setAnimDuration(3000);
                                mWaveLoadingView.pauseAnimation();
                                mWaveLoadingView.resumeAnimation();
                                mWaveLoadingView.cancelAnimation();
                                mWaveLoadingView.startAnimation();

                            }else {
                                Toast.makeText(getActivity(), obj.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
        EmpLocation();
    }

    private void EmpLocation(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_emplocation,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("status")) {
                                Toast.makeText(getActivity(), obj.getString("message"),Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(), obj.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("location", locationss);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public class LocationTrack extends Service implements LocationListener {
        private final Context mContext;
        boolean checkGPS = false;
        boolean checkNetwork = false;
        boolean canGetLocation = false;
        Location loc;
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
        private static final long MIN_TIME_BW_UPDATES = 10000;
        protected LocationManager locationManager;

        public LocationTrack(Context mContext) {
            this.mContext = mContext;
            getLocation();
        }

        private Location getLocation() {
            try {
                locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
                checkGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                checkNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                if (!checkGPS && !checkNetwork) {
                    Toast.makeText(mContext, "No Service Provider is available", Toast.LENGTH_SHORT).show();
                } else {
                    this.canGetLocation = true;
                    if (checkGPS) {

                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                        }

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d( "Coordinates",latitude+""+longitude);
                                latt = Double.toString(latitude);
                                lang = Double.toString(longitude);
                                try {
                                    object.put("lat", latt);
                                    object.put("lng", lang);
                                    locationss = object.toString();
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                        if (locationManager == null) {
                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                            latt = Double.toString(latitude);
                            lang = Double.toString(longitude);
                            try {
                                object.put("lat", latt);
                                object.put("lng", lang);
                                locationss = object.toString();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                    if (checkNetwork) {
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                        }

                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, new LocationListener() {
                            @Override
                            public void onLocationChanged(Location location) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.d( "Coordinates",latitude+""+longitude);
                                latt = Double.toString(latitude);
                                lang = Double.toString(longitude);
                                try {
                                    object.put("lat", latt);
                                    object.put("lng", lang);
                                    locationss = object.toString();
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onStatusChanged(String provider, int status, Bundle extras) {

                            }

                            @Override
                            public void onProviderEnabled(String provider) {

                            }

                            @Override
                            public void onProviderDisabled(String provider) {

                            }
                        });
                        if (locationManager == null) {
                            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            latitude = loc.getLatitude();
                            longitude = loc.getLongitude();
                            latt = Double.toString(latitude);
                            lang = Double.toString(longitude);
                            try {
                                object.put("lat", latt);
                                object.put("lng", lang);
                                locationss = object.toString();
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return loc;
        }
        public void stopListener() {
            if (locationManager != null) {

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.removeUpdates(LocationTrack.this);
            }
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onLocationChanged(Location location) {
            location.setAccuracy(100);
            latitude=location.getLatitude();
            longitude=location.getLongitude();
            latt = Double.toString(latitude);
            lang = Double.toString(longitude);
            try {
                object.put("lat", latt);
                object.put("lng", lang);
                locationss = object.toString();
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
        }
    }
}