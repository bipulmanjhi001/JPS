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

public class AccountAdapter  extends BaseAdapter {
    private Context mContext;
    ArrayList<AccountModel> mylist = new ArrayList<AccountModel>();

    public AccountAdapter(ArrayList<AccountModel> itemArray, Context mContext) {
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
        private TextView id,date,amount;
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

                convertView = inflator.inflate(R.layout.account_list_row, null);
                view.id = (TextView) convertView.findViewById(R.id.id);
                view.date = (TextView) convertView.findViewById(R.id.date);
                view.amount = (TextView) convertView.findViewById(R.id.amount);

                convertView.setTag(view);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            view = (ViewHolder) convertView.getTag();
        }
        try {
            view.id.setTag(position);
            view.date.setText("Date : "+mylist.get(position).getDate());
            view.amount.setText("Amount : "+mylist.get(position).getAmount());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return convertView;
    }
}

