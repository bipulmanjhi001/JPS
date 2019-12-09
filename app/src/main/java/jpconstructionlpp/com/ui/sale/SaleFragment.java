package jpconstructionlpp.com.ui.sale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.model.VolleySingleton;
import static android.content.Context.MODE_PRIVATE;

public class SaleFragment extends Fragment {

    private RadioGroup radioSexGroup;
    private EditText Name,Address,Mobile,Email,FOLLOW,Follow_Date,Comment;
    private Button get_dates,get_follows,Submit;
    String selectDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Double longitude,latitude;
    LocationTrack locationTrack;
    String latt,lang,locationss="",gender="Male";
    ArrayList<String> followstatus = new ArrayList<String>();
    String Names,Addresss,Mobiles,Emails,FOLLOWs,Follow_Dates,Comments,followstatuss;
    Dialog dialog;
    ListView listView;
    private static final String SHARED_PREF_NAME = "JPhpref";
    private String token,usernames;
    JSONObject object;
    Timer myTimer;
    TimerTask doThis;
    int delay = 10000;
    int period = 10000;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.add_client, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        usernames = prefs.getString("username", null);
        token = prefs.getString("keyid", null);

        Name=(EditText)root.findViewById(R.id.Name);
        Address=(EditText)root.findViewById(R.id.Address);
        Mobile=(EditText)root.findViewById(R.id.Mobile);
        Email=(EditText)root.findViewById(R.id.Email);
        Comment=(EditText)root.findViewById(R.id.Comment);
        FOLLOW=(EditText)root.findViewById(R.id.FOLLOW);
        Follow_Date=(EditText)root.findViewById(R.id.Follow_Date);
        locationTrack = new LocationTrack(getActivity());
        dialog=new Dialog(getActivity());
        radioSexGroup=(RadioGroup)root.findViewById(R.id.radioGrp);
        object=new JSONObject();
        myTimer = new Timer();

        radioSexGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    Toast.makeText(getActivity(), rb.getText(), Toast.LENGTH_SHORT).show();
                    if(rb.getText().equals("Male")) {
                        gender="Male";
                    }else {
                        gender="Female";
                    }
                }
            }
        });

        get_dates=(Button)root.findViewById(R.id.get_date);
        get_dates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                selectDate=dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                                Follow_Date.setText(selectDate);
                                Toast.makeText(getActivity(),selectDate,Toast.LENGTH_SHORT).show();
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        get_follows=(Button)root.findViewById(R.id.get_follow);
        get_follows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFollow();

            }
        });

        Submit=(Button)root.findViewById(R.id.Submit);
        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptClient();
            }
        });
        GetGPS();
        return root;
    }

    public void GetGPS(){
        doThis = new TimerTask() {
            public void run() {
                    Check();
            }
        };
        try {
            myTimer.scheduleAtFixedRate(doThis, delay, period);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }
    private void Check() {
       StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_rapidlocation,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject obj = new JSONObject(response);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Toast.makeText(getActivity(), "Check connection again.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> params = new HashMap<>();
                                        params.put("token", token);
                                        params.put("location", locationss);
                                        Log.d( "Coordinates",locationss);

                                        return params;
                                    }
                                };
                                VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
                            }

    public void GetFollow() {
        followstatus.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_followstatus,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("status")) {
                                JSONArray userJson = obj.getJSONArray("list");
                                for(int i=0; i<userJson.length(); i++) {
                                    JSONObject data=userJson.getJSONObject(i);
                                    String follow = data.getString("follow");
                                    followstatus.add(follow);
                                }
                                showFollow();
                            }else {
                                Toast.makeText(getActivity(),"No Follow added",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Check again..", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void showFollow() {
        dialog.setContentView(R.layout.followstatus_list_dialog);
        listView= (ListView) dialog.findViewById(R.id.followstatus_list);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, followstatus){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view =super.getView(position, convertView, parent);
                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.WHITE);
                return view;
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                followstatuss=followstatus.get(position).toString();
                FOLLOW.setText(followstatuss);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void attemptClient() {
        Names = Name.getText().toString();
        Addresss = Address.getText().toString();
        Mobiles = Mobile.getText().toString();
        Emails = Email.getText().toString();
        Comments = Comment.getText().toString();
        FOLLOWs = FOLLOW.getText().toString();
        Follow_Dates = Follow_Date.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(Names)) {
            Name.setError(getString(R.string.error_field_required));
            focusView = Name;
            cancel = true;
        }
        if (TextUtils.isEmpty(Addresss)) {
            Address.setError(getString(R.string.error_field_required));
            focusView = Address;
            cancel = true;
        }
        if (TextUtils.isEmpty(Mobiles)) {
            Mobile.setError(getString(R.string.error_field_required));
            focusView = Mobile;
            cancel = true;
        }
        if (TextUtils.isEmpty(Emails)) {
            Email.setError(getString(R.string.error_field_required));
            focusView = Email;
            cancel = true;
        }
        if (TextUtils.isEmpty(Comments)) {
            Comment.setError(getString(R.string.error_field_required));
            focusView = Comment;
            cancel = true;
        }
        if (TextUtils.isEmpty(FOLLOWs)) {
            FOLLOW.setError(getString(R.string.error_field_required));
            focusView = FOLLOW;
            cancel = true;
        }
        if (TextUtils.isEmpty(Follow_Dates)) {
            Follow_Date.setError(getString(R.string.error_field_required));
            focusView = Follow_Date;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();

        } else {
            AddClient();
        }
    }
    private void AddClient() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_addclient,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {

                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                Name.setText("");
                                Address.setText("");
                                Mobile.setText("");
                                Email.setText("");
                                Comment.setText("");
                                FOLLOW.setText("");
                                Follow_Date.setText("");

                            } else {
                                Toast.makeText(getActivity(), "Check details again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Check details again.", Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("token", token);
                params.put("location", locationss);
                params.put("name", Names);
                params.put("gender", gender);
                params.put("mobile", Mobiles);
                params.put("email", Emails);
                params.put("address",Addresss);
                params.put("follow_status", followstatuss);
                params.put("follow_comment",Comments);
                params.put("follow_date", Follow_Dates);
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