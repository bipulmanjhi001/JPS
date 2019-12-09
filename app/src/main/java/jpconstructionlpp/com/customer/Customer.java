package jpconstructionlpp.com.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import jpconstructionlpp.com.Login;
import jpconstructionlpp.com.R;
import jpconstructionlpp.com.customer.ui.dashboard.DashboardFragment;
import jpconstructionlpp.com.customer.ui.installment.InstallmentFragment;
import jpconstructionlpp.com.customer.ui.due.NotificationsFragment;

public class Customer extends AppCompatActivity {

    private Toolbar toolbar;
    TextView tooltext;
    Button logout;
    private static final String SHARED_PREF_NAME = "JPhpref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        tooltext=(TextView)findViewById(R.id.tooltext);
        tooltext.setText("Dashboard");

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        loadFragment(new DashboardFragment());

        logout=(Button)findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
                finish();
                Intent intent = new Intent(Customer.this, Login.class);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_dashboard:
                    tooltext.setText("Dashboard");
                    fragment = new DashboardFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_home:
                    tooltext.setText("View Installment");
                    fragment = new InstallmentFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_notifications:
                    tooltext.setText("Due Amount");
                    fragment = new NotificationsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
