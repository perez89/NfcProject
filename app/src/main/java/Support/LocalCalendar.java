package Support;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;

import java.util.TimeZone;

import static android.R.attr.id;
import static android.provider.CalendarContract.*;

/**
 * Created by User on 10/10/2016.
 */

public class LocalCalendar {
    private static final String NAME_CALENDAR = "NFC_SCHEDULE_WORK";
    private static final String ACCOUNT_NAME = "NFC Work Schedule";
    private static final String CALENDAR_DISPLAY_NAME = "Calendar NFC work";

    public static long createCalendar(Context context) {
        // System.out.println("createCalendar");
        ContentValues values = new ContentValues();
        values.put(
                Calendars.ACCOUNT_NAME,
                ACCOUNT_NAME);
        values.put(
                Calendars.ACCOUNT_TYPE,
                ACCOUNT_TYPE_LOCAL);
        values.put(
                Calendars.NAME,
                NAME_CALENDAR);
        values.put(
                Calendars.CALENDAR_DISPLAY_NAME,
                CALENDAR_DISPLAY_NAME);
        values.put(
                Calendars.CALENDAR_COLOR,
                0x33FF00);
        values.put(
                Calendars.CALENDAR_ACCESS_LEVEL,
                Calendars.CAL_ACCESS_READ);
              //  Calendars.CAL_ACCESS_OWNER);
        values.put(
                Calendars.OWNER_ACCOUNT,
                "some.account@googlemail.com");
        values.put(
                Calendars.CALENDAR_TIME_ZONE,
                TimeZone.getDefault() + "");
        //"Europe/Lisbon");
        values.put(
                Calendars.SYNC_EVENTS,
                1);

        Uri uri = context.getContentResolver().insert(buildUri(), values);

        long idCalendar = Long.valueOf(uri.getLastPathSegment());
        //   System.out.println("Calendar created ID= " + idCalendar);

        if (idCalendar > 0) {
            setPrefsIdCalendar(idCalendar, context);
            return idCalendar;
        } else {
            return 0;
        }

    }

    public static Uri buildUri() {
        Uri.Builder builder =
                Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                Calendars.ACCOUNT_NAME,
                "com.perez");
        builder.appendQueryParameter(
                Calendars.ACCOUNT_TYPE,
                ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CALLER_IS_SYNCADAPTER,
                "true");
        return builder.build();
    }

    public static void updateCalendarAction(Context context) {
        ContentValues values = new ContentValues();

        values.put(
                Calendars.CALENDAR_ACCESS_LEVEL,
                Calendars.CAL_ACCESS_READ);


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        int uri = context.getContentResolver().update(buildUri(), values, Calendars.CALENDAR_DISPLAY_NAME + "=?", new String[]{CALENDAR_DISPLAY_NAME});
    }
    public static long getCalendars(Context context) {
        String[] projection =
                new String[]{
                        Calendars._ID,
                        Calendars.CALENDAR_DISPLAY_NAME,
                        Calendars.CALENDAR_LOCATION,
                        Calendars.ACCOUNT_TYPE,
                        Calendars.ACCOUNT_NAME,
                        Calendars.VISIBLE,
                        Calendars.CALENDAR_ACCESS_LEVEL,
                        Calendars.OWNER_ACCOUNT
                };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Cursor calCursor =
                context.getContentResolver().
                        query(Calendars.CONTENT_URI,
                                projection,
                                null,
                                null,
                                Calendars._ID + " ASC");
        while (calCursor.moveToNext()) {
            long id = calCursor.getLong(0);
            String calendarDisplay = calCursor.getString(1);
            String location = calCursor.getString(2);
            String accType = calCursor.getString(3);
            String accName = calCursor.getString(4);
            int vis = calCursor.getInt(5);
            String calAccess = calCursor.getString(6);
            String owner = calCursor.getString(7);

            System.out.println("id= " + id + "  | calendarDisplay= " + calendarDisplay + "  | location= " + location + "  | type= " + accType +
                   "  | AccName= " + accName + "  | vis= " + vis + "  | calAccess= " + calAccess + "  | owner= " + owner);
        }
        calCursor.close();
        return id;
    }


    /*
    public static long verifyCalendar(Context context){
        System.out.println("1 - verify calendar - id -> ");
        long id = -1;
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                };
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND " + CalendarContract.Calendars.NAME + " >= ? AND " + CalendarContract.Calendars.VISIBLE + " = ?";
        String[] selectionArgs = new String[]{ACCOUNT_NAME , NAME_CALENDAR , "1"};


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }

        Cursor calCursor =
                context.getContentResolver().
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                CalendarContract.Calendars._ID + " ASC");
        if(calCursor.getCount() > 0){
            calCursor.moveToFirst();
            id = calCursor.getInt(0);
            String nameC = calCursor.getString(1);
            System.out.println("2 - verify calendar - id -> " + id + "name-> " + nameC);
            calCursor.close();
            return id;
        }
        //if (calCursor.moveToFirst()) {
          //  do {
            //    id = calCursor.getInt(0);
              //  String displayName = calCursor.getString(1);
               // if (displayName.equals(NAME_CALENDAR))

                //System.out.println(id + " < > " + displayName);
            //} while (calCursor.moveToNext());
        //}
        System.out.println("3 - verify calendar - id -> " + id);
        calCursor.close();
        return id;
    }
*/

    public static void deleteCalendarUnderSameAccount(Context context) {
        //Uri uri = context.getContentResolver().insert(builder.build(), values);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        context.getContentResolver().delete(Calendars.CONTENT_URI, Calendars.CALENDAR_DISPLAY_NAME + "=?", new String[]{CALENDAR_DISPLAY_NAME});
        // context.getContentResolver().delete(CalendarContract.Calendars.CONTENT_URI, CalendarContract.Calendars._ID+"=?" , new String[] {"9"} );

        LocalPreferences.getInstance().setPreference(LocalPreferences.ID_CALENDAR, "8", context);
    }


    public static long getIdCalendarFromCalendars(Context context) {
        long id = 0;
        String[] projection =
                new String[]{
                        Calendars._ID,
                        Calendars.CALENDAR_DISPLAY_NAME
                };
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        String selection = Calendars.NAME + " = ? ";
        String[] selectionArgs = new String[]{NAME_CALENDAR};

        Cursor calCursor =
                context.getContentResolver().
                        query(Calendars.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                Calendars._ID + " ASC");
        while (calCursor.moveToNext()) {
            id = calCursor.getLong(0);
            String calendarDisplay = calCursor.getString(1);
            break;
        }
        calCursor.close();
        return id;
    }

    public static long getIdCalendar(Context c) {
        String value = LocalPreferences.getInstance().getIdCalendarPreference(LocalPreferences.ID_CALENDAR, c);
        if (value == null || value.equals("")) {
            return LocalCalendar.createCalendar(c);
        } else {
            return Long.parseLong(value);
        }
    }

    public static void getEvents(Context context) {
        long idCalendar = getIdCalendar(context);
        if (idCalendar > 0) {


            String[] projection =
                    new String[]{
                            Events._ID,
                            Events.ACCOUNT_NAME,
                            Events.DTSTART,
                            Events.DTEND,
                            Events.OWNER_ACCOUNT,
                            Events.VISIBLE,
                            Events.ACCOUNT_TYPE,
                            Events.CALENDAR_ACCESS_LEVEL,
                            Events.CALENDAR_DISPLAY_NAME,
                            Events.CALENDAR_ID
                    };
            String selection = Events.CALENDAR_ID + " = ?";
            String[] selectionArgs = new String[]{Long.toString(idCalendar)};

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            }
            Cursor calCursor =
                    context.getContentResolver().
                            query(Events.CONTENT_URI,
                                    projection,
                                    selection,
                                    selectionArgs,
                                    Events._ID + " DESC");
            while (calCursor.moveToNext()) {
                long id = calCursor.getLong(0);
                String accName = calCursor.getString(1);
                long start = calCursor.getLong(2);
                long end = calCursor.getLong(3);
                String owner = calCursor.getString(4);
                int vis = calCursor.getInt(5);
                String accType = calCursor.getString(6);
                int level = calCursor.getInt(7);
                String display = calCursor.getString(8);
                idCalendar = calCursor.getLong(9);

                System.out.println("id= " + id + "  | accName= " + accName + "  | start= " + start + "  | end= " + end +
                        "  | owner= " + owner + "  | vis= " + vis + "  | accType= " + accType + "  | level= " + level + "  | display= " + display + " | idCalendar= " + idCalendar);
            }
            calCursor.close();
        }
    }

    private static void setPrefsIdCalendar(long idCalendar, Context context) {
        LocalPreferences.getInstance().setPreference(LocalPreferences.ID_CALENDAR, "" + idCalendar, context);
    }

    /*
    public static long getIdCalendar(Context context){
        String idCalendarValue;
        long idCalendar;
        idCalendarValue = getDefaults("idCalendar_Key", context);

        if(idCalendarValue != null){
            idCalendar = Long.parseLong(idCalendarValue);
            if(idCalendar > 0)
                return idCalendar;
        }

        return -1;
    }
*/


}
