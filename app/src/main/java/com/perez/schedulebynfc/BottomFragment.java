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

import static Support.LocalTime.getCurrentMilliseconds;
import static Support.LocalTime.getFormatTime;

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
    private TextView tvDayTime, tvDayTimeSeconds, tvWeekTime, tvMonthTime;
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
        System.out.println("xxxxxxxxxxxxxxxxxxxxxx");
        rootView = inflater.inflate(R.layout.fragment_bottom, container, false);
        context = getActivity();
        viewsInitialization();
       // handler.handleMessage();
        setHandlerAndThread();
        return rootView;

    }

    public void startTimer(long dayTime, long weekTime, long monthTime, int secs) {
        //set a new Timer
        timer = new Timer();

        timerTask = new CustomTimer(context, handler, dayTime, weekTime, monthTime, secs);

        //initialize the TimerTask's job
        //initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 20, 1000); //
    }


    private void setHandlerAndThread() {
        myHandlerThread = new MyHandlerThread("myBottomHandlerThread");
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
            WeakReference<Context> mWeakRefContext;
            Handler handler;
            Message message;
            int sec = 0;
            long _dayTime = 0;
            long _weekTime = 0;
            long _monthTime = 0;

            CustomTimer(Context c,  Handler h, long dayTimeX, long weekTimeX, long monthTimeX, int secs)
            {
                if(secs>59)
                    secs=0;
                sec = secs;
                handler = h;
                mWeakRefContext = new WeakReference<Context>(c);
                _dayTime = dayTimeX;
                _weekTime = weekTimeX;
                _monthTime = monthTimeX;
            }

            public void run() {
                String str;
                if(sec>59){
                    str=":00";
                }else{
                    if(sec<10){
                        str=":0"+sec;
                    }
                    else{
                        str=":"+sec;
                    }
                }

                message = handler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("currentDayTimeSeconds", str); //Long
                message.setData(b);

                //message.obj= mWeakRefContext;
                message.obj= mWeakRefContext;
                message.what=1;
                handler.sendMessage(message);

                if(sec>59) {
                    sec = 0;
                    _dayTime = _dayTime + 60000;
                    _weekTime = _weekTime + 60000;
                    _monthTime = _monthTime + 60000;
                    message = handler.obtainMessage();
                    b = new Bundle();
                    String dayTimeHuman = LocalTime.getFormatTime(_dayTime);
                    String weekTimeHuman = LocalTime.getFormatTime(_weekTime);
                    String monthTimeHuman = LocalTime.getFormatTime(_monthTime);
                    b.putString("dayTimeHuman", dayTimeHuman); //Long
                    b.putString("weekTimeHuman", weekTimeHuman); //Long
                    b.putString("monthTimeHuman", monthTimeHuman); //Long

                    message.setData(b);

                    //message.obj= mWeakRefContext;
                    message.obj= mWeakRefContext;
                    message.what=2;
                    handler.sendMessage(message);
                }
                sec++;
            }
        };


    @Override
    public void onResume() {
        super.onResume();


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

            long milli = getCurrentMilliseconds();
            int  secs = LocalTime.getSeconds(milli);
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
            long currentDayTime = 0;
            long currentWeekTime = 0;
            long currentMonthTime = 0;
            boolean open = false;
            try {
                timeStartOfMonth = dataString.getMilliseconds();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millisecondsDay = 86400000;

            if(timeStartOfMonth>0){

                long time = timeStartOfMonth;
                //System.out.println("run background-inside-timeStartOfMonth="+time);
                for(int i=0; i<numOfDays ; i++){
                 //   System.out.println("run background-inside-2");
                    int localWeek =  LocalTime.getWeekOfMonth(time);
                    int localDay =  LocalTime.getDay(time);
                    long timeEnd = time+millisecondsDay;
                    List<EventClass> listOfEvents = lEventService.getEventsForDay(time, timeEnd);
                    //if(listOfEvents.get())
                  //  System.out.println("dayweekyear2= " +month + " " +localWeek+ " " +localDay);
                    long totalDayTime = getTotalDayTime(listOfEvents);
                    String xpto = getFormatTime(totalDayTime);
                    System.out.println("xpto day= " + localDay + " | time= " + xpto);

                    if(day == localDay){

                        if (listOfEvents.size()>0 && !(listOfEvents.get(listOfEvents.size()-1).isClose())) {
                            totalDayTime = totalDayTime + LocalTime.getCurrentMilliseconds() - listOfEvents.get(listOfEvents.size()-1).getData().getStartTime();
                            open=true;
                        }
                        currentDayTime = totalDayTime;
                    }


                    if(week == localWeek)
                        currentWeekTime = currentWeekTime + totalDayTime;

                   /* if(totalDayTime<minTime)
                        minTime=(int)(long)totalDayTime;

                    if(totalDayTime>maxTime)
                        maxTime=(int)(long)totalDayTime;*/

                    currentMonthTime = currentMonthTime +totalDayTime;

                    time = time + millisecondsDay;


                }
               }
            //currentMonthTime=10000000;
//TODO            adicionar um evento em aberto

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            System.out.println("currentDayTime="+currentDayTime);
            String value = getFormatTime(currentDayTime);
            System.out.println("currentDayTime="+value);
            b.putLong("currentDayTime", currentDayTime); //Long
            b.putString("currentDayValue", value); ///string
            if(open)
                b.putInt("currentSecs", secs);
            else
                b.putInt("currentSecs", -1);

            value = getFormatTime(currentWeekTime);
            System.out.println("currentWeekTime="+value);
            b.putLong("currentWeekTime", currentWeekTime);
            b.putString("currentWeekValue", value);


            value = getFormatTime(currentMonthTime);
            System.out.println("currentMonthTime="+value);
            b.putLong("currentMonthTime", currentMonthTime);
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

    //TODO colocar o timetask a incrementar a cada minuto

    private void viewsInitialization(){
        tvDayTime = (TextView)rootView.findViewById(R.id.tvTimeCurrentDay);
        tvDayTimeSeconds = (TextView)rootView.findViewById(R.id.tvTimeCurrentDaySeconds);
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
                    String monthValue =  msg.getData().getString("currentMonthValue");
                    dayTime = msg.getData().getLong("currentDayTime");
                    weekTime = msg.getData().getLong("currentWeekTime");
                    monthTime = msg.getData().getLong("currentMonthTime");
                    int secs = msg.getData().getInt("currentSecs");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                    {
                        if(secs>-1)
                            startTimer(dayTime, weekTime, monthTime, secs);
                        if(!dayValue.equals("-"))
                            setTvDay(dayValue);

                        if(!weekValue.equals("-"))
                            setTvWeek(weekValue);

                        if(!monthValue.equals("-"))
                           setTvMonth(monthValue);
                    }
                    break;
                case 1:
                    String dayValueSeconds =  msg.getData().getString("currentDayTimeSeconds");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                    {
                        setTvDaySeconds(dayValueSeconds);
                    }
                    break;
                case 2:
                    String dayValueStr =  msg.getData().getString("dayTimeHuman");
                    String weekValueStr =  msg.getData().getString("weekTimeHuman");
                    String monthValueStr =  msg.getData().getString("monthTimeHuman");

                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                    {
                        if(!dayValueStr.equals("-"))
                            setTvDay(dayValueStr);

                        if(!weekValueStr.equals("-"))
                            setTvWeek(weekValueStr);

                        if(!monthValueStr.equals("-"))
                            setTvMonth(monthValueStr);
                    }
                    break;
            }
        }
    }

    private void setTvDay(String dayValue) {
        tvDayTime.setText(dayValue);
    }

    private void setTvDaySeconds(String dayValueSeconds) {
        tvDayTimeSeconds.setText(dayValueSeconds);
    }

    private void setTvWeek(String weekValue) {
        tvWeekTime.setText(weekValue);
    }

    private void setTvMonth(String monthValue) {
        tvMonthTime.setText(monthValue);
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

