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
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Support.EventClass;
import Support.LocalEventService;
import Support.LocalTime;


/**
 * Created by User on 07/10/2016.
 */
public class MainFragment extends Fragment  {

    private Button btCreateCalendar, btSimulateNFC;
    private View rootView;
   // Worker _workerThread;
    private Context context;
    List<TextView> listDaysOfTheWeek;
    List<TextView> listWeekHeader;
    List<TextView> listWeekTotalTime;
    TextView[][] tvDayTime = new TextView[7][5];
    //static TextView[][] tvDay = new TextView[7][5];
    static TextView[][] tvDay = new TextView[7][5];
    Runnable progressThread;
    //MyHandler myHandler;
    HandlerThread handlerThread;



    private MyHandler handler = new MyHandler();
    private MyHandlerThread myHandlerThread;


    // Handler mhandler;

    public static MainFragment newInstance(int _month, int _year) {
        MainFragment myFragment = new MainFragment();
        Bundle args = new Bundle();
        args.putInt("year_CurrentView", _year);
        args.putInt("month_CurrentView", _month);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println(" MainFragment - OnCreate");
        rootView = inflater.inflate(R.layout.fragment_show_main_save, container, false);
        context= getActivity();
        Bundle b = getArguments();
        int year = b.getInt("year_CurrentView");
        int month = b.getInt("month_CurrentView");
        month++;
        initialization();
        setHandlerAndThread(year, month);

        return rootView;

    }

    private void setHandlerAndThread(int year, int month) {
        myHandlerThread = new MyHandlerThread("myHandlerThread");
        myHandlerThread.start();
        myHandlerThread.prepareHandler();
        try {
            myHandlerThread.postTask(new CustomRunnable(context, handler, year, month));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    class MyHandler extends Handler{

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {
            WeakReference<Context> mWeakRefContext = (WeakReference<Context>) msg.obj;
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    System.out.println("0000 ");
                    int day =  msg.getData().getInt("dayPosition");
                    String dayOfWeekValue =  msg.getData().getString("dayValue");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvDaysOfTheWeek(day, dayOfWeekValue);
                    break;
                case 1:
                    System.out.println("1111 ");
                    int week =  msg.getData().getInt("weekPosition");
                    String weekValue =  msg.getData().getString("weekValue");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvWeekHeader(week, weekValue);
                    break;
                case 2:
                    System.out.println("2222 ");
                    int dayPos =  msg.getData().getInt("dayPosition");
                    int weekPos =  msg.getData().getInt("weekPosition");
                    String timeValue =  msg.getData().getString("timeValue");
                    String dayValue =  msg.getData().getString("dayValue");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null){
                        setTvDayTime(dayPos, weekPos, timeValue);
                        setTvDay(dayPos, weekPos, dayValue);
                    }

                    break;
                case 3:
                    System.out.println("3333 ");
                    int weekPosition =  msg.getData().getInt("weekPosition");
                    String totalTimeValue =  msg.getData().getString("totalTimeValue");
                    //TODO - verificar se é == "-"
                    if(mWeakRefContext != null && mWeakRefContext.get() != null){

                            setTvWeekTotalTime(weekPosition, totalTimeValue);
                    }

                    break;
                case 4:
                    //  if(mWeakRefContext != null && mWeakRefContext.get() != null)

                    break;
            }
        }
    }

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handlerThread != null){
            handlerThread.quit();
            handlerThread.interrupt();
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myHandlerThread != null){
            myHandlerThread.quit();
            myHandlerThread.interrupt();
        }
    }

    private void initialization() {
        initializationLayout();
        //   buttons();
    }

    private void initializationLayout() {
        initializationDaysOfTheWeek();
        initializationWeekHeader();
        initializationDays();
        initializationDaysTime();
        initializationTotalWeekTime();
    }

    private void initializationTotalWeekTime() {
        listWeekTotalTime = new ArrayList<TextView>();

        TextView tvDay = (TextView)rootView.findViewById(R.id.tvW1Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvW2Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvW3Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvW4Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvW5Total);
        listWeekTotalTime.add(tvDay);
    }

    void setTvWeekTotalTime(int week, String value){
        listWeekTotalTime.get(week).setText(""+value);
    }

    private void initializationDaysTime() {
        //week one
        tvDayTime[0][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D1);
        tvDayTime[1][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D2);
        tvDayTime[2][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D3);
        tvDayTime[3][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D4);
        tvDayTime[4][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D5);
        tvDayTime[5][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D6);
        tvDayTime[6][0] = (TextView) rootView.findViewById(R.id.tvFloatW1D7);

        //week two
        tvDayTime[0][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D1);
        tvDayTime[1][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D2);
        tvDayTime[2][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D3);
        tvDayTime[3][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D4);
        tvDayTime[4][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D5);
        tvDayTime[5][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D6);
        tvDayTime[6][1] = (TextView) rootView.findViewById(R.id.tvFloatW2D7);

        //week three
        tvDayTime[0][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D1);
        tvDayTime[1][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D2);
        tvDayTime[2][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D3);
        tvDayTime[3][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D4);
        tvDayTime[4][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D5);
        tvDayTime[5][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D6);
        tvDayTime[6][2] = (TextView) rootView.findViewById(R.id.tvFloatW3D7);

        //week four
        tvDayTime[0][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D1);
        tvDayTime[1][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D2);
        tvDayTime[2][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D3);
        tvDayTime[3][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D4);
        tvDayTime[4][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D5);
        tvDayTime[5][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D6);
        tvDayTime[6][3] = (TextView) rootView.findViewById(R.id.tvFloatW4D7);

        //week five
        tvDayTime[0][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D1);
        tvDayTime[1][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D2);
        tvDayTime[2][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D3);
        tvDayTime[3][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D4);
        tvDayTime[4][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D5);
        tvDayTime[5][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D6);
        tvDayTime[6][4] = (TextView) rootView.findViewById(R.id.tvFloatW5D7);

    }
    void setTvDayTime(int day, int week, String value){
        tvDayTime[day][week].setText(""+value);
    }

    private void initializationDays() {
        //week one
        tvDay[0][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D1);
        tvDay[1][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D2);
        tvDay[2][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D3);
        tvDay[3][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D4);
        tvDay[4][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D5);
        tvDay[5][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D6);
        tvDay[6][0] = (TextView) rootView.findViewById(R.id.tvFloatDayW1D7);

        //week two
        tvDay[0][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D1);
        tvDay[1][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D2);
        tvDay[2][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D3);
        tvDay[3][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D4);
        tvDay[4][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D5);
        tvDay[5][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D6);
        tvDay[6][1] = (TextView) rootView.findViewById(R.id.tvFloatDayW2D7);

        //week three
        tvDay[0][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D1);
        tvDay[1][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D2);
        tvDay[2][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D3);
        tvDay[3][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D4);
        tvDay[4][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D5);
        tvDay[5][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D6);
        tvDay[6][2] = (TextView) rootView.findViewById(R.id.tvFloatDayW3D7);

        //week four
        tvDay[0][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D1);
        tvDay[1][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D2);
        tvDay[2][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D3);
        tvDay[3][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D4);
        tvDay[4][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D5);
        tvDay[5][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D6);
        tvDay[6][3] = (TextView) rootView.findViewById(R.id.tvFloatDayW4D7);

        //week five
        tvDay[0][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D1);
        tvDay[1][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D2);
        tvDay[2][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D3);
        tvDay[3][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D4);
        tvDay[4][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D5);
        tvDay[5][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D6);
        tvDay[6][4] = (TextView) rootView.findViewById(R.id.tvFloatDayW5D7);

    }

    static void setTvDay(int day, int week, String value){
        tvDay[day][week].setText(""+value);
    }

    private void initializationWeekHeader() {
        listWeekHeader = new ArrayList<TextView>();
        TextView tvDay = (TextView)rootView.findViewById(R.id.tvWeek1);
        listWeekHeader.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvWeek2);
        listWeekHeader.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvWeek3);
        listWeekHeader.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvWeek4);
        listWeekHeader.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvWeek5);
        listWeekHeader.add(tvDay);

        tvDay=null;
    }

    void setTvWeekHeader(int week, String value){
        listWeekHeader.get(week).setText(""+value);
    }

    private void initializationDaysOfTheWeek() {
        listDaysOfTheWeek = new ArrayList<TextView>();
        TextView tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek0);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek1);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek2);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek3);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek4);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek5);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek6);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvDayOfWeek7);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView)rootView.findViewById(R.id.tvTotalPartial);
        listDaysOfTheWeek.add(tvDay);

        tvDay=null;
    }

    void setTvDaysOfTheWeek(int dayOfWeek, String value){
        listDaysOfTheWeek.get(dayOfWeek).setText(""+value);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class CustomRunnable implements Runnable {
        private WeakReference<Context> mWeakRefContext;
        long timeStartOfWeek;
        Handler handler;

        public CustomRunnable(Context context, Handler h, int year, int month) throws ParseException {
            handler = h;
            System.out.println(year + " xpto "+ month);
            this.mWeakRefContext = new WeakReference<Context>(context);
            timeStartOfWeek =  LocalTime.getInitialTimeOfLayout(year, month);
        }
        @ Override
        public void run() {
            //Main task execution logic here
            loadColumns();
        }

        public void loadColumns() {
            System.out.println("loadColumns");

            //Log.i("while null", "Not init yet"); //It keeps on looping here
            long week_milli = 604800000;
            createLeftColumn();
            for (int week = 0; week < 5; week++) {
                    final long initial_week_time = timeStartOfWeek + (week * week_milli);
                    createWeekColumns(week, initial_week_time);
            }
        }
        private void createLeftColumn() {
            System.out.println("createLeftColumn");
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            // for the current Locale :
            //   DateFormatSymbols symbols = new DateFormatSymbols();
            //   DateFormatSymbols symbols = new DateFormatSymbols();
            String[] dayNames = symbols.getShortWeekdays();

            for (String s : dayNames) {
                //     System.out.print(s + " ");
            }
            //System.out.println("perez");
            for (int i = 0; i < 9; i++) {
                //System.out.println("perez-+1");

                Message message = handler.obtainMessage();
                Bundle b = new Bundle();

                b.putInt("dayPosition", i); // for example
                if (i == 0){
                    b.putString("dayValue", " "); // for example
                }else if(i == 8){
                    b.putString("dayValue", "Total"); // for example
                } else {
                    if (i == 7) {
                        String output = dayNames[1].substring(0, 1).toUpperCase() + dayNames[1].substring(1);
                        b.putString("dayValue", output); // for example
                    } else {
                        String output = dayNames[i + 1].substring(0, 1).toUpperCase() + dayNames[i + 1].substring(1);
                        b.putString("dayValue", output); // for example
                    }
                }
                message.setData(b);
                message.obj= mWeakRefContext;
                message.what = 0;
                handler.sendMessage(message);
                // message.sendToTarget();
            }
        }

        private void  createWeekColumns(int week, long timeStartOfWeek) {
            System.out.println("createWeekColumns");
            long iniWeek;
            long total = 0;

            createHeaderColumns(week, timeStartOfWeek);
            long dayTime = 86400000;
            long totalWeekDuration=0;
            System.out.println("createWeekColumns");
            for (int day = 0; day < 7; day++) {

               // createHeaderColumns(week, timeStartOfWeek, layout); //-> so para verificar que esta correto
                iniWeek = timeStartOfWeek + (dayTime * day);

               totalWeekDuration = totalWeekDuration + createDays(week, day, iniWeek);

            }
            createTotalWeekHour(week, totalWeekDuration);
        }

        private long createDays(int local_week, final int local_day, final long timeStartOfWeek) {

            final int week = local_week;

            final LocalEventService lEventService = new LocalEventService(mWeakRefContext);

            List<EventClass> listOfEvents = lEventService.getEventsForDay(timeStartOfWeek, (timeStartOfWeek + 86400000));
            System.out.println("local_week =" + local_week + " | local_day= " + local_day + " | listOfEvents= " + listOfEvents.size());
            DayClassTMP dayObject = new DayClassTMP(listOfEvents);

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();

            b.putInt("dayPosition", local_day); // for example
            b.putInt("weekPosition", week); // for example
            b.putString("dayValue","" + LocalTime.getDay(timeStartOfWeek)); // for example
            b.putString("timeValue", dayObject.toString()); // for example
            message.setData(b);

            message.obj= mWeakRefContext;
            message.what=2;
            handler.sendMessage(message);


            return dayObject.getTotalDuration();
            }

            private void  createHeaderColumns(int pos, long timeStartOfWeek) {


                // System.out.println(timeStartOfWeek + " >>>> " + pos + " >>>> " + timeStartOfWeek);
            int week = LocalTime.getWeekOfMonth(timeStartOfWeek);
                //week= week-1;

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();


            b.putInt("weekPosition", pos); // for example

            if (week < 10)
                b.putString("weekValue",("W0" + week));
            else
                b.putString("weekValue",("W" + week));
            message.setData(b);
                message.obj= mWeakRefContext;
                message.what = 1;
                handler.sendMessage(message);
          /*  ll_Week.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // do you work here

                    Context context = getContext();
                    CharSequence text = "title week";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });
*/
          }


        public class DayClassTMP {
            private List<EventClass> listOfEvents;

            long totalDuration;
            String durationString;

            public DayClassTMP(List<EventClass> listOfEvents) {
                this.listOfEvents = listOfEvents;
                setDuration();
            }

            public void setDuration() {
                long totalDuration = 0;

                for (EventClass event : listOfEvents
                        ) {
                    System.out.println("totalDuration= " + totalDuration);
                    totalDuration = totalDuration + event.getData().getDuration();
                }

                this.totalDuration = totalDuration;

                int minutes = (int) ((totalDuration / (1000 * 60)) % 60);
                int hours = (int) ((totalDuration / (1000 * 60 * 60)) % 24);

                long numOfHours= 0;
                if (totalDuration >= 86400000) {
                    numOfHours = totalDuration / 86400000;
                    numOfHours = numOfHours * 24;
                    hours = hours + (int)numOfHours;
                }

                //if(totalDuration>86400000)


                if (hours > 0 || minutes > 0) {
                    if (minutes < 10)
                        this.durationString = hours + ".0" + minutes;
                    else
                        this.durationString = hours + "." + minutes;
                } else {
                    this.durationString = "-";
                }
            }

            public long getTotalDuration() {
                return totalDuration;
            }

            @Override
            public String toString() {
                return durationString;
            }
        }

        private  void createTotalWeekHour(int week, long duration) {
            System.out.println("createTotalWeekHour.duration()= " + duration);
            //long totalDuration = listOfWeeks.get(col - 1).getTotalHours();
            String durationString = "-";
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();

            //System.out.println("col= " + col + " > " + listOfWeeks.get(col - 1).getTotalHours());
            if (duration > 0) {
                long numOfDays = 0;
                if (duration >= 86400000) {
                    numOfDays = duration / 86400000;
                    numOfDays = numOfDays * 24;
                }

                int minutes = (int) ((duration / (1000 * 60)) % 60);
                int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
                hours = (int) numOfDays + hours;
                if (hours > 0 || minutes > 0) {
                    if (minutes < 10)
                        durationString = hours + ".0" + minutes;
                    else
                        durationString = hours + "." + minutes;

                }
                b.putString("totalTimeValue", durationString); // for example
                message.setData(b);

            }else{
                b.putString("totalTimeValue", durationString); // for example
                message.setData(b);
            }
            b.putInt("weekPosition", week); // for example
            message.obj= mWeakRefContext;
            message.what=3;
            handler.sendMessage(message);


            //listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
            //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));


            //          setTvTotalHoursWeek(col, durationString, layout);

        }
    };

    private class MyHandlerThread extends HandlerThread {

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