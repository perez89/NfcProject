package Support;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;

import com.perez.schedulebynfc.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Long.parseLong;

/**
 * Created by User on 17/10/2016.
 */

public class LocalEventService {
    static Context context;

    public LocalEventService(Context c) {
        this.context = c;
    }

    public long isLastEventClose() {
        EventClass eventClass;
        eventClass = getLastEvent();
        //System.out.println("inicio= " + eventClass.getlDate().get.getStartTime() + " | fim= " + eventClass.getEndTime());

        if (eventClass != null) {
            if (!eventClass.isClose())
                return eventClass.getEventID();
        }
        return -1;
    }

    private EventClass getLastEvent() {
        EventClass eventClass = null;

        // List<Integer> listIdEvents = new ArrayList<Integer>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 0, 0, 0);
        //long startDay = calendar.getTimeInMillis();
        calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 23, 59, 59);
        //long endDay = calendar.getTimeInMillis();
        LocalTime.DayRange dayRange = new LocalTime.DayRange();
        long startDay = dayRange.getStartDay();
        long endDay = dayRange.getEndDay();


        String[] projection = new String[]{BaseColumns._ID, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTSTART + "< ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay), Long.toString(MainActivity.getIdCalendar())};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return ;
        }

        Cursor cur = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                CalendarContract.Events.DTSTART + " DESC LIMIT 1");

        if (cur != null) {
            long eventID = 0;
            long startTime = 0;
            long endTime = 0;

            //avancar para ultimo evento
            cur.moveToFirst();

            eventID = cur.getLong(0);
            startTime = cur.getLong(1);
            endTime = cur.getLong(2);

            eventClass = new EventClass(eventID, startTime, endTime);

        }
        cur.close();
        return eventClass;
    }

    public void createNewEvent(int eventNumbDay, long calendarID, long currentMilleseconds) {

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, currentMilleseconds);
        values.put(CalendarContract.Events.DTEND, currentMilleseconds);
        values.put(CalendarContract.Events.TITLE, "Work - " + eventNumbDay);
        values.put(CalendarContract.Events.DESCRIPTION, "Open - " + eventNumbDay);
        values.put(CalendarContract.Events.CALENDAR_ID, calendarID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault() + "");
        values.put(CalendarContract.Events.EVENT_END_TIMEZONE, TimeZone.getDefault() + "");

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
        ContentResolver cr = context.getContentResolver();
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = parseLong(uri.getLastPathSegment());
        //
        // ... do something with event ID
        //
        //

    }

    public void closeEvent(long eventID) {

        long startTimeEvent = getEventStartTime(eventID);
        long currentMilliseconds = LocalTime.getCurrentMilliseconds();
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();

        // change the hour (close) of last event

        values.put(CalendarContract.Events.DESCRIPTION, "CLOSE - " + eventID);
        values.put(CalendarContract.Events.DTSTART, startTimeEvent);
        values.put(CalendarContract.Events.DTEND, currentMilliseconds);
        // values.put(CalendarContract.Events.DTEND, ""+currentMilliseconds);
        // Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
        String where = CalendarContract.Events._ID + " = " + eventID + " AND " + CalendarContract.Events.CALENDAR_ID + " = " + MainActivity.getIdCalendar();


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

        int rows = cr.update(CalendarContract.Events.CONTENT_URI, values, where, null);

    }

    public List<Long> getEventsIDsSpecificDay() {
        List<Long> listIdEvents = new ArrayList<Long>();

        LocalTime.DayRange dayRange = new LocalTime.DayRange();
        long startDay = dayRange.getStartDay();
        long endDay = dayRange.getEndDay();
        //86400000 -> number of milleseconds of a day

        String[] projection = new String[]{BaseColumns._ID};
        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + "< ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay), Long.toString(MainActivity.getIdCalendar())};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return ;
        }
        Cursor cur = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);
        // System.out.println("1 - ABCDEF");
        while (cur.moveToNext()) {
            // System.out.println("2 - ABCDEF");
            listIdEvents.add(cur.getLong(0));
        }

        cur.close();
        return listIdEvents;
    }

    private long getEventStartTime(long idEvent) {
        String[] projection =
                new String[]{
                        CalendarContract.Events.DTSTART
                };
        String selection = CalendarContract.Events._ID + " = ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(idEvent), Long.toString(MainActivity.getIdCalendar())};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
        }
        Cursor calCursor =
                context.getContentResolver().
                        query(CalendarContract.Events.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                CalendarContract.Events._ID + " ASC");
        calCursor.moveToFirst();
        long startTime = parseLong(calCursor.getString(0));
        calCursor.close();
        return startTime;
    }

    public void deleteEvents(long startTime, long endTime) {
        List<Long> listEvents = getEventsIDsSpecificDay();

        ContentResolver cr = context.getContentResolver();
        //String selection = CalendarContract.Events.DTSTART + " >=" + startTime +" AND " + CalendarContract.Events.DTEND + "<" + endTime;

        Uri deleteUri = null;
        for (Long i :
                listEvents) {
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, i);
            int rows = cr.delete(deleteUri, null, null);
        }

        //Log.i(DEBUG_TAG, "Rows deleted: " + rows);
    }

    public List<EventClass> getEventsForDay(long startDay, long endDay) {
        List<EventClass> listOfEvents = new ArrayList<EventClass>();
        String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + "< ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
        String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay), Long.toString(MainActivity.getIdCalendar())};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            //return ;
        }
        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);

        if(cursor == null || cursor.getCount() == 0){

        }else{

        }
        while (cursor.moveToNext()) {
            long id = cursor.getLong(0);
            long dtStart = cursor.getLong(1);
            long dtEnd = cursor.getLong(2);
            EventClass event;

            event = new EventClass(id, dtStart, dtEnd);
            listOfEvents.add(event);
        }

        cursor.close();
        return listOfEvents;
    }

//cancelar esta ideia por agora, a ideia inicial seria fechar o evento de um dia anterior automaticamente
   /* public void checkYesterdayLastEvent() {
        LocalTime.DayRange dayRange = new LocalTime.DayRange();
        long startDay =  dayRange.getStartDay();
        String[] projection =
                new String[]{
                        CalendarContract.Events._ID,
                        CalendarContract.Events.DTSTART,
                        CalendarContract.Events.DTEND
                };
        String selection = CalendarContract.Events.CALENDAR_ID + " = ? AND " + CalendarContract.Events.DTSTART + " < ? ";
        String[] selectionArgs = new String[]{Long.toString(MainActivity.getIdCalendar()), Long.toString(startDay)};

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
        }
        Cursor calCursor =
                context.getContentResolver().
                        query(CalendarContract.Events.CONTENT_URI,
                                projection,
                                selection,
                                selectionArgs,
                                CalendarContract.Events._ID + " DESC");
        if(calCursor!=null){
            EventClass event = new EventClass(calCursor.getLong(0),calCursor.getLong(1),calCursor.getLong(2));
            if(!(event.isClose())){

            }
        }
        calCursor.close();
    }*/
}
