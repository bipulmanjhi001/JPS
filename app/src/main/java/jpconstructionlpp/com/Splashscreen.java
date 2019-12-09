package jpconstructionlpp.com;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import com.skyfishjy.library.RippleBackground;
import jpconstructionlpp.com.model.ConnectivityReceiver;
import jpconstructionlpp.com.model.RajApplication;

public class Splashscreen extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
        ImageView imageView=(ImageView)findViewById(R.id.centerImage);
        rippleBackground.startRippleAnimation();
        checkConnection();
    }
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } finally {
                        Intent openMain = new Intent(Splashscreen.this, Login.class);
                        startActivity(openMain);
                        finish();
                    }
                }
            };
            timer.start();
        } else  {
            message = "connect your internet.";
            color = Color.RED;
            Toast toast=Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT);
            toast.show();

            Intent openMain = new Intent(Splashscreen.this, Login.class);
            startActivity(openMain);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RajApplication.getInstance().setConnectivityListener(this);
    }
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
    @Override
    public void onBackPressed() {
        backButtonHandler();
        return;
    }

    public void backButtonHandler() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Splashscreen.this);
        alertDialog.setTitle("Leave application?");
        alertDialog.setMessage("Are you sure you want to leave the application?");
        alertDialog.setIcon(R.mipmap.ic_launcher_round);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Splashscreen.this.finish();
                    }
                });
        alertDialog.setNegativeButton("NO",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }
}
