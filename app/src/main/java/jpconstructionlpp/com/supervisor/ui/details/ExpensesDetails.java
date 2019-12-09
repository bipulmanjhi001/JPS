package jpconstructionlpp.com.supervisor.ui.details;

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
import jpconstructionlpp.com.model.ExpenseList;
import jpconstructionlpp.com.model.ExpenseListAdpater;
import jpconstructionlpp.com.model.VolleySingleton;
import static android.content.Context.MODE_PRIVATE;

public class ExpensesDetails extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    ProgressBar pr_at_list;
    String token;
    ExpenseListAdpater adapter;
    ListView expense_list;
    ArrayList<ExpenseList> expenseListss;
    private static final String SHARED_PREF_NAME = "JPhpref";
    FloatingActionButton floatingActionButton;

    public ExpensesDetails() {
    }

    public static ExpensesDetails newInstance(String param1, String param2) {
        ExpensesDetails fragment = new ExpensesDetails();
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
        View view = inflater.inflate(R.layout.fragment_expensedetails, container, false);

        expense_list = (ListView) view.findViewById(R.id.expense__list);
        expense_list.setDivider(null);
        pr_at_list = (ProgressBar) view.findViewById(R.id.expense_at_list);

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        token = sp.getString("keyid", "");
        expenseListss = new ArrayList<ExpenseList>();

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.expense_staff);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenseListss.clear();
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
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_expenselist,
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
                                String name = itemslist.getString("name");
                                String amount = itemslist.getString("amount");

                                ExpenseList expenseList = new ExpenseList(id, date,name, amount);
                                expenseListss.add(expenseList);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            pr_at_list.setVisibility(View.GONE);
                            adapter = new ExpenseListAdpater(expenseListss, getActivity());
                            expense_list.setAdapter(adapter);
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