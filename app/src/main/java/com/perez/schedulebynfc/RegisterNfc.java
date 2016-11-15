package com.perez.schedulebynfc;


import android.content.Context;

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
    public void newNfcDetected(Context context, long calendarID, long currentMilleseconds) {
        startLocalEvent(context);
        System.out.println("newNfcDetected");


        listEventsDay = getEvents();

        //create new event
        if (listEventsDay.isEmpty()) {
            //lista de eventos para este dia está vazia
            //criar novo evento para este dia
            //checkYesterdayLastEvent();
            System.out.println("is empty");
            System.out.println(LocalTime.getHour(currentMilleseconds) + " : " +LocalTime.getMinute(currentMilleseconds)+" - "+LocalTime.getDay(currentMilleseconds) + "/ " + LocalTime.getMonth(currentMilleseconds) + " / " + LocalTime.getYear(currentMilleseconds) );
            createEvent(0, calendarID, currentMilleseconds);

        } else {
            System.out.println("NOT is empty");
            //lista de eventos para este dia nao está vazia
            //analisar ultimo evento
            long eventID = isLastEventClose();
            if (eventID < 1) { //yes
                //ultimo evento esta fechado e é necessário criar novo evento
                //criar novo evento para este dia
                System.out.println("NOT is empty - create event");
                createEvent(listEventsDay.size(), calendarID, currentMilleseconds);
            } else { //no
                //ultimo evento está aberto, necessário fecha-lo
                System.out.println("NOT is empty - close event");
                closeEvent(eventID);
            }
        }
    }

    private void startLocalEvent(Context context) {
        lEventService = new LocalEventService(context);
    }

    private void checkYesterdayLastEvent() {
        //lEventService.checkYesterdayLastEvent();
    }



    private void createEvent(int i, long calendarID, long currentMilleseconds) {
        lEventService.createNewEvent(i+1, calendarID, currentMilleseconds);
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
