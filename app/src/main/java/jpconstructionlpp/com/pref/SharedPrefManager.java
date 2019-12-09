package jpconstructionlpp.com.pref;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import jpconstructionlpp.com.Login;

public class SharedPrefManager {

    private static final String SHARED_PREF_NAME = "JPhpref";
    private static final String KEY_BRANCH_ID= "designation";
    private static final String KEY_ID = "keyid";
    private static final String KEY_USERNAME = "username";
    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }
    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }
    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getName());
        editor.putString(KEY_BRANCH_ID, user.getBranch_id());
        editor.apply();
    }

    public boolean isLoggedIn() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID, null) != null;
    }

    public void logout() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        mCtx.startActivity(new Intent(mCtx, Login.class));
    }
}
