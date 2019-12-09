package jpconstructionlpp.com.supervisor.ui.Expenses;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.gcacace.signaturepad.views.SignaturePad;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.model.VolleySingleton;
import static android.content.Context.MODE_PRIVATE;

public class Expenses extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    Button add_expenses,add_expenses_item_names,add_expenses_date;
    EditText expense_date,expense_item_name;
    EditText bill_name,particular_name,bill_amount,bill_now;
    private int mYear, mMonth, mDay;
    SignaturePad expense_signaturePad;

    private static final String SHARED_PREF_NAME = "JPhpref";
    String name,token,getId,bill_nos;
    String expense_dates,expense_item_names,bill_names,particular_names,bill_amounts;
    ArrayList ids = new ArrayList();
    ListView items;
    ArrayList expense_heads = new ArrayList();
    Button clear_sign;
    LinearLayout show_sign;
    private OnFragmentInteractionListener mListener;

    public Expenses() {

    }

    public static Expenses newInstance(String param1, String param2) {
        Expenses fragment = new Expenses();
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
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        show_sign=(LinearLayout)view.findViewById(R.id.show_sign);
        add_expenses=(Button)view.findViewById(R.id.add_expenses);
        expense_date=(EditText)view.findViewById(R.id.expense_date);

        bill_now=(EditText)view.findViewById(R.id.bill_no);
        expense_item_name=(EditText)view.findViewById(R.id.expense_item_name);
        bill_name=(EditText)view.findViewById(R.id.bill_name);
        particular_name=(EditText)view.findViewById(R.id.particular_name);
        bill_amount=(EditText)view.findViewById(R.id.bill_amount);
        add_expenses_date=(Button)view.findViewById(R.id.add_expenses_date);

        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        name = sp.getString("keyusername", "");
        token=sp.getString("keyid", "");

        CallValue();

        add_expenses_item_names=(Button)view.findViewById(R.id.add_expenses_item_name);
        add_expenses_item_names.setOnClickListener(this);
        add_expenses.setOnClickListener(this);
        add_expenses_date.setOnClickListener(this);

        expense_signaturePad=(SignaturePad)view.findViewById(R.id.expense_signaturePad);
        clear_sign=(Button)view.findViewById(R.id.clear_sign);
        clear_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expense_signaturePad.clear();
            }
        });
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == add_expenses_date) {
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            expense_date.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }

        if(v == add_expenses_item_names){
            showPopupClassType(v);
        }

        if(v == add_expenses){
            attemptLogin();
        }
    }

    private void showPopupClassType(View view) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.list_dialog);
        items= (ListView) dialog.findViewById(R.id.List);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, expense_heads);
        items.setAdapter(adapter);
        items.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                expense_item_name.setText(expense_heads.get(position).toString());
                getId=ids.get(position).toString();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void attemptLogin() {
        expense_dates = expense_date.getText().toString();
        expense_item_names = expense_item_name.getText().toString();
        bill_names = bill_name.getText().toString();
        particular_names = particular_name.getText().toString();
        bill_amounts = bill_amount.getText().toString();
        bill_nos = bill_now.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(expense_dates)) {
            expense_date.setError(getString(R.string.error_field_required));
            focusView = expense_date;
            cancel = true;
        }
        if (TextUtils.isEmpty(expense_item_names)) {
            expense_item_name.setError(getString(R.string.error_field_required));
            focusView = expense_item_name;
            cancel = true;
        }
        if (TextUtils.isEmpty(bill_names)) {
            bill_name.setError(getString(R.string.error_field_required));
            focusView = bill_name;
            cancel = true;
        }
        if (TextUtils.isEmpty(bill_amounts)) {
            bill_amount.setError(getString(R.string.error_field_required));
            focusView = bill_amount;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();

        }else {
            AddExpenses();
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void CallValue(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_expensehead,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray userJson = obj.getJSONArray("list");

                            for(int i=0; i<userJson.length(); i++) {

                                JSONObject itemslist = userJson.getJSONObject(i);
                                String id = itemslist.getString("id");
                                ids.add(id);
                                String name = itemslist.getString("name");
                                expense_heads.add(name);

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
                })
        {
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    private void AddExpenses(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_addexpense,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("status")) {
                                Toast.makeText(getActivity(), obj.getString("message"),Toast.LENGTH_SHORT).show();
                                bill_name.setText("");
                                particular_name.setText("");
                                bill_amount.setText("");
                                bill_now.setText("");
                                expense_date.setText("");
                                expense_item_name.setText("");

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
                params.put("expense_head", getId);
                Log.d("ex_id",getId);
                params.put("date", expense_dates);
                params.put("bill_no", bill_nos);
                params.put("name", bill_names);
                params.put("particulars", particular_names);
                params.put("description",particular_names);
                params.put("amount", bill_amounts);

                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

}