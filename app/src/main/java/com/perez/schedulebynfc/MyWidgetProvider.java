package com.perez.schedulebynfc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;

import Support.EventClass;
import Support.LocalEventService;
import Support.LocalTime;

import static Support.LocalTime.getCurrentMilliseconds;
import static Support.LocalTime.getFormatTime;

/**
 * Created by User on 26/12/2016.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_CLICK = "ACTION_CLICK";
    private TextView tvDayTime, tvWeekTime, tvWeekMonth;
    private long currentDayTime;
    private long currentWeekTime;
    private long currentMonthTime;
    private boolean working;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        initializeVariables();

        System.out.println("updatexpto called - widget");
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        // create some random data
        //TODO get real time
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        // Set the text
        loadTime(context);

        //text day
        String whileTimeLessThan1 = getFormatTime(currentDayTime);
        if (whileTimeLessThan1.equals("-") && working)
            whileTimeLessThan1 = "1";
        remoteViews.setTextViewText(R.id.tvUpdateDay, whileTimeLessThan1);

        //text week
        whileTimeLessThan1 = getFormatTime(currentWeekTime);
        if (whileTimeLessThan1.equals("-") && working)
            whileTimeLessThan1 = "1";
        remoteViews.setTextViewText(R.id.tvUpdateWeek, whileTimeLessThan1);

        //text month
        whileTimeLessThan1 = getFormatTime(currentMonthTime);
        if (whileTimeLessThan1.equals("-") && working)
            whileTimeLessThan1 = "1";

        remoteViews.setTextViewText(R.id.tvUpdateMonth, whileTimeLessThan1);
        if (working)
            remoteViews.setViewVisibility(R.id.ivWorking, View.VISIBLE);
        else
            remoteViews.setViewVisibility(R.id.ivWorking, View.INVISIBLE);
        // Register an onClickListener
        Intent intent = new Intent(context, MyWidgetProvider.class);

        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.llWidgetOnClick, pendingIntent);
        for (int widgetId : allWidgetIds) {
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    private void initializeVariables() {
        currentDayTime = 0;
        currentWeekTime = 0;
        currentMonthTime = 0;
        working = false;
    }

    void loadTime(Context context) {
        LocalEventService lEventService = new LocalEventService(new WeakReference<Context>(context));
        long milli = getCurrentMilliseconds();
        int secs = LocalTime.getSeconds(milli);
        int week = LocalTime.getWeekOfMonth(milli);
        int year = LocalTime.getYear(milli);
        int month = LocalTime.getMonth(milli);
        int day = LocalTime.getDay(milli);


        int numOfDays = LocalTime.getNumberDaysMonth(year, month);
        LocalTime.DateString dataString = new LocalTime.DateString(year + "", (month + 1) + "", "", "", "", "");

        long timeStartOfMonth = 0;

        try {
            timeStartOfMonth = dataString.getMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millisecondsDay = 86400000;

        if (timeStartOfMonth > 0) {

            long time = timeStartOfMonth;
            List<EventClass> listOfEvents = null;
            //System.out.println("run background-inside-timeStartOfMonth="+time);
            for (int i = 0; i < numOfDays; i++) {

                int localWeek = LocalTime.getWeekOfMonth(time);
                int localDay = LocalTime.getDay(time);
                long timeEnd = time + millisecondsDay;
                listOfEvents = lEventService.getEventsForDay(time, timeEnd);
                //if(listOfEvents.get())
                //  System.out.println("dayweekyear2= " +month + " " +localWeek+ " " +localDay);
                long totalDayTime = getTotalDayTime(listOfEvents);
                String xpto = getFormatTime(totalDayTime);
                // System.out.println("xpto day= " + localDay + " | time= " + xpto);

                if (day == localDay) {

                    if (listOfEvents.size() > 0 && !(listOfEvents.get(listOfEvents.size() - 1).isClose())) {
                        totalDayTime = totalDayTime + LocalTime.getCurrentMilliseconds() - listOfEvents.get(listOfEvents.size() - 1).getData().getStartTime();
                        working = true;
                    } else {
                        working = false;
                    }
                    currentDayTime = totalDayTime;
                }


                if (week == localWeek)
                    currentWeekTime = currentWeekTime + totalDayTime;

                   /* if(totalDayTime<minTime)
                        minTime=(int)(long)totalDayTime;

                    if(totalDayTime>maxTime)
                        maxTime=(int)(long)totalDayTime;*/

                currentMonthTime = currentMonthTime + totalDayTime;

                time = time + millisecondsDay;


            }
            System.out.println(">>>> " + currentDayTime + " " + currentWeekTime + " " + currentMonthTime);
            listOfEvents.clear();
            listOfEvents = null;
        }
        lEventService = null;
    }

    private long getTotalDayTime(List<EventClass> listOfEvents) {
        long total = 0;
        for (int i = 0; i < listOfEvents.size(); i++) {
            total = total + listOfEvents.get(i).getData().getDuration();
        }
        return total;
    }

    public static class WidgetUpdate {
        public void Update(WeakReference<Context> mWeakRefContext) {
            if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                Context c = mWeakRefContext.get();
                Intent intent = new Intent(c, MyWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                System.out.println("call updatexpto");
                int ids[] = AppWidgetManager.getInstance(c.getApplicationContext()).getAppWidgetIds(new ComponentName(c.getApplicationContext(), MyWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                c.sendBroadcast(intent);
            }

        }
    }
}
