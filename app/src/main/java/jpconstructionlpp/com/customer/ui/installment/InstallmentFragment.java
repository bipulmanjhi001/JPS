package jpconstructionlpp.com.customer.ui.installment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
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
import java.util.HashMap;
import java.util.Map;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.model.CustomerPaidAdapter;
import jpconstructionlpp.com.model.CustomerPaidModel;
import jpconstructionlpp.com.model.ExpenseList;
import jpconstructionlpp.com.model.ExpenseListAdpater;
import jpconstructionlpp.com.model.VolleySingleton;
import static android.content.Context.MODE_PRIVATE;

public class InstallmentFragment extends Fragment {

    String token;
    private static final String SHARED_PREF_NAME = "JPhpref";
    CustomerPaidAdapter customerPaidAdapter;
    ArrayList<CustomerPaidModel> customerPaidModels;
    ListView paid_amt;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cust_home, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = sp.getString("keyid", "");
        customerPaidModels=new ArrayList<CustomerPaidModel>();
        paid_amt=(ListView)root.findViewById(R.id.paid_amt);
        ShowCustPaid();

        return root;
    }

    private void ShowCustPaid() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_customerpaid,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray userJson = obj.getJSONArray("list");

                            for (int i = 0; i < userJson.length(); i++) {

                                JSONObject itemslist = userJson.getJSONObject(i);
                                String id = itemslist.getString("id");
                                String date = itemslist.getString("getdate");
                                String amount = itemslist.getString("amount");

                                CustomerPaidModel customerPaidModel = new CustomerPaidModel(id, date, amount);
                                customerPaidModels.add(customerPaidModel);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            customerPaidAdapter = new CustomerPaidAdapter(customerPaidModels, getActivity());
                            paid_amt.setAdapter(customerPaidAdapter);
                            customerPaidAdapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
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