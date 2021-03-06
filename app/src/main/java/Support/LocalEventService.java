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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static java.lang.Long.parseLong;

/**
 * Created by User on 17/10/2016.
 */

public class LocalEventService {
    static WeakReference<Context> context;

    public LocalEventService(WeakReference<Context> c) {
        this.context = c;
    }

    public long isLastEventClose() {
        LocalEvent localEvent;
        localEvent = getLastEvent();
        //System.out.println("inicio= " + localEvent.getlDate().get.getStartTime() + " | fim= " + localEvent.getEndTime());

        if (localEvent != null) {
            if (!localEvent.isClose())
                return localEvent.getEventID();
        }
        return -1;
    }

    private LocalEvent getLastEvent() {
        LocalEvent localEvent = null;
        if (context != null && context.get() != null) {
            long idCalendar = LocalCalendar.getIdCalendar(context.get());

            if (idCalendar > 0) {
                // List<Integer> listIdEvents = new ArrayList<Integer>();
       /*         Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 0, 0, 0);
                //long startDay = calendar.getTimeInMillis();
                calendar.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, 23, 59, 59);
                //long endDay = calendar.getTimeInMillis();
                LocalTime.DayRange dayRange = new LocalTime.DayRange();
                long startDay = dayRange.getStartDay();
                long endDay = dayRange.getEndDay();*/
                long currentTime = LocalTime.getCurrentMilliseconds();

                String[] projection = new String[]{BaseColumns._ID, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.CALENDAR_TIME_ZONE, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION};
                String selection = CalendarContract.Events.DTSTART + " < ? AND " + CalendarContract.Events.DTEND + "< ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
                String[] selectionArgs = new String[]{Long.toString(currentTime), Long.toString(currentTime), Long.toString(idCalendar)};

                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return ;
                }

                Cursor cur = context.get().getContentResolver().query(CalendarContract.Events.CONTENT_URI,
                        projection,
                        selection,
                        selectionArgs,
                        CalendarContract.Events.DTSTART + " DESC LIMIT 1");

                if (cur != null) {


                    //avancar para ultimo evento
                    cur.moveToFirst();

                    long eventID = cur.getLong(0);
                    long startTime = cur.getLong(1);
                    long endTime = cur.getLong(2);
                    String timeZone = cur.getString(3);
                    String title = cur.getString(4);
                    String description = cur.getString(5);

                    localEvent = new LocalEvent(eventID, startTime, endTime, timeZone, title, description);

                }
                cur.close();
            }
        }
        return localEvent;

    }

    public void createNewEvent(int eventNumbDay, long calendarID, long currentMilleseconds) {
        if (context != null && context.get() != null) {
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, currentMilleseconds);
            values.put(CalendarContract.Events.DTEND, currentMilleseconds);
            values.put(CalendarContract.Events.TITLE, "Work - " + eventNumbDay);
            values.put(CalendarContract.Events.DESCRIPTION, "Open - Using App");
            values.put(CalendarContract.Events.CALENDAR_ID, calendarID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault() + "");
            values.put(CalendarContract.Events.EVENT_END_TIMEZONE, TimeZone.getDefault() + "");


            if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            ContentResolver cr = context.get().getContentResolver();
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);

            // get the event ID that is the last element in the Uri
            long eventID = parseLong(uri.getLastPathSegment());
            //
            // ... do something with event ID
            //
            //
        }

    }

    public void closeEvent(long eventID) {
        if (context != null && context.get() != null) {
            long idCalendar = LocalCalendar.getIdCalendar(context.get());

            if (idCalendar > 0) {
                long startTimeEvent = getEventStartTime(eventID);
                long currentMilliseconds = LocalTime.getCurrentMilliseconds();
                ContentResolver cr = context.get().getContentResolver();
                ContentValues values = new ContentValues();

                // change the hour (close) of last event

                values.put(CalendarContract.Events.DESCRIPTION, "Close - Using App");
                values.put(CalendarContract.Events.DTSTART, startTimeEvent);
                values.put(CalendarContract.Events.DTEND, currentMilliseconds);
                // values.put(CalendarContract.Events.DTEND, ""+currentMilliseconds);
                // Uri updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID);
                String where = CalendarContract.Events._ID + " = " + eventID + " AND " + CalendarContract.Events.CALENDAR_ID + " = " + idCalendar;


                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
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
        }
    }

    public List<Long> getEventsIDsSpecificDay() {
        if (context != null && context.get() != null) {
            long idCalendar = LocalCalendar.getIdCalendar(context.get());
            List<Long> listIdEvents = new ArrayList<Long>();
           // System.out.println("getEventsIDsSpecificDay id= " + idCalendar);
            if (idCalendar > 0) {
                LocalTime.DayRange dayRange = new LocalTime.DayRange();
                long startDay = dayRange.getStartDay();
                long endDay = dayRange.getEndDay();
                //86400000 -> number of milleseconds of a day
             //   System.out.println("startDay= " + startDay + " | endDay= " + endDay + " | MainActivity.getIdCalendar()= " + idCalendar);
                String[] projection = new String[]{BaseColumns._ID};
                String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + "< ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
                String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay), Long.toString(idCalendar)};

                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return ;
                }
                Cursor cur = context.get().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);
         //       System.out.println("1 - ABCDEF");
                while (cur.moveToNext()) {
           //         System.out.println("2 - ABCDEF");
                    listIdEvents.add(cur.getLong(0));
                }

                cur.close();
            }
            return listIdEvents;
        }
        return null;
    }


    private long getEventStartTime(long idEvent) {

        if (context != null && context.get() != null) {
            long idCalendar = LocalCalendar.getIdCalendar(context.get());
            if (idCalendar > 0) {
                String[] projection =
                        new String[]{
                                CalendarContract.Events.DTSTART
                        };
                String selection = CalendarContract.Events._ID + " = ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
                String[] selectionArgs = new String[]{Long.toString(idEvent), Long.toString(idCalendar)};

                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                }
                Cursor calCursor =
                        context.get().getContentResolver().
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
        }
        return 0;
    }

    public void deleteEvents(long startTime, long endTime) {
        if (context != null && context.get() != null) {
            List<Long> listEvents = getEventsIDsSpecificDay();

            ContentResolver cr = context.get().getContentResolver();
            //String selection = CalendarContract.Events.DTSTART + " >=" + startTime +" AND " + CalendarContract.Events.DTEND + "<" + endTime;

            Uri deleteUri = null;
            for (Long i :
                    listEvents) {
                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, i);
                int rows = cr.delete(deleteUri, null, null);
            }

            //Log.i(DEBUG_TAG, "Rows deleted: " + rows);
        }
    }

    public List<LocalEvent> getEventsForDay(long startDay) {
        if (context != null && context.get() != null) {
            long idCalendar = LocalCalendar.getIdCalendar(context.get());
            List<LocalEvent> listOfEvents = new ArrayList<LocalEvent>();
            if (idCalendar > 0) {

                String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.CALENDAR_TIME_ZONE, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION};
                String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + "<= ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
                String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(startDay), Long.toString(idCalendar)};

                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return ;
                }

                Cursor cursor = context.get().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);

                if (cursor == null || cursor.getCount() == 0) {

                } else {

                }
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(0);
                    long dtStart = cursor.getLong(1);
                    long dtEnd = cursor.getLong(2);
                    String timeZone = cursor.getString(3);
                    String title = cursor.getString(4);
                    String description = cursor.getString(5);
                    LocalEvent event;

                    event = new LocalEvent(id, dtStart, dtEnd, timeZone, title, description);
                    listOfEvents.add(event);
                }

                cursor.close();
            }
            return listOfEvents;
        }
        return null;
    }

    public List<LocalEvent> getEventsForDay(long startDay, long endDay) {
        if (context != null && context.get() != null) {
            long idCalendar = LocalCalendar.getIdCalendar(context.get());
            List<LocalEvent> listOfEvents = new ArrayList<LocalEvent>();
            if (idCalendar > 0) {

                String[] projection = new String[]{CalendarContract.Events._ID, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.CALENDAR_TIME_ZONE, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION};
                String selection = CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + "< ? AND " + CalendarContract.Events.CALENDAR_ID + " = ?";
                String[] selectionArgs = new String[]{Long.toString(startDay), Long.toString(endDay), Long.toString(idCalendar)};

                if (ActivityCompat.checkSelfPermission(context.get(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return ;
                }

                Cursor cursor = context.get().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, null);

                if (cursor == null || cursor.getCount() == 0) {

                } else {


                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(0);
                        long dtStart = cursor.getLong(1);
                        long dtEnd = cursor.getLong(2);
                        String timeZone = cursor.getString(3);
                        String title = cursor.getString(4);
                        String description = cursor.getString(5);
                        LocalEvent event;

                        event = new LocalEvent(id, dtStart, dtEnd, timeZone, title, description);
                        listOfEvents.add(event);
                    }
                }
                cursor.close();
            }
            return listOfEvents;
        }
        return null;
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
            LocalEvent event = new LocalEvent(calCursor.getLong(0),calCursor.getLong(1),calCursor.getLong(2));
            if(!(event.isClose())){

            }
        }
        calCursor.close();
    }*/
}
