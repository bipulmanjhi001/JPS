package jpconstructionlpp.com.ui.follow;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import java.util.List;
import java.util.Map;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.api.URLs;
import jpconstructionlpp.com.model.FollowModel;
import jpconstructionlpp.com.model.VolleySingleton;
import static android.content.Context.MODE_PRIVATE;

public class FollowFragment extends Fragment {

    private static final String SHARED_PREF_NAME = "JPhpref";
    private String token,usernames,ids,message;
    ArrayList<FollowModel> followModels;
    FollowAdapter followAdapter;
    RecyclerView recyclerView;
    Dialog myDialog;
    private LinearLayoutManager mLinearLayoutManager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_follow__us, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        usernames = prefs.getString("username", null);
        token = prefs.getString("keyid", null);

        myDialog=new Dialog(getActivity());
        followModels=new ArrayList<>();

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView=(RecyclerView)root.findViewById(R.id.follow_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        UPDATE();

        return root;
    }

    private void UPDATE() {
        followModels.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_clientlist,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject c = new JSONObject(response);
                            if (c.getBoolean("status")) {
                                JSONArray array = c.getJSONArray("list");

                                for (int i = 0; i < array.length(); i++) {
                                    FollowModel followModel = new FollowModel();

                                    JSONObject object = array.getJSONObject(i);
                                    String id = object.getString("id");
                                    String name = object.getString("name");
                                    String  follow_date = object.getString("follow_date");

                                    followModel.setId(id);
                                    followModel.setName(name);
                                    followModel.setDate(follow_date);
                                    followModels.add(followModel);

                                }
                                    try {
                                        followAdapter=new FollowAdapter(getActivity(),R.layout.follow_list,followModels);
                                        recyclerView.setAdapter(followAdapter);
                                        followAdapter.notifyDataSetChanged();
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                    }

                            } else {
                                try {
                                    Toast.makeText(getActivity(), c.getString("message"), Toast.LENGTH_SHORT).show();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
                            }
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


    public class FollowAdapter extends RecyclerView.Adapter<FollowHolder> {

        private final List<FollowModel> bakeries;
        private Context context;
        private int itemResource;

        public FollowAdapter(Context context, int itemResource, List<FollowModel> bakeries) {
            this.bakeries = bakeries;
            this.context = context;
            this.itemResource = itemResource;
        }

        @Override
        public FollowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(this.itemResource, parent, false);
            return new FollowHolder(this.context, view);
        }

        @Override
        public void onBindViewHolder(FollowHolder holder, int position) {

            FollowModel bakery = this.bakeries.get(position);
            holder.bindBakery(bakery);
        }

        @Override
        public int getItemCount() {
            return this.bakeries.size();
        }
    }

    public class FollowHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView Name,date;
        private final TextView profile_product_id1;
        private FollowModel bakery;
        private Context context;

        public FollowHolder(Context context, View itemView) {

            super(itemView);
            this.context = context;
            this.date = (TextView) itemView.findViewById(R.id.follow_date);
            this.Name = (TextView) itemView.findViewById(R.id.follow_name);
            this.profile_product_id1 = (TextView) itemView.findViewById(R.id.follow_id);
            itemView.setOnClickListener(this);
        }

        public void bindBakery(FollowModel bakery) {
            this.bakery = bakery;
            this.Name.setText(bakery.getName());
            this.profile_product_id1.setText(bakery.getId());
            this.date.setText(bakery.getDate());
        }

        @Override
        public void onClick(View v) {
            if (this.bakery != null) {
                ids=this.bakery.getId();
                FollowFarward();
            }
        }
    }
    public void FollowFarward(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_forwardclient,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getBoolean("status")) {
                                try {
                                    message = obj.getString("message");
                                    ShowPopup();
                                }catch (NullPointerException e){
                                    e.printStackTrace();
                                }
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
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", ids);
                return params;
            }
        };
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    public void ShowPopup() {
        TextView txtclose;
        TextView add_d;
        Button btnFollow;

        myDialog.setContentView(R.layout.check_msg);
        myDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        add_d =(TextView) myDialog.findViewById(R.id.credit_amt);
        add_d.setText(message);

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