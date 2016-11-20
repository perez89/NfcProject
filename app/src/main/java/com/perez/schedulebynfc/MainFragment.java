package com.perez.schedulebynfc;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import Support.CreateMonth;
import Support.EventClass;
import Support.LocalCalendar;
import Support.LocalEventService;
import Support.LocalTime;

/**
 * Created by User on 07/10/2016.
 */
public class MainFragment extends Fragment implements UiThreadCallback {

    private Button btCreateCalendar, btSimulateNFC;
    private View rootView;
   // Worker _workerThread;
    private Context context;
    static List<TextView> listDaysOfTheWeek;
    static List<TextView> listWeekHeader;
    static List<TextView> listWeekTotalTime;
    static TextView[][] tvDayTime = new TextView[7][5];
    //static TextView[][] tvDay = new TextView[7][5];
    static TextView[][] tvDay = new TextView[7][5];

    // Handler mhandler;

    public static MainFragment newInstance(int year, int month) {
        MainFragment myFragment = new MainFragment();

        Bundle args = new Bundle();
        args.putInt("year_CurrentView", year);
        args.putInt("month_CurrentView", month);
        myFragment.setArguments(args);

        return myFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("MainFragment");
        rootView = inflater.inflate(R.layout.fragment_show_main_save, container, false);
        context= getActivity();
        Bundle b = getArguments();
        int year = 2016;
        int month = 10;
        month++;

        System.out.println("initialization-1");
        initialization();
        setHandlerAndThread(year, month);
        System.out.println("initialization-2");
        //startHandlerThread(year, month);
        //startWorker(year, month);

        //getCalendars();
        //getCalendars();

        return rootView;

    }
    private CustomHandlerThread mHandlerThread;
    private UiHandler mUiHandler;



    private void setHandlerAndThread(int year, int month) {
        // handler for UI thread to receive message from worker thread
        mUiHandler = new UiHandler();
        mUiHandler.setContext(getActivity());
        // create and start a new worker thread
        mHandlerThread = new CustomHandlerThread("HandlerThread", year, month);
        mHandlerThread.setUiThreadCallback(this);
        mHandlerThread.start();

      // mHandlerThread.onLooperPrepared()

    }


    @Override
    public void publishToUiThread(Message message) {
// add the message from worker thread to UI thread's message queue
        if(mUiHandler != null){
            mUiHandler.sendMessage(message);
        }
    }

    private static class UiHandler extends Handler {
        private WeakReference<Context> mWeakRefContext;

        public void setContext(Context context){
            mWeakRefContext = new WeakReference<Context>(context);
        }

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    int day =  msg.getData().getInt("dayPosition");
                    String dayOfWeekValue =  msg.getData().getString("dayValue");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvDaysOfTheWeek(day, dayOfWeekValue);
                    break;
                case 1:
                    int week =  msg.getData().getInt("weekPosition");
                    String weekValue =  msg.getData().getString("weekValue");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvWeekHeader(week, weekValue);
                    break;
                case 2:
                    int dayPos =  msg.getData().getInt("dayPosition");
                    int weekPos =  msg.getData().getInt("weekPosition");
                    String timeValue =  msg.getData().getString("timeValue");
                    String dayValue =  msg.getData().getString("dayValue");
                    if(mWeakRefContext != null && mWeakRefContext.get() != null){
                        if(!timeValue.equals("-"))
                            setTvDayTime(dayPos, weekPos, timeValue);
                        setTvDay(dayPos, weekPos, dayValue);
                    }

                    break;
                case 3:
                    int weekPosition =  msg.getData().getInt("weekPosition");
                    String totalTimeValue =  msg.getData().getString("totalTimeValue");
                    //TODO - verificar se é == "-"
                    if(mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvWeekTotalTime(weekPosition, totalTimeValue);
                    break;
                case 4:
                  //  if(mWeakRefContext != null && mWeakRefContext.get() != null)

                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandlerThread != null){
            mHandlerThread.quit();
            mHandlerThread.interrupt();
        }
    }

 /*   private void startWorker(int year, int month) {
        _workerThread = new Worker("HandlerThread",year, month);
        _workerThread.start();
        System.out.println("before");
        _workerThread.getLooper();
        System.out.println("_workerThread==null");
        if(_workerThread!=null) {
            System.out.println("_workerThread!=null");
            _workerThread.loadColumns();
            //           System.out.println("thread diferente de null");
            //         Looper looper = _workerThread.getLooper();
            //       mhandler = new Handler(looper, this);
        }
        //handlerInitialization();
    }*/

    /*   private void handlerInitialization() {
           mhandler = new Handler(_workerThread.getLooper()) {
               @Override
               public void handleMessage(Message msg) {
                   super.handleMessage(msg);
                   switch(msg.what){
                       case 0:
                           int day =  msg.getData().getInt("dayPosition");
                           String dayOfWeekValue =  msg.getData().getString("dayValue");
                           setTvDaysOfTheWeek(day, dayOfWeekValue);
                           break;
                       case 1:
                           int week =  msg.getData().getInt("weekPosition");
                           String weekValue =  msg.getData().getString("weekValue");
                           setTvWeekHeader(week, weekValue);
                           break;
                       case 2:
                           int dayPos =  msg.getData().getInt("dayPosition");
                           int weekPos =  msg.getData().getInt("weekPosition");
                           String timeValue =  msg.getData().getString("timeValue");
                           String dayValue =  msg.getData().getString("dayValue");
                           if(!timeValue.equals("-"))
                               setTvDayTime(dayPos, weekPos, timeValue);
                           setTvDay(dayPos, weekPos, dayValue);
                           break;
                       case 3:
                           int weekPosition =  msg.getData().getInt("weekPosition");
                           String totalTimeValue =  msg.getData().getString("totalTimeValue");
                           //TODO - verificar se é == "-"
                           setTvWeekTotalTime(weekPosition, totalTimeValue);
                           break;
                       case 4:

                           break;
                   }
               }
           };
       }
   */
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

    static void setTvWeekTotalTime(int week, String value){
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
    static void setTvDayTime(int day, int week, String value){
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

    static void setTvWeekHeader(int week, String value){
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
        tvDay=null;
    }

    static void setTvDaysOfTheWeek(int dayOfWeek, String value){
        listDaysOfTheWeek.get(dayOfWeek).setText(""+value);
    }

    @Override
    public void onResume() {
        super.onResume();

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

                //RegisterNfc.getInstance().newNfcDetected(context, idCalendar, currentMilleseconds);
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

       public boolean handleMessage(Message msg) {
        switch(msg.what){
            case 0:
                int day =  msg.getData().getInt("dayPosition");
                String dayOfWeekValue =  msg.getData().getString("dayValue");
                setTvDaysOfTheWeek(day, dayOfWeekValue);
                break;
            case 1:
                int week =  msg.getData().getInt("weekPosition");
                String weekValue =  msg.getData().getString("weekValue");
                setTvWeekHeader(week, weekValue);
                break;
            case 2:
                int dayPos =  msg.getData().getInt("dayPosition");
                int weekPos =  msg.getData().getInt("weekPosition");
                String timeValue =  msg.getData().getString("timeValue");
                String dayValue =  msg.getData().getString("dayValue");
                if(!timeValue.equals("-"))
                    setTvDayTime(dayPos, weekPos, timeValue);
                setTvDay(dayPos, weekPos, dayValue);
                break;
            case 3:
                int weekPosition =  msg.getData().getInt("weekPosition");
                String totalTimeValue =  msg.getData().getString("totalTimeValue");
                //TODO - verificar se é == "-"
                setTvWeekTotalTime(weekPosition, totalTimeValue);
                break;
            case 4:

                break;
        }
        return true;
    }


/*
    class Worker extends HandlerThread{
        int year;
        int month;

        private Handler mhandler;
        long timeStartOfWeek;
        @Override
        public void run() {

        }

        public Worker(String name, int year, int month) {
            super(name);
            this.year=year;
            this.month=month;
            CreateMonth monthToLoad = new CreateMonth(getActivity(), year, month);
            this.timeStartOfWeek = monthToLoad.getStartWeeksToShow();


        }

        @Override
        protected void onLooperPrepared() {
            System.out.println("onLooperPrepared");
            mhandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch(msg.what){
                        case 0:
                            int day =  msg.getData().getInt("dayPosition");
                            String dayOfWeekValue =  msg.getData().getString("dayValue");
                            setTvDaysOfTheWeek(day, dayOfWeekValue);
                            break;
                        case 1:
                            int week =  msg.getData().getInt("weekPosition");
                            String weekValue =  msg.getData().getString("weekValue");
                            setTvWeekHeader(week, weekValue);
                            break;
                        case 2:
                            int dayPos =  msg.getData().getInt("dayPosition");
                            int weekPos =  msg.getData().getInt("weekPosition");
                            String timeValue =  msg.getData().getString("timeValue");
                            String dayValue =  msg.getData().getString("dayValue");
                            if(!timeValue.equals("-"))
                                setTvDayTime(dayPos, weekPos, timeValue);
                            setTvDay(dayPos, weekPos, dayValue);
                            break;
                        case 3:
                            int weekPosition =  msg.getData().getInt("weekPosition");
                            String totalTimeValue =  msg.getData().getString("totalTimeValue");
                            //TODO - verificar se é == "-"
                            setTvWeekTotalTime(weekPosition, totalTimeValue);
                            break;
                        case 4:

                            break;
                    }
                }
            };
        }

        public void loadColumns() {
            System.out.println("loadColumns");
            while (mhandler == null)
                System.out.println("WHILE NULL");
            //Log.i("while null", "Not init yet"); //It keeps on looping here
            long week_milli = 604800000;
            for (int week = 0; week < 6; week++) {

                if (week == 0) {

                    createLeftColumn();


                } else {
                    final long initial_week_time = this.timeStartOfWeek + ((week - 1) * week_milli);

                    createWeekColumns(week, initial_week_time);

                    //createInitialWeek(week, timeStartOfWeek);
                    //
                }

            }

            //mHandler.sendEmptyMessage(1);
        }
        void createWeekColumns(int week, long timeStartOfWeek) {

            long iniWeek;
            long total = 0;
            createHeaderColumns(week, timeStartOfWeek);
            long dayTime = 86400000;
            long totalWeekDuration=0;
            for (int day = 0; day < 7; day++) {

                //createHeaderColumns(week, timeStartOfWeek, layout); //-> so para verificar que esta correto
                iniWeek = timeStartOfWeek + (dayTime * day);
                totalWeekDuration = totalWeekDuration + createDays(week, day, iniWeek);
                total = total + iniWeek;
            }
            createTotalWeekHour(week, totalWeekDuration);
        }

        private void createTotalWeekHour(int week, long duration) {
            //long totalDuration = listOfWeeks.get(col - 1).getTotalHours();
            String durationString = "-";

            //System.out.println("col= " + col + " > " + listOfWeeks.get(col - 1).getTotalHours());
            if (duration > 0) {
                long numOfDays = 0;
                if (duration >= 86400000) {
                    numOfDays = duration / 86400000;
                    numOfDays = numOfDays * 24;
                }
                // System.out.println("numOfDays= " + numOfDays);

                //System.out.println("totalDuration= " + totalDuration);

                int minutes = (int) ((duration / (1000 * 60)) % 60);
                int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
                hours = (int) numOfDays + hours;
                if (hours > 0 || minutes > 0) {
                    if (minutes < 10)
                        durationString = hours + ".0" + minutes;
                    else
                        durationString = hours + "." + minutes;

                }
                Message message = mhandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("weekPosition", week); // for example

                b.putString("totalTimeValue", durationString); // for example
                message.setData(b);
                message.what=3;
                mhandler.sendMessage(message);
            }



            //listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
            //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));


            //          setTvTotalHoursWeek(col, durationString, layout);

        }

        private long createDays(int local_week, final int local_day, final long timeStartOfWeek) {
            final int week = local_week - 1;

            final LocalEventService lEventService = new LocalEventService(context);

            //  tvDayTime[local_day][week] = (TextView) rlDay.findViewById(R.id.tvMainValue);
            // tvDay[local_day][week] = (TextView) rlDay.findViewById(tvSecondValue);
            //tvSecondValue.setText("" + LocalTime.getDay(timeStartOfWeek));

            List<EventClass> listOfEvents = lEventService.getEventsForDay(timeStartOfWeek, (timeStartOfWeek + 86400000));
            DayClassTMP dayObject = new DayClassTMP(listOfEvents);

            Message message = mhandler.obtainMessage();
            Bundle b = new Bundle();

            b.putInt("dayPosition", local_day); // for example
            b.putInt("weekPosition", week); // for example
            b.putString("dayValue",""+LocalTime.getDay(timeStartOfWeek)); // for example
            b.putString("timeValue", dayObject.toString()); // for example
            message.setData(b);
            message.what=2;
            //listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
            //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));

            mhandler.sendMessage(message);
            return dayObject.getTotalDuration();

            //DaySchedule(new Position(day, week, timeStartOfWeek));

      /*      rl_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do you work here

                    Context context = getContext();
                    CharSequence text = "Hello Dialog day=!" + local_day + " | week= " + week;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });*/
            //loadAnimation(rlDay, (1000-local_week*70)*local_week);
        //}

      /*  private void createHeaderColumns(int pos, long timeStartOfWeek) {
            //     System.out.println(timeStartOfWeek + " >>>> " + pos + " >>>> " + timeStartOfWeek);
            int week = LocalTime.getWeekOfMonth(timeStartOfWeek);
            Message message = mhandler.obtainMessage();
            Bundle b = new Bundle();
            message.what = 1;
            b.putInt("weekPosition", pos); // for example

            if (week < 10)
                b.putString("weekValue",("W0" + week));
            else
                b.putString("weekValue",("W" + week));
            message.setData(b);
            mhandler.sendMessage(message);
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
            });*/

      //  }

        /*private void createLeftColumn() {

            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            // for the current Locale :
            //   DateFormatSymbols symbols = new DateFormatSymbols();
            String[] dayNames = symbols.getShortWeekdays();

            for (String s : dayNames) {
                //     System.out.print(s + " ");
            }

            for (int i = 0; i < 9; i++) {
                Message message = Message.obtain();
                Bundle b = new Bundle();
                message.what = 0;
                b.putInt("dayPosition", i); // for example
                if (i == 0){
                    b.putString("dayValue", " "); // for example
                }if(i == 8){
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
                mhandler.sendMessage(message);
                message.sendToTarget();
            }
        }*/

      /*  public class DayClassTMP {
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


    }
*/


    public class CustomHandlerThread extends HandlerThread {
        int year;
        int month;
        long timeStartOfWeek;
        CustomHandler mHandler;

        // use weak reference to avoid activity being leaked
        private WeakReference<UiThreadCallback> mUiThreadCallback;

        public CustomHandlerThread(String name, int year, int month) {
            super(name);
            this.year=year;
            this.month=month;
            CreateMonth monthToLoad = new CreateMonth(getActivity(), year, month);
            this.timeStartOfWeek = monthToLoad.getStartWeeksToShow();


        }


        // Get a reference to worker thread's handler after looper is prepared
        @Override
        protected void onLooperPrepared() {
            System.out.println("onLooperPrepared");
            super.onLooperPrepared();
            mHandler = new CustomHandler(getLooper());
           if(mHandler!=null){
               System.out.println("onLooperPrepared-2");
               loadColumns();
        }}

        // Used by UI thread to send message to worker thread's message queue
   /*     public void addMessage(Message message){
            if(mHandler != null) {
                mHandler.sendMessage(message);
            }
        }
*/
        public void postRunnable(Runnable runnable){
            if(mHandler != null) {
                mHandler.post(runnable);
            }
        }
        // The UiThreadCallback is used to send message to UI thread
        public void setUiThreadCallback(UiThreadCallback callback){
            this.mUiThreadCallback = new WeakReference<UiThreadCallback>(callback);
        }

        // Custom Handler. It pause the thread for some time and send a message back to UI Thread
        private class CustomHandler extends Handler {
            public CustomHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                        if(mUiThreadCallback != null && mUiThreadCallback.get() != null){
                            System.out.println("MESSAGE= " + msg.what);
                            mUiThreadCallback.get().publishToUiThread(msg);
                        }


            }
        }

        public void loadColumns() {
            System.out.println("loadColumns");
            //while (mhandler == null)
                System.out.println("WHILE NULL");
            //Log.i("while null", "Not init yet"); //It keeps on looping here
            long week_milli = 604800000;
            for (int week = 0; week < 6; week++) {

                if (week == 0) {

                    createLeftColumn();


                } else {
                    final long initial_week_time = this.timeStartOfWeek + ((week - 1) * week_milli);


                    //createInitialWeek(week, timeStartOfWeek);
                    //
                }

            }

            //mHandler.sendEmptyMessage(1);
        }

        void createWeekColumns(int week, long timeStartOfWeek) {

            long iniWeek;
            long total = 0;
            createHeaderColumns(week, timeStartOfWeek);
            long dayTime = 86400000;
          /*  long totalWeekDuration=0;
            for (int day = 0; day < 7; day++) {

                //createHeaderColumns(week, timeStartOfWeek, layout); //-> so para verificar que esta correto
                iniWeek = timeStartOfWeek + (dayTime * day);
                totalWeekDuration = totalWeekDuration + createDays(week, day, iniWeek);
                total = total + iniWeek;
            }
            createTotalWeekHour(week, totalWeekDuration);*/
        }

        private void createTotalWeekHour(int week, long duration) {
            //long totalDuration = listOfWeeks.get(col - 1).getTotalHours();
            String durationString = "-";

            //System.out.println("col= " + col + " > " + listOfWeeks.get(col - 1).getTotalHours());
            if (duration > 0) {
                long numOfDays = 0;
                if (duration >= 86400000) {
                    numOfDays = duration / 86400000;
                    numOfDays = numOfDays * 24;
                }
                // System.out.println("numOfDays= " + numOfDays);

                //System.out.println("totalDuration= " + totalDuration);

                int minutes = (int) ((duration / (1000 * 60)) % 60);
                int hours = (int) ((duration / (1000 * 60 * 60)) % 24);
                hours = (int) numOfDays + hours;
                if (hours > 0 || minutes > 0) {
                    if (minutes < 10)
                        durationString = hours + ".0" + minutes;
                    else
                        durationString = hours + "." + minutes;

                }
                Message message = Message.obtain(); // = Message.obtain();
                Bundle b = new Bundle();
                b.putInt("weekPosition", week); // for example

                b.putString("totalTimeValue", durationString); // for example
                message.setData(b);
                message.what=3;
                if(mHandler != null) {
                    mHandler.sendMessage(message);
                }
            }



            //listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
            //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));


            //          setTvTotalHoursWeek(col, durationString, layout);

        }

        private long createDays(int local_week, final int local_day, final long timeStartOfWeek) {
            final int week = local_week - 1;
            if(context == null){
                System.out.println("context=null-0");
            }
            final LocalEventService lEventService = new LocalEventService(getActivity());

            //  tvDayTime[local_day][week] = (TextView) rlDay.findViewById(R.id.tvMainValue);
            // tvDay[local_day][week] = (TextView) rlDay.findViewById(tvSecondValue);
            //tvSecondValue.setText("" + LocalTime.getDay(timeStartOfWeek));

            List<EventClass> listOfEvents = lEventService.getEventsForDay(timeStartOfWeek, (timeStartOfWeek + 86400000));
            DayClassTMP dayObject = new DayClassTMP(listOfEvents);

            Message message = Message.obtain(); // = Message.obtain();
            Bundle b = new Bundle();

            b.putInt("dayPosition", local_day); // for example
            b.putInt("weekPosition", week); // for example
            b.putString("dayValue",""+LocalTime.getDay(timeStartOfWeek)); // for example
            b.putString("timeValue", dayObject.toString()); // for example
            message.setData(b);
            message.what=2;
            //listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
            //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));

            if(mHandler != null) {
                mHandler.sendMessage(message);
            }
            return dayObject.getTotalDuration();

            //DaySchedule(new Position(day, week, timeStartOfWeek));

      /*      rl_day.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // do you work here

                    Context context = getContext();
                    CharSequence text = "Hello Dialog day=!" + local_day + " | week= " + week;
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            });*/
            //loadAnimation(rlDay, (1000-local_week*70)*local_week);
        }

        private void createHeaderColumns(int pos, long timeStartOfWeek) {
            //     System.out.println(timeStartOfWeek + " >>>> " + pos + " >>>> " + timeStartOfWeek);
            int week = LocalTime.getWeekOfMonth(timeStartOfWeek);
            Message message = Message.obtain(); // = Message.obtain();
            Bundle b = new Bundle();
            message.what = 1;
            b.putInt("weekPosition", pos); // for example

            if (week < 10)
                b.putString("weekValue",("W0" + week));
            else
                b.putString("weekValue",("W" + week));
            message.setData(b);
            if(mHandler != null) {
                mHandler.sendMessage(message);
            }
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
            });*/

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

        private void createLeftColumn() {

            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            // for the current Locale :
            //   DateFormatSymbols symbols = new DateFormatSymbols();
            String[] dayNames = symbols.getShortWeekdays();

            for (String s : dayNames) {
                //     System.out.print(s + " ");
            }
            System.out.println("perez");
            for (int i = 0; i < 9; i++) {
                System.out.println("perez-+1");

                Message message = new Message();
                Bundle b = new Bundle();
                message.what = 0;
                b.putInt("dayPosition", i); // for example
                if (i == 0){
                    b.putString("dayValue", " "); // for example
                }if(i == 8){
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
                if(mHandler != null) {
                    System.out.println("perez-+2");
                    mHandler.sendMessage(message);
                    break;
                }
              // mhandler.sendMessage(message);
               // message.sendToTarget();
            }
        }



    }


    private static class CustomRunnable implements Runnable {
        private WeakReference<Context> mWeakRefContext;



        public CustomRunnable(Context context,String name, int year, int month) {
            mWeakRefContext = new WeakReference<Context>(context);
            this.year=year;
            this.month=month;
            CreateMonth monthToLoad = new CreateMonth(mWeakRefContext, year, month);
            this.timeStartOfWeek = monthToLoad.getStartWeeksToShow();


        }
        @ Override
        public void run() {
            //Main task execution logic here
        }

        public void loadColumns() {
            System.out.println("loadColumns");
            //while (mhandler == null)
            System.out.println("WHILE NULL");
            //Log.i("while null", "Not init yet"); //It keeps on looping here
            long week_milli = 604800000;
            for (int week = 0; week < 6; week++) {

                if (week == 0) {

                    createLeftColumn();


                } else {
                    final long initial_week_time = this.timeStartOfWeek + ((week - 1) * week_milli);


                    //createInitialWeek(week, timeStartOfWeek);
                    //
                }

            }

            //mHandler.sendEmptyMessage(1);
        }
    };
}