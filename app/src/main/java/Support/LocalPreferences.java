package Support;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by User on 18/10/2016.
 */

public class LocalPreferences {
    public static final String ID_CALENDAR = "pref_calendar_id";
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
        else {
            long id = LocalCalendar.getIdCalendarFromCalendars(context);
            if(id>0){
                setPreference(LocalPreferences.ID_CALENDAR, "" + id, context);
                return ""+id;
            }

            return null;
        }
    }
}
