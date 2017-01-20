package com.perez.schedulebynfc;


import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.List;

import Support.LocalEventService;
import Support.LocalTime;

/**
 * Created by User on 07/10/2016.
 */

public class RegisterNfc {
    private static List<Long>  listEventsDay;
    private static LocalEventService lEventService;
    private static RegisterNfc instance = null;

    protected RegisterNfc() {
        // Exists only to defeat instantiation.
    }

    public static RegisterNfc getInstance() {
        if(instance == null) {
            instance = new RegisterNfc();
        }
        return instance;
    }


    /* Other methods protected by singleton-ness */
    public void newNfcDetected(WeakReference<Context> mWeakRefContext, long calendarID, long currentMilleseconds) {

        startLocalEvent(mWeakRefContext);
        //System.out.println("currentMilleseconds= "  + currentMilleseconds);


        listEventsDay = getEvents();

        //create new event
        if (listEventsDay.isEmpty()) {
            //lista de eventos para este dia está vazia
            //criar novo evento para este dia
            //checkYesterdayLastEvent();
            System.out.println("Day event list empty");
            System.out.println(LocalTime.getHour(currentMilleseconds) + " : " +LocalTime.getMinute(currentMilleseconds)+" - "+LocalTime.getDay(currentMilleseconds) + "/ " + LocalTime.getMonth(currentMilleseconds) + " / " + LocalTime.getYear(currentMilleseconds) );
            createEvent(0, calendarID, currentMilleseconds);

            customToast(mWeakRefContext, "Entrada - NFC");

        } else {
            //lista de eventos para este dia nao está vazia
            //analisar ultimo evento
            long eventID = isLastEventClose();

            if (eventID < 1) { //yes
                //ultimo evento esta fechado e é necessário criar novo evento
                //criar novo evento para este dia
                System.out.println("LAST event close - create event");
                createEvent(listEventsDay.size(), calendarID, currentMilleseconds);
                customToast(mWeakRefContext, "Entrada - NFC");
            } else { //no
                //ultimo evento está aberto, necessário fecha-lo
                System.out.println("LAST event open - close event");
                closeEvent(eventID);
                customToast(mWeakRefContext, "Saida - NFC");
            }
        }
        updateWidget(mWeakRefContext);

    }

    private void customToast(final WeakReference<Context> mWeakRefContext, final String message){
        Handler h = new Handler(mWeakRefContext.get().getMainLooper());

        h.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mWeakRefContext.get(), message, Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void updateWidget(WeakReference<Context> mWeakRefContext) {
        new MyWidgetProvider.WidgetUpdate().Update(mWeakRefContext);
     /*   if(mWeakRefContext != null && mWeakRefContext.get() != null){
            Context c = mWeakRefContext.get();
            Intent intent = new Intent(c,MyWidgetProvider.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            System.out.println("call updatexpto");
            int ids[] = AppWidgetManager.getInstance(c.getApplicationContext()).getAppWidgetIds(new ComponentName(c.getApplicationContext(), MyWidgetProvider.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            c.sendBroadcast(intent);
        }*/

    }

    private void startLocalEvent(WeakReference<Context> mWeakRefContext) {

        lEventService = new LocalEventService(mWeakRefContext);
    }

    private void checkYesterdayLastEvent() {
        //lEventService.checkYesterdayLastEvent();
    }



    private void createEvent(int i, long calendarID, long currentMilliseconds) {
        lEventService.createNewEvent(i+1, calendarID, currentMilliseconds);
    }

    private void closeEvent(long eventID) {
        lEventService.closeEvent(eventID);
    }

    private long isLastEventClose() {
        return lEventService.isLastEventClose();
    }

    private List<Long> getEvents() {
        List<Long> listEvents;

        listEvents = lEventService.getEventsIDsSpecificDay();

        return listEvents;
    }

    public void release(){
        instance = null;
        lEventService = null;
        listEventsDay.clear();
        listEventsDay = null;
    }
}
