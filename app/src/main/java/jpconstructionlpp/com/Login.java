package jpconstructionlpp.com;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.customer.Customer;
import jpconstructionlpp.com.model.VolleySingleton;
import jpconstructionlpp.com.pref.SharedPrefManager;
import jpconstructionlpp.com.pref.User;
import jpconstructionlpp.com.supervisor.Supervisor;

public class Login extends AppCompatActivity {

    LinearLayout sign_in_button;
    EditText user_id,password;
    Dialog myDialog;
    TextView forgot;
    String user_ids,passwords,keyvalue="STAFF";
    private static final String SHARED_PREF_NAME = "JPhpref";
    private static final String KEY_BRANCH_ID= "designation";
    private static final String KEY_ID = "keyid";
    String usernames,tokens,deg;
    RadioGroup chooseGroup2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        usernames = prefs.getString("username", null);
        tokens = prefs.getString(KEY_ID, null);
        deg = prefs.getString(KEY_BRANCH_ID, null);

        if (SharedPrefManager.getInstance(this).isLoggedIn()) {
            try {
                if(deg.equals("1")) {
                    finish();
                    startActivity(new Intent(this, Sales.class));
                }
                else if(deg.equals("2")){
                    finish();
                    startActivity(new Intent(this, Supervisor.class));
                }
                else if(deg.equals("3")){
                    finish();
                    startActivity(new Intent(this, Customer.class));
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        sign_in_button=(LinearLayout)findViewById(R.id.sign_in_button);
        sign_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(keyvalue.equals("STAFF")) {
                    attemptLogin();
                }else {
                    attemptLogin2();
                }
            }
        });
        chooseGroup2=(RadioGroup)findViewById(R.id.chooseGroup2);
        chooseGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();
                    if(rb.getText().equals("STAFF")) {
                        keyvalue="STAFF";
                    }
                    else {
                        keyvalue="CUSTOMER";
                    }
                }
            }
        });
        user_id=(EditText)findViewById(R.id.user_id);
        password=(EditText)findViewById(R.id.password);
        myDialog=new Dialog(Login.this);
        forgot=(TextView)findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPopup(v);
            }
        });
    }
    private void attemptLogin() {
        user_id.setError(null);
        password.setError(null);
        user_ids = user_id.getText().toString();
        passwords = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(user_ids)) {
            user_id.setError(getString(R.string.error_field_required));
            focusView = user_id;
            cancel = true;
        }
        if (TextUtils.isEmpty(passwords)) {
            password.setError(getString(R.string.error_incorrect_password));
            focusView = password;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();

        } else {
            Authenticate();
        }
    }

    public void Authenticate() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {

                                JSONObject userJson = obj.getJSONObject("user");
                                String designation = userJson.getString("designation");
                                String token = userJson.getString("token");
                                String name= userJson.getString("name");
                                User user = new User(userJson.getString("token"), userJson.getString("designation"), userJson.getString("name") );

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();

                                if(designation.equals("1")) {
                                    Intent intent = new Intent(getApplicationContext(), Sales.class);
                                    intent.putExtra("designation", designation);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                    finish();
                                }
                                else if(designation.equals("2")){
                                    Intent intent = new Intent(getApplicationContext(), Supervisor.class);
                                    intent.putExtra("designation", designation);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                    finish();
                                }

                            } else if (!obj.getBoolean("status")) {

                                String error = obj.getString("error");
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Connection error..", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user_ids);
                params.put("password", passwords);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void attemptLogin2() {
        user_id.setError(null);
        password.setError(null);
        user_ids = user_id.getText().toString();
        passwords = password.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(user_ids)) {
            user_id.setError(getString(R.string.error_field_required));
            focusView = user_id;
            cancel = true;
        }
        if (TextUtils.isEmpty(passwords)) {
            password.setError(getString(R.string.error_incorrect_password));
            focusView = password;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();

        } else {
            Authenticate2();
        }
    }

    public void Authenticate2() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {

                                JSONObject userJson = obj.getJSONObject("user");
                                String designation = userJson.getString("designation");
                                String token = userJson.getString("token");
                                User user = new User(userJson.getString("token"), userJson.getString("designation"), userJson.getString("name") );

                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();

                                if(designation.equals("3")) {
                                    Intent intent = new Intent(getApplicationContext(), Customer.class);
                                    intent.putExtra("designation", designation);
                                    intent.putExtra("token", token);
                                    startActivity(intent);
                                    finish();
                                }

                            } else if (!obj.getBoolean("status")) {

                                String error = obj.getString("error");
                                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "Connection error..", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user_ids);
                params.put("password", passwords);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void ShowPopup(View v) {
        TextView txtclose;
        final EditText add_d,add_password;
        Button btnFollow;
        myDialog.setContentView(R.layout.forgot_password);
        myDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        add_d =(EditText) myDialog.findViewById(R.id.add_user);
        add_password=(EditText) myDialog.findViewById(R.id.add_password);
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }
}