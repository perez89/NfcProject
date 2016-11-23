package com.perez.schedulebynfc;


import android.content.Context;

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

        } else {
            //lista de eventos para este dia nao está vazia
            //analisar ultimo evento
            long eventID = isLastEventClose();
            if (eventID < 1) { //yes
                //ultimo evento esta fechado e é necessário criar novo evento
                //criar novo evento para este dia
                System.out.println("LAST event close - create event");
                createEvent(listEventsDay.size(), calendarID, currentMilleseconds);
            } else { //no
                //ultimo evento está aberto, necessário fecha-lo
                System.out.println("LAST event open - close event");
                closeEvent(eventID);
            }
        }
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
