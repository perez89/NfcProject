package Support;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.perez.schedulebynfc.MainActivity;

import java.util.TimeZone;

import static android.R.attr.id;
import static com.perez.schedulebynfc.MainActivity.getDefaults;
import static com.perez.schedulebynfc.MainActivity.setDefaults;

/**
 * Created by User on 10/10/2016.
 */

public class LocalCalendar {
    private static final String NAME_CALENDAR = "NFC_SCHEDULE_WORK";
    private static final String ACCOUNT_NAME = "NFC Work Schedule";
    private static final String CALENDAR_DISPLAY_NAME = "Calendar NFC work";

    public static long createCalendar(Context context) {
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME,
                ACCOUNT_NAME);
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                NAME_CALENDAR);
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CALENDAR_DISPLAY_NAME);
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0x33FF00);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                "some.account@googlemail.com");
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                TimeZone.getDefault() + "");
        //"Europe/Lisbon");
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                "com.perez");
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri = context.getContentResolver().insert(builder.build(), values);

        long idCalendar = Long.valueOf(uri.getLastPathSegment());
        System.out.println("Calendar created ID= " + idCalendar);

        setIdCalendar(idCalendar, context);
        return idCalendar;
    }

    public static long getCalendars(Context context) {
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Calendars.CALENDAR_LOCATION,
                        CalendarContract.Calendars.ACCOUNT_TYPE,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.VISIBLE,
                        CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                        CalendarContract.Calendars.OWNER_ACCOUNT
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
                        query(CalendarContract.Calendars.CONTENT_URI,
                                projection,
                                null,
                                null,
                                CalendarContract.Calendars._ID + " ASC");
        while (calCursor.moveToNext()){
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

    public static void getEvents(Context context) {
        String[] projection =
                new String[]{
                        CalendarContract.Events._ID,
                        CalendarContract.Events.ACCOUNT_NAME,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND,
                        CalendarContract.Events.OWNER_ACCOUNT,
                        CalendarContract.Events.VISIBLE,
                        CalendarContract.Events.ACCOUNT_TYPE,
                        CalendarContract.Events.CALENDAR_ACCESS_LEVEL,
                        CalendarContract.Events.CALENDAR_DISPLAY_NAME,
                        CalendarContract.Events.CALENDAR_ID
                };
        String selection = CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(MainActivity.getIdCalendar())};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
        }
        Cursor calCursor =
                context.getContentResolver().
                        query(CalendarContract.Events.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                CalendarContract.Events._ID + " DESC");
        while (calCursor.moveToNext()){
            long id = calCursor.getLong(0);
            String accName = calCursor.getString(1);
            long start = calCursor.getLong(2);
            long end = calCursor.getLong(3);
            String owner = calCursor.getString(4);
            int vis = calCursor.getInt(5);
            String accType = calCursor.getString(6);
            int level = calCursor.getInt(7);
            String display = calCursor.getString(8);
            long idCalendar = calCursor.getLong(9);

            System.out.println("id= " + id + "  | accName= " + accName + "  | start= " + start + "  | end= " + end +
                    "  | owner= " + owner + "  | vis= " + vis + "  | accType= " + accType + "  | level= " + level + "  | display= " + display + " | idCalendar= " + idCalendar);
        }
        calCursor.close();
    }

    private static void setIdCalendar(long idCalendar, Context context){
        String idCalendarValue = String.valueOf(idCalendar);
        setDefaults("idCalendar_Key", idCalendarValue, context);
    }

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



}