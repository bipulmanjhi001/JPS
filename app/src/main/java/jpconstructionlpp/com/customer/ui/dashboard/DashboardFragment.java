package jpconstructionlpp.com.customer.ui.dashboard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
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
import static android.content.Context.MODE_PRIVATE;

public class DashboardFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    String token;
    LinearLayout sw_layout;
    private static final String SHARED_PREF_NAME = "JPhpref";
    private TextView Name,Email,Permanent,Phone,plot_area,total_cost,downpayment,dues_amount,no_installments;
    ProgressBar cust_pro;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cust_dashboard, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = sp.getString("keyid", "");

        cust_pro=(ProgressBar)view.findViewById(R.id.cust_pro);
        sw_layout=(LinearLayout)view.findViewById(R.id.sw_layout);
        Name = (TextView)view.findViewById(R.id.Name);
        Email = (TextView)view.findViewById(R.id.Email);
        Permanent = (TextView)view.findViewById(R.id.Permanent);
        Phone = (TextView)view.findViewById(R.id.Phone);
        plot_area = (TextView)view.findViewById(R.id.plot_area);
        total_cost = (TextView)view.findViewById(R.id.total_cost);
        downpayment = (TextView)view.findViewById(R.id.downpayment);
        dues_amount = (TextView)view.findViewById(R.id.dues_amount);
        no_installments = (TextView)view.findViewById(R.id.no_installments);

        ShowDashboard();

        return view;
    }

    private void ShowDashboard() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_customerdetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("status")) {
                                JSONObject object=obj.getJSONObject("userdata");
                                sw_layout.setVisibility(View.VISIBLE);
                                cust_pro.setVisibility(View.GONE);
                                Name.setText(object.getString("name"));
                                Permanent.setText(object.getString("permanent_address"));
                                Phone.setText(object.getString("mobile_no"));
                                Email.setText(object.getString("email"));
                                plot_area.setText(object.getString("plot_area"));
                                total_cost.setText(object.getString("total_cost"));
                                downpayment.setText(object.getString("downpayment"));
                                dues_amount.setText(object.getString("dues_amount"));
                                no_installments.setText(object.getString("no_installments"));
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
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
