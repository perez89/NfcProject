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
import android.widget.TextView;

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
    private TextView tvDayTime, tvWeekTime, tvMonthTime;
    private long dayTime = 0;
    private long weekTime = 0;
    private long monthTime = 0;
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
        viewsInitialization();
       // handler.handleMessage();
        setHandlerAndThread();
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
            int week = LocalTime.getWeekOfMonth(milli);
            int year = LocalTime.getYear(milli);

            int month = LocalTime.getMonth(milli);
            int day = LocalTime.getDay(milli);

            System.out.println(year + " xpto " + month);

            int numOfDays = LocalTime.getNumberDaysMonth(year, month);
            LocalTime.DateString dataString = new LocalTime.DateString(year+"",(month+1)+"","","","","");

            System.out.println("dayweekyear= " +month + " " +week+ " " +day + " "  +numOfDays);
            //int minTime = 999;
            //int maxTime = 0;
            int currentDayTime = 0;
            int currentWeekTime = 0;
            int currentMonthTime = 0;
            try {
                timeStartOfMonth = dataString.getMilliseconds();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millisecondsDay = 86400000;

            if(timeStartOfMonth>0){

                long time = timeStartOfMonth;
                System.out.println("run background-inside-timeStartOfMonth="+time);
                for(int i=0; i<numOfDays ; i++){
                    System.out.println("run background-inside-2");
                    int localWeek =  LocalTime.getWeekOfMonth(time);
                    int localDay =  LocalTime.getDay(time);
                    long timeEnd = time+millisecondsDay;
                    List<EventClass> listOfEvents = lEventService.getEventsForDay(time, timeEnd);
                    //if(listOfEvents.get())
                    System.out.println("dayweekyear2= " +month + " " +localWeek+ " " +localDay);
                    long totalDayTime = getTotalDayTime(listOfEvents);


                    if(day == localDay)
                        currentDayTime = (int)(long)totalDayTime;


                    if(week == localWeek)
                        currentWeekTime = currentWeekTime + (int)(long)totalDayTime;

                   /* if(totalDayTime<minTime)
                        minTime=(int)(long)totalDayTime;

                    if(totalDayTime>maxTime)
                        maxTime=(int)(long)totalDayTime;*/

                    currentMonthTime = currentMonthTime +(int)(long)totalDayTime;
                    time = time + millisecondsDay;
                }
               }

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            String value = LocalTime.getFormatTime(currentDayTime);
            System.out.println("currentDayTime="+value);
            b.putInt("currentDayTime", currentDayTime); //int
            b.putString("currentDayValue", value); ///string


            value = LocalTime.getFormatTime(currentWeekTime);
            System.out.println("currentWeekTime="+value);
            b.putInt("currentWeekTime", currentWeekTime); //int
            b.putString("currentWeekValue", value);


            value = LocalTime.getFormatTime(currentMonthTime);
            System.out.println("currentMonthTime="+value);
            b.putInt("currentMonthTime", currentMonthTime); //int
            b.putString("currentMonthValue", value);


            message.setData(b);

            message.obj= mWeakRefContext;
            message.what=0;
            handler.sendMessage(message);

        }


        private long getTotalDayTime(List<EventClass> listOfEvents) {
            long total = 0;
            for(int i=0 ; i<listOfEvents.size() ; i++){
                total= total+listOfEvents.get(i).getData().getDuration();
            }
            return total;
        }
    }

    private void viewsInitialization(){
        tvDayTime = (TextView)rootView.findViewById(R.id.tvTimeCurrentDay);
        tvWeekTime = (TextView)rootView.findViewById(R.id.tvTimeCurrentWeek);
        tvMonthTime = (TextView)rootView.findViewById(R.id.tvTimeCurrentMonth);
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
                    String dayValue =  msg.getData().getString("currentDayValue");
                    String weekValue =  msg.getData().getString("currentWeekValue");
                    String MonthValue =  msg.getData().getString("currentMonthValue");
                    dayTime = msg.getData().getInt("currentDayTime");
                    weekTime = msg.getData().getInt("currentWeekTime");
                    monthTime = msg.getData().getInt("currentMonthTime");

                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                    {
                        if(!dayValue.equals("-"))
                            setTvDay( dayValue);

                        if(!weekValue.equals("-"))
                            setTvWeek(weekValue);

                        if(!MonthValue.equals("-"))
                           setTvMonth(MonthValue);
                    }
                    break;
            }
        }
    }

    private void setTvMonth(String monthValue) {
        tvDayTime.setText(monthValue);
    }

    private void setTvWeek(String weekValue) {
        tvWeekTime.setText(weekValue);
    }

    private void setTvDay(String dayValue) {
        tvMonthTime.setText(dayValue);
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

