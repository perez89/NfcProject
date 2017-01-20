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

import Support.TimeCalculation;

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
        String durationText = getDisplayTimeForWidget(currentDayTime);

        remoteViews.setTextViewText(R.id.tvUpdateDay, ""+durationText);

        //text week
        durationText = getDisplayTimeForWidget(currentWeekTime);

        remoteViews.setTextViewText(R.id.tvUpdateWeek, ""+durationText);

        //text month_frag_show
        durationText = getDisplayTimeForWidget(currentMonthTime);

        remoteViews.setTextViewText(R.id.tvUpdateMonth, ""+durationText);

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

    private void loadTime(Context context) {
        TimeCalculation _timeCalculation = new TimeCalculation(new WeakReference<Context>(context));


        //int minTime = 999;
        //int maxTime = 0;
        currentDayTime = _timeCalculation.getDayTime();
        currentWeekTime = _timeCalculation.getWeekTime();
        currentMonthTime = _timeCalculation.getMonthTime();
        working = _timeCalculation.isWorking();


    }

    private String getDisplayTimeForWidget(long time){
        String text = getFormatTime(time);
        if (text.equals("-") || text.equals("0") ){
            if(working)
                text = "0:00";
            else
                text = "-";
        }
        return text;
    }

    private void initializeVariables() {
        currentDayTime = 0;
        currentWeekTime = 0;
        currentMonthTime = 0;
        working = false;
    }



    public static class WidgetUpdate {
        public void Update(WeakReference<Context> mWeakRefContext) {
            if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                Context c = mWeakRefContext.get();
                Intent intent = new Intent(c, MyWidgetProvider.class);
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

                int ids[] = AppWidgetManager.getInstance(c.getApplicationContext()).getAppWidgetIds(new ComponentName(c.getApplicationContext(), MyWidgetProvider.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                c.sendBroadcast(intent);
            }

        }
    }
}
