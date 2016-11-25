package com.perez.schedulebynfc;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Support.EventClass;
import Support.LocalEventService;
import Support.LocalTime;

/**
 * Created by User on 23/11/2016.
 */

public class BottomFragment extends Fragment {
    //we are going to use a handler to be able to run in our TimerTask
    CustomTimer timerTask;
    Timer timer;
    private View rootView;
    private MyHandler handler = new MyHandler();
    private MyHandlerThread myHandlerThread;
    Context context;

    public static BottomFragment newInstance() {
        BottomFragment myFragment = new BottomFragment();

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println(" MainFragment - OnCreate");
        rootView = inflater.inflate(R.layout.fragment_bottom, container, false);
        context = getActivity();

       // handler.handleMessage();

        return rootView;

    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        timerTask = new CustomTimer(getActivity().getApplicationContext());

        //initialize the TimerTask's job
        //initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 1000, 60000); //
    }


    private void setHandlerAndThread() {
        myHandlerThread = new MyHandlerThread("myHandlerThread");
        myHandlerThread.start();
        myHandlerThread.prepareHandler();
        try {
            myHandlerThread.postTask(new CustomRunnable(context, handler));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private static class CustomTimer extends TimerTask{
        WeakReference<Context> weakContext;


        CustomTimer (Context c)
            {
                weakContext = new WeakReference<Context>(c);
            }
            public void run() {
            }
        };


    @Override
    public void onResume() {
        super.onResume();
        startTimer();

    }


    @Override
    public void onPause() {
        super.onPause();
        stopHandler();
        stoptimertask();
    }
    private class CustomRunnable implements Runnable {
        private WeakReference<Context> mWeakRefContext;
        private LocalEventService lEventService;
        long timeStartOfMonth;
        Handler handler;

        public CustomRunnable(Context context, Handler h) throws ParseException {
            handler = h;
            this.mWeakRefContext = new WeakReference<Context>(context);
            lEventService = new LocalEventService(mWeakRefContext);
        }

        @Override
        public void run() {
            //Main task execution logic here
           //load

            long milli = LocalTime.getCurrentMilliseconds();
            int year = LocalTime.getYear(milli);
            int month = LocalTime.getMonth(milli);
            System.out.println(year + " xpto " + month);
            int numOfDays = LocalTime.getNumberDaysMonth(year, month);
            LocalTime.DateString dataString = new LocalTime.DateString(year+"",month+"","","","","");
            int minTime = 999;
            int maxTime = 0;
            try {
                timeStartOfMonth = dataString.getMilliseconds();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(timeStartOfMonth>0){

                for(int i=0; i<numOfDays ; i++){


                    //if(listOfEvents.get())
                }
               }
        }

        private int getDayTime(long time){
            List<EventClass> listOfEvents = lEventService.getEventsForDay(time, (time + 86400000));
            return 0;
        }
    }

    class MyHandler extends Handler {

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {
            WeakReference<Context> mWeakRefContext = (WeakReference<Context>) msg.obj;
            super.handleMessage(msg);
            switch (msg.what) {
                //handle result from handler
                case 0:

                    break;
            }
        }
    }

    private void stopHandler(){
        if(myHandlerThread != null){
            myHandlerThread.quit();
            myHandlerThread.interrupt();
        }
    }

    public class MyHandlerThread extends HandlerThread {

        private Handler handler;

        public MyHandlerThread(String name) {
            super(name);
        }

        public void postTask(Runnable task){
            handler.post(task);
        }

        public void prepareHandler(){
            handler = new Handler(getLooper());
        }
    }
}

