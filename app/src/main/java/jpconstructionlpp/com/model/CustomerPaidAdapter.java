package jpconstructionlpp.com.model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import jpconstructionlpp.com.R;

public class CustomerPaidAdapter extends BaseAdapter {

    private Context mContext;
    ArrayList<CustomerPaidModel> mylist = new ArrayList<>();

    public CustomerPaidAdapter(ArrayList<CustomerPaidModel> itemArray, Context mContext) {
        super();
        this.mContext = mContext;
        mylist = itemArray;
    }

    @Override
    public int getCount() {
        return mylist.size();
    }

    @Override
    public String getItem(int position) {
        return mylist.get(position).toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder {
        private TextView id, name, p_name;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder view;
        LayoutInflater inflator = null;
        if (convertView == null) {
            view = new ViewHolder();
            try {

                inflator = ((Activity) mContext).getLayoutInflater();
                convertView = inflator.inflate(R.layout.customer_paid_row, null);
                view.id = (TextView) convertView.findViewById(R.id.id);
                view.name = (TextView) convertView.findViewById(R.id.name);
                view.p_name = (TextView) convertView.findViewById(R.id.p_name);

                convertView.setTag(view);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        } else {
            view = (ViewHolder) convertView.getTag();
        }
        try {
            view.id.setTag(position);
            view.id.setText(mylist.get(position).getId());
            view.name.setText("Date : " + mylist.get(position).getGetdate());
            view.p_name.setText("Amount : " + mylist.get(position).getAmount());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}