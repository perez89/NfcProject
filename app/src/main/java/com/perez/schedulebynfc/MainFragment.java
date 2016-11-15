package com.perez.schedulebynfc;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Calendar;

import Support.LocalCalendar;
import Support.LocalTime;

/**
 * Created by User on 07/10/2016.
 */
public class MainFragment extends Fragment {

    private Button btCreateCalendar, btSimulateNFC;
    private View rootView;
    private Context context;
    private long idCalendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("MainFragment");
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        initialization();
        //getCalendars();
        //getCalendars();

        return rootView;

    }

    private void initialization() {
        setContext();
        buttons();
    }

    @Override
    public void onResume() {
        super.onResume();
        idCalendar = MainActivity.getIdCalendar();
    }

    private void setContext(){
        context = getActivity();
    }

    private void buttons() {
        btCreateCalendar = (Button) rootView.findViewById(R.id.btCreateCalendar);
        btCreateCalendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                LocalCalendar.getEvents(getActivity());
                //LocalCalendar.getCalendars(context);
            }
        });
        btSimulateNFC = (Button) rootView.findViewById(R.id.btSimulateNFC);
        btSimulateNFC.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                //System.out.println("MainFragment - btSimulateNFC - calendarID = " + calendarID);
                long currentMilleseconds = LocalTime.getCurrentMilliseconds();
                RegisterNfc.getInstance().newNfcDetected(context, idCalendar, currentMilleseconds);
                //LocalCalendar.getCallendares(getActivity());
               //  LocalCalendar.getEvents(getActivity());
                //RegisterNfc.deleteEvents(getActivity(), 0, 0);
                //loadTime();
            }
        });
    }

    private void loadTime() {
            Calendar cl = Calendar.getInstance();
            cl.setTimeInMillis(LocalTime.getCurrentMilliseconds());  //here your time in miliseconds
            int year = cl.get(Calendar.YEAR);
            int month = cl.get(Calendar.MONTH);
            int day = cl.get(Calendar.DAY_OF_MONTH);
            int dayweek = cl.get(Calendar.DAY_OF_WEEK);
            int weekOfMonth = cl.get(Calendar.WEEK_OF_MONTH);
            int weekOfyear = cl.get(Calendar.WEEK_OF_YEAR);
        System.out.println("year= " + year +
                " | month= " + month +
                " | day= " + day +
                " | dayweek= " + dayweek +
                " | weekOfMonth= " + weekOfMonth +
                " | weekOfyear= " + weekOfyear);
    }


}
