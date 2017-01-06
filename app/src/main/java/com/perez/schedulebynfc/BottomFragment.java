package com.perez.schedulebynfc;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Support.EventClass;
import Support.LocalCalendar;
import Support.LocalEventService;
import Support.LocalTime;
import Support.MyCalendarObserver;

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
    private final long DAY_MILLISECONDS = 86400000;
    Context context;
    private TextView tvDayTime, tvWeekTime, tvMonthTime;
    private long dayTime = 0;
    private long weekTime = 0;
    private long monthTime = 0;
    private MyCalendarObserver myObserver;
    public static BottomFragment newInstance() {
        BottomFragment myFragment = new BottomFragment();

        return myFragment;
    }
    ImageView ivWorking;
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
        myObserver = new MyCalendarObserver(handler);
        return rootView;

    }

    public void startTimer(long dayTime, long weekTime, long monthTime, int secTimeX) {
        //set a new Timer
        timer = new Timer();

        timerTask = new CustomTimer(context, handler, dayTime, weekTime, monthTime, secTimeX);

        //initialize the TimerTask's job
        //initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 20, 1000); //
    }


    private void setHandlerAndThread() {
        stopHandler();
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

    private static class CustomTimer extends TimerTask {
        WeakReference<Context> mWeakRefContext;
        Handler handler;
        Message message;
        int sec;
        long _dayTime = 0;
        long _weekTime = 0;
        long _monthTime = 0;

        CustomTimer(Context c, Handler h, long dayTimeX, long weekTimeX, long monthTimeX, int secTimeX) {
            sec = secTimeX;
            handler = h;
            mWeakRefContext = new WeakReference<Context>(c);
            _dayTime = dayTimeX;
            _weekTime = weekTimeX;
            _monthTime = monthTimeX;
        }

        public void run() {
            //System.out.println("tick tack");
            //if(sec%2==0)

            if (sec > 59) {
                Bundle b = new Bundle();
                //updateMainFragment(_dayTime);
                updateWidget(mWeakRefContext);
                updateMainFragment(_dayTime);
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
                message.obj = mWeakRefContext;
                message.what = 2;
                handler.sendMessage(message);
            }
            sec++;
        }

        private void updateMainFragment(long time) {
            if(mCallback!=null)
                mCallback.sendResfreshTime(time);
        }

        //http://www.androiddesignpatterns.com/2013/01/inner-class-handler-memory-leak.html
        //https://www.youtube.com/watch?v=LGDdxw55Gis
        //http://stackoverflow.com/questions/36191755/why-use-weakreference-on-android-listeners
        //http://svmuburt.spms.min-saude.pt/cs/PDS/relat_00_pdsmonit.php?tbl=cs_pds_links&painel=1&amb_id=9
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
    }




    @Override
    public void onResume() {
        super.onResume();
        getActivity().getContentResolver().
                registerContentObserver(
                        LocalCalendar.buildUri(),
                        true,
                        myObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopHandler();
        stoptimertask();
        getActivity().getContentResolver().
                unregisterContentObserver(myObserver);
    }

    private class CustomRunnable implements Runnable {
        private WeakReference<Context> mWeakRefContext;
        private LocalEventService lEventService;
        long timeStartOfMonth;
        Handler handler;

        public CustomRunnable(Context context, Handler h) throws ParseException {
            System.out.println("CustomRunnable, ALTERACAO!!!!!!!!!!!!!!!!");
            handler = h;
            this.mWeakRefContext = new WeakReference<Context>(context);
            lEventService = new LocalEventService(mWeakRefContext);
        }

        @Override
        public void run() {
            //Main task execution logic here
            //load
            System.out.println("CustomRunnable-run, ALTERACAO!!!!!!!!!!!!!!!!");
            long milli = getCurrentMilliseconds();
            int secs_real = LocalTime.getSeconds(milli);
            int week_real = LocalTime.getWeekOfMonth(milli);
            int year_real = LocalTime.getYear(milli);

            int month_real = LocalTime.getMonth(milli);
            int day_real = LocalTime.getDay(milli);
            int secs = LocalTime.getSeconds(milli);
            System.out.println(year_real + " xpto " + month_real);

            int numOfDays = LocalTime.getNumberDaysMonth(year_real, month_real);
            LocalTime.DateString dataString = new LocalTime.DateString(year_real + "", (month_real + 1) + "", "", "", "", "");

            System.out.println("dayweekyear= " + month_real + " " + week_real + " " + day_real + " " + numOfDays);
            //int minTime = 999;
            //int maxTime = 0;
            long currentDayTime = 0;
            long currentWeekTime = 0;
            long currentMonthTime = 0;
            boolean working = false;
            try {
                timeStartOfMonth = dataString.getMilliseconds();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (timeStartOfMonth > 0) {
                //System.out.println("run background-inside-timeStartOfMonth="+time);
                for (int i = 0; i < numOfDays; i++) {
                    //   System.out.println("run background-inside-2");
                    int localWeek = LocalTime.getWeekOfMonth(timeStartOfMonth);
                    int localDay = LocalTime.getDay(timeStartOfMonth);
                    long timeEnd = timeStartOfMonth + DAY_MILLISECONDS;
                    List<EventClass> listOfEvents = lEventService.getEventsForDay(timeStartOfMonth, timeEnd);
                    //if(listOfEvents.get())
                    //  System.out.println("dayweekyear2= " +month_frag_show + " " +localWeek+ " " +localDay);
                    long totalDayTime = getTotalDayTime(listOfEvents);
                    //String xpto = getFormatTime(totalDayTime);
                    //System.out.println("xpto day= " + localDay + " | time= " + xpto);

                    if (day_real == localDay) {
                        if (listOfEvents.size() > 0 && !(listOfEvents.get(listOfEvents.size() - 1).isClose())) {
                            totalDayTime = totalDayTime + LocalTime.getCurrentMilliseconds() - listOfEvents.get(listOfEvents.size() - 1).getData().getStartTime();
                            working = true;
                        }
                        currentDayTime = totalDayTime;
                    }


                    if (week_real == localWeek)
                        currentWeekTime = currentWeekTime + totalDayTime;

                   /* if(totalDayTime<minTime)
                        minTime=(int)(long)totalDayTime;

                    if(totalDayTime>maxTime)
                        maxTime=(int)(long)totalDayTime;*/

                    currentMonthTime = currentMonthTime + totalDayTime;

                    timeStartOfMonth = timeStartOfMonth + DAY_MILLISECONDS;


                }
            }
            //currentMonthTime=10000000;
//TODO            adicionar um evento em aberto

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            System.out.println("currentDayTime-1=" + currentDayTime);
            String value = getFormatTime(currentDayTime);
            System.out.println("currentDayTime-2=" + value);
            b.putLong("currentDayTime", currentDayTime); //Long
            b.putString("currentDayValue", value); ///string
            b.putInt("currentSecs", secs); ///string

            b.putBoolean("working", working);


            value = getFormatTime(currentWeekTime);
            System.out.println("currentWeekTime=" + value);
            b.putLong("currentWeekTime", currentWeekTime);
            b.putString("currentWeekValue", value);


            value = getFormatTime(currentMonthTime);
            System.out.println("currentMonthTime=" + value);
            b.putLong("currentMonthTime", currentMonthTime);
            b.putString("currentMonthValue", value);


            message.setData(b);

            message.obj = mWeakRefContext;
            message.what = 0;
            handler.sendMessage(message);

        }



        private long getTotalDayTime(List<EventClass> listOfEvents) {
            long total = 0;
            for (int i = 0; i < listOfEvents.size(); i++) {
                total = total + listOfEvents.get(i).getData().getDuration();
            }
            return total;
        }
    }

    //TODO colocar o timetask a incrementar a cada minuto

    private void viewsInitialization() {
        tvDayTime = (TextView) rootView.findViewById(R.id.tvTimeCurrentDay);
        tvWeekTime = (TextView) rootView.findViewById(R.id.tvTimeCurrentWeek);
        tvMonthTime = (TextView) rootView.findViewById(R.id.tvTimeCurrentMonth);
        ivWorking = (ImageView) rootView.findViewById(R.id.ivWorking);
        changeImageViewVisibility(false);
    }

    void changeImageViewVisibility(boolean working){
        if(working){
            ivWorking.setVisibility(View.VISIBLE);
        }else{
            ivWorking.setVisibility(View.INVISIBLE);
        }
    }

    class MyHandler extends Handler {

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {
            WeakReference<Context> mWeakRefContext = null;
            if(msg.obj!=null)
                mWeakRefContext = (WeakReference<Context>) msg.obj;

            super.handleMessage(msg);
            switch (msg.what) {
                //handle result from handler
                case 0:
                    System.out.println("case 0");
                    String dayValue = msg.getData().getString("currentDayValue");
                    String weekValue = msg.getData().getString("currentWeekValue");
                    String monthValue = msg.getData().getString("currentMonthValue");
                    dayTime = msg.getData().getLong("currentDayTime");
                    weekTime = msg.getData().getLong("currentWeekTime");
                    monthTime = msg.getData().getLong("currentMonthTime");
                    int secs = msg.getData().getInt("currentSecs");
                    boolean working = msg.getData().getBoolean("working");

                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        if(working){
                            System.out.println("working");
                            changeImageViewVisibility(true);

                            startTimer(dayTime, weekTime, monthTime, secs);
                            if((dayValue.equals("-")) || (dayValue.equals("0"))){

                                setTvDay("0:00");
                            }else{
                                setTvDay(dayValue);
                            }
                        }else{
                            System.out.println("not working");
                            changeImageViewVisibility(false);
                            if(dayValue.equals("-") || dayValue.equals("0"))
                                setTvDay("-");
                            else
                                setTvDay(dayValue);
                        }

                        if (!weekValue.equals("-"))
                            setTvWeek(weekValue);

                        if (!monthValue.equals("-"))
                            setTvMonth(monthValue);
                    }
                    break;
                case 1:

                    break;
                case 2:
                    String dayValueStr = msg.getData().getString("dayTimeHuman");
                    String weekValueStr = msg.getData().getString("weekTimeHuman");
                    String monthValueStr = msg.getData().getString("monthTimeHuman");

                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        if (!dayValueStr.equals("-"))
                            setTvDay(dayValueStr);

                        if (!weekValueStr.equals("-"))
                            setTvWeek(weekValueStr);

                        if (!monthValueStr.equals("-"))
                            setTvMonth(monthValueStr);
                    }
                    break;
                case  3:
                    changeCalendar();
                    break;
            }
        }
    }

    private void changeCalendar() {
        System.out.println("changeCalendar");

        ((MainActivity)getActivity()).laodBottomFrag();
        ((MainActivity)getActivity()).updateWidget();



        //setHandlerAndThread();
    }

    private void setTvDay(String dayValue) {
        tvDayTime.setText(dayValue);
    }

    private void setTvWeek(String weekValue) {
        tvWeekTime.setText(weekValue);
    }

    private void setTvMonth(String monthValue) {
        tvMonthTime.setText(monthValue);
    }


    private void stopHandler() {
        if (myHandlerThread != null) {
            myHandlerThread.quit();
            myHandlerThread.interrupt();
        }
    }

    public class MyHandlerThread extends HandlerThread {

        private Handler handler;

        public MyHandlerThread(String name) {
            super(name);
        }

        public void postTask(Runnable task) {
            handler.post(task);
        }

        public void prepareHandler() {
            handler = new Handler(getLooper());
        }
    }


    static RefreshTime mCallback;

    public interface RefreshTime{
        void sendResfreshTime(long text);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (RefreshTime) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TextClicked");
        }
    }

   // public void someMethod(){
     //   mCallback.sendText("YOUR TEXT");
   // }

    @Override
    public void onDetach() {
        mCallback = null; // => avoid leaking, thanks @Deepscorn
        super.onDetach();
    }



}

