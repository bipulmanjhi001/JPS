package jpconstructionlpp.com.supervisor.ui.AccountLedger;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.model.AccountAdapter;
import jpconstructionlpp.com.model.AccountModel;
import jpconstructionlpp.com.model.VolleySingleton;
import static android.content.Context.MODE_PRIVATE;

public class Account extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    ProgressBar pr_at_list;
    String token;
    AccountAdapter adapter;
    ListView account_list;
    ArrayList<AccountModel> accountListss;
    private static final String SHARED_PREF_NAME = "JPhpref";
    FloatingActionButton floatingActionButton;

    public Account() {
    }
    public static Account newInstance(String param1, String param2) {
        Account fragment = new Account();
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
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        account_list = (ListView) view.findViewById(R.id.account__list);
        account_list.setDivider(null);
        pr_at_list = (ProgressBar) view.findViewById(R.id.account_at_list);

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = sp.getString("keyid", "");
        accountListss = new ArrayList<AccountModel>();

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.account_staff);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountListss.clear();
                CallList();
                ObjectAnimator.ofFloat(floatingActionButton, "rotation", 0f, 360f).setDuration(800).start();
            }
        });

        CallList();
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    public void CallList() {
        pr_at_list.setVisibility(View.VISIBLE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_accpayment,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray userJson = obj.getJSONArray("list");

                            for (int i = 0; i < userJson.length(); i++) {

                                JSONObject itemslist = userJson.getJSONObject(i);
                                String id = itemslist.getString("id");
                                String date = itemslist.getString("date");
                                String amount = itemslist.getString("amount");
                                AccountModel accountList = new AccountModel(id,amount,date);
                                accountListss.add(accountList);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            pr_at_list.setVisibility(View.GONE);
                            adapter = new AccountAdapter(accountListss, getActivity());
                            account_list.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        } catch (NullPointerException e) {
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
    }
}