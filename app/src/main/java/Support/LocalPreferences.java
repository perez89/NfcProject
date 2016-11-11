package Support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by User on 18/10/2016.
 */

public class LocalPreferences {
    private static LocalPreferences instance = null;
    private LocalPreferences() {
        // Exists only to defeat instantiation.
    }

    public static LocalPreferences getInstance() {
        if(instance == null) {
            instance = new LocalPreferences();
        }
        return instance;
    }

    public void setPreference(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getPreference(String key, Context context) {
        String value;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        value = preferences.getString(key, null);

        if(value != null)
            return value;
        else
            return null;
    }
}
