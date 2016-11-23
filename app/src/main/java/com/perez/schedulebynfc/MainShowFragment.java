package com.perez.schedulebynfc;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Support.CreateMonth;
import Support.EventClass;
import Support.LocalEventService;
import Support.LocalTime;
import Support.MonthClass;

import static com.perez.schedulebynfc.R.id.tvSecondValue;

/**
 * Created by User on 12/10/2016.
 */

public class MainShowFragment extends Fragment {
    View rootView;
    Context context;
    LayoutInflater inflater;
    LinearLayout ll_WeekZero, ll_WeekOne, ll_WeekTwo, ll_WeekThree, ll_WeekFour, ll_WeekFive;
    int count_column_finish;
    List<RelativeLayout> rl_weeks = new ArrayList<RelativeLayout>();
    MonthClass monthToShow;
    Handler handler;
    int year;
    int month;
    List<TextView> listOfTextViews;
    ArrayList<LinearLayout> listOfVerticalRL = new ArrayList<LinearLayout>();
    List<Total> listOfWeeks = new ArrayList<Total>();
    Total MonthTotal = new Total();

    TextView[][] tvDayTime = new TextView[7][6];
    TextView[][] tvDay = new TextView[7][6];
    List<TextView> tvWeek = new ArrayList<TextView>();


    public static MainShowFragment newInstance(int year, int month) {
        MainShowFragment myFragment = new MainShowFragment();

        Bundle args = new Bundle();
        args.putInt("year_CurrentView", year);
        args.putInt("month_CurrentView", month);
        myFragment.setArguments(args);

        return myFragment;
    }

    void addView(int layout){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("MainShowFragment - onCreateView");

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0){
                    int row =  msg.getData().getInt("row");
                    int col =  msg.getData().getInt("col");
                    String time =  msg.getData().getString("time");
                    String day =  msg.getData().getString("day");
                    if(!time.equals("-"))
                        setTextViewDayTime(row, col, time);
                    setTextViewDay(row, col, day);
                }
                if(msg.what == 2){
                    int col =  msg.getData().getInt("col");


                }

            }
        };

        count_column_finish=0;
        this.inflater = inflater;
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_show_main, container, false);

        listOfTextViews = new ArrayList<TextView>();
        Bundle b = getArguments();
        year = b.getInt("year_CurrentView");
        month = b.getInt("month_CurrentView");
        month++;
        //loadInitialMonth();

        listOfWeeks.add(new Total());
        listOfWeeks.add(new Total());
        listOfWeeks.add(new Total());
        listOfWeeks.add(new Total());
        listOfWeeks.add(new Total());
        listOfWeeks.add(new Total());
        TextView tv = (TextView) rootView.findViewById(R.id.tvTest);
        tv.setText("year= " + year + " " + " month= " + month);
        try {
            initialization();
        } catch (Exception e) {

        }
        LinearLayout rlFix = (LinearLayout) rootView.findViewById(R.id.rlFix);

        return rootView;

    }

    private void loadInitialMonth() {

        CreateMonth monthToLoad = new CreateMonth(getActivity(), year, month);
        monthToShow = monthToLoad.loadAndGetMonth();
    }

    private void initialization() {
        initializationWeekColumns();
        loadLayout();
    }


    private void initializationWeekColumns() {
        ll_WeekZero = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w0);
        ll_WeekZero.removeAllViews();
        listOfVerticalRL.add(ll_WeekZero);

        ll_WeekOne = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w1);
        ll_WeekOne.removeAllViews();
        listOfVerticalRL.add(ll_WeekOne);

        ll_WeekTwo = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w2);
        ll_WeekTwo.removeAllViews();
        listOfVerticalRL.add(ll_WeekTwo);

        ll_WeekThree = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w3);
        ll_WeekThree.removeAllViews();
        listOfVerticalRL.add(ll_WeekThree);

        ll_WeekFour = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w4);
        ll_WeekFour.removeAllViews();
        listOfVerticalRL.add(ll_WeekFour);

        ll_WeekFive = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w5);
        ll_WeekFive.removeAllViews();
        listOfVerticalRL.add(ll_WeekFive);
    }


    @Override
    public Context getContext() {
        return context;
    }

    //Perez - criar o layout
    private void loadLayout() {
        CreateMonth monthToLoad = new CreateMonth(getActivity(), year, month);
        long timeStartOfWeek = monthToLoad.getStartWeeksToShow();

        loadColumns(timeStartOfWeek);


        //loadWeeks(timeStartOfWeek);

        //loadTotalWeeks();



    }

    private void loadBottom() {
    }

    private void loadColumns(long timeStartOfWeek) {
       // System.out.println("loadColumns");
        long week_milli = 604800000;
        for (int week = 0; week < 6; week++) {
            final ViewGroup layout = getLayout(week);
            if (week == 0) {

                        createLeftColumn(layout,week);


            } else {
                final long initial_week_time = timeStartOfWeek + ((week - 1) * week_milli);
                final int finalWeek = week;
                createWeekColumns(finalWeek, initial_week_time, layout);

                //createInitialWeek(week, timeStartOfWeek);
                //
            }

        }

    }

    void createWeekColumns(int week, long timeStartOfWeek, ViewGroup layout) {

        long iniWeek;
        long total = 0;
        createHeaderColumns(week, timeStartOfWeek, layout);
        long dayTime = 86400000;
        for (int day = 0; day < 7; day++) {

            //createHeaderColumns(week, timeStartOfWeek, layout); //-> so para verificar que esta correto
            iniWeek = timeStartOfWeek + (dayTime * day);
            createDays(week, day, iniWeek, layout);
            total = total + iniWeek;
        }
        createTotalWeekHour(week, layout);
    }

    private void createDays(int local_week, final int local_day, final long timeStartOfWeek, final ViewGroup layout) {
        final int week = local_week - 1;
        final RelativeLayout rlDay = (RelativeLayout) inflater.inflate(R.layout.item_day_rl, layout, false);
        RelativeLayout rl_day = (RelativeLayout) rlDay.findViewById(R.id.rlDay);


        final LocalEventService lEventService = null;
        //new LocalEventService(context);
        new Thread() {
            public void run() {
                //System.out.println("local_week= " + week + "    local_day= " + day);
                LayoutInflater inflater = LayoutInflater.from(getContext());




                tvDayTime[local_day][week] = (TextView) rlDay.findViewById(R.id.tvMainValue);
                tvDay[local_day][week] = (TextView) rlDay.findViewById(tvSecondValue);
                //tvSecondValue.setText("" + LocalTime.getDay(timeStartOfWeek));

                    List<EventClass> listOfEvents = lEventService.getEventsForDay(timeStartOfWeek, (timeStartOfWeek + 86400000));
                    DayClassTMP dayObject = new DayClassTMP(listOfEvents);

                    Message message = handler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putInt("row", local_day); // for example
                    b.putInt("col", week); // for example
                    b.putString("day",""+LocalTime.getDay(timeStartOfWeek)); // for example
                    b.putString("time", dayObject.toString()); // for example
                    message.setData(b);
                    message.what=0;
                    listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
                    //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));

                    handler.sendMessage(message);
                    dayObject=null;
                    listOfEvents=null;
                    b=null;
                    message=null;
            }}.start();

        //DaySchedule(new Position(day, week, timeStartOfWeek));

        rl_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do you work here

                Context context = getContext();
                CharSequence text = "Hello Dialog day=!" + local_day + " | week= " + week;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        //loadAnimation(rlDay, (1000-local_week*70)*local_week);
        layout.addView(rlDay);

        //return monthToShow.getListOfWeeks().get(local_week-1).getListOfDays().get(local_day-1).getDayTotalDuration();
    }

    private void createHeaderColumns(int pos, long timeStartOfWeek, ViewGroup layout) {
        //     System.out.println(timeStartOfWeek + " >>>> " + pos + " >>>> " + timeStartOfWeek);
        int week = LocalTime.getWeekOfMonth(timeStartOfWeek);
        LayoutInflater inflater = LayoutInflater.from(getContext());

        RelativeLayout rlWeek = (RelativeLayout) inflater.inflate(R.layout.item_week_rl, layout, false);

        RelativeLayout ll_Week = (RelativeLayout) rlWeek.findViewById(R.id.rlHeaderWeek);
        TextView tvWeek = (TextView) rlWeek.findViewById(R.id.tvHeaderWeek);
        if (week < 10)
            tvWeek.setText("W0" + week);
        else
            tvWeek.setText("W" + week);

        ll_Week.setOnClickListener(new View.OnClickListener() {

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
        layout.addView(rlWeek);
    }

    private void createTotalWeekHour(int col, ViewGroup layout) {
        long totalDuration = listOfWeeks.get(col - 1).getTotalHours();
        String durationString = "-";

        System.out.println("col= " + col + " > " + listOfWeeks.get(col - 1).getTotalHours());
        if (totalDuration != 0) {




            long numOfDays = 0;
            if (totalDuration >= 86400000) {
                numOfDays = totalDuration / 86400000;
                numOfDays = numOfDays * 24;
            }
           // System.out.println("numOfDays= " + numOfDays);

            //System.out.println("totalDuration= " + totalDuration);

            int minutes = (int) ((totalDuration / (1000 * 60)) % 60);
            int hours = (int) ((totalDuration / (1000 * 60 * 60)) % 24);
            hours = (int) numOfDays + hours;
            if (hours > 0 || minutes > 0) {
                if (minutes < 10)
                    durationString = hours + ".0" + minutes;
                else
                    durationString = hours + "." + minutes;

            }
        }
        setTvTotalHoursWeek(col, durationString, layout);
        addTotalMonth(totalDuration);
        if(isColumnsFinish()){
            loadBottom();
        }
    }


    private void createLeftColumn(ViewGroup layout, int col) {
      //  System.out.println("createLeftColumn");
        LayoutInflater inflater = LayoutInflater.from(getContext());
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        // for the current Locale :
        //   DateFormatSymbols symbols = new DateFormatSymbols();
        String[] dayNames = symbols.getShortWeekdays();

        for (String s : dayNames) {
       //     System.out.print(s + " ");
        }

        for (int i = 0; i < 9; i++) {
            LinearLayout item_left = (LinearLayout) inflater.inflate(R.layout.item_left_rl, layout, false);

            LinearLayout ll_Week = (LinearLayout) item_left.findViewById(R.id.rlDayLeft);
            // rl_weeks.add(ll_Week);

            TextView tvMainValue = (TextView) item_left.findViewById(R.id.tvDayWeek);
            if ((i == 0) || (i == 8)) {
                tvMainValue.setText("");
            } else {
                if (i == 7) {
                    String output = dayNames[1].substring(0, 1).toUpperCase() + dayNames[1].substring(1);
                    tvMainValue.setText("" + output);

                } else {
                    String output = dayNames[i + 1].substring(0, 1).toUpperCase() + dayNames[i + 1].substring(1);
                    tvMainValue.setText("" + output);
                }
            }
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("col", col); // for example
            message.what = 2;
            message.setData(b);
            message.obj =item_left;
            handler.sendMessage(message);

           // layout.addView();
            addTotalMonth(0);
            if(isColumnsFinish()){
                loadBottom();
            }
        }
    }


    private ViewGroup getLayout(int i) {
        switch (i) {
            case 0:
                return ll_WeekZero;
            case 1:
                return ll_WeekOne;

            case 2:
                return ll_WeekTwo;

            case 3:
                return ll_WeekThree;

            case 4:
                return ll_WeekFour;

            case 5:
                return ll_WeekFive;


            default:
                return ll_WeekZero;
        }
    }


    private void setTvTotalHoursWeek(final int local_week, String duration, ViewGroup layout) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout rlDay = (RelativeLayout) inflater.inflate(R.layout.item_total_rl, layout, false);

        RelativeLayout ll_WeekOne = (RelativeLayout) rlDay.findViewById(R.id.rlTotalWeek);
        TextView tvMainValue = (TextView) rlDay.findViewById(R.id.tvTotalWeek);

        tvMainValue.setText(duration);


        //loadAnimation(rlDay, (1000-local_week*70)*local_week);
        layout.addView(rlDay);
    }


    private void loadAnimation(View ll_WeekOne, long time) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.animation);
        animation.setDuration(time);
        ll_WeekOne.setAnimation(animation);
        ll_WeekOne.animate();
        animation.start();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public class Position {
        int row;
        int col;
        long time;

        public Position(int row, int col, long time) {
            this.row = row;
            this.col = col;
            this.time = time;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public long getTime() {
            return time;
        }
    }

    private void DaySchedule(Position position) {

        //System.out.println("size events= " + listOfEvents.size() + " | week= " + col + " | day= " + row + " | time= " + dayObject.getTotalDuration() + ">>>" +dayObject.toString());
        //System.out.println(col + " + " + row + "total week= " + listOfWeeks.get(col).getTotalHours());
    }

    private synchronized void addTotalMonth(long time) {
        MonthTotal.addTotalHours(time);
    }

    private void setTextViewDayTime(int row, int col, String duration) {
        tvDayTime[row][col].setText("" + duration);
    }


    private void setTextViewDay(int row, int col, String day) {
        tvDay[row][col].setText("" + day);
    }
    private synchronized boolean isColumnsFinish() {
           return MonthTotal.isFinish();
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

    private class Total {
        private int count;
        private long totalHours;

        public Total() {
            this.count = 0;
            this.totalHours = 0;

        }
        public boolean isFinish(){
            if(count==6)
                return true;
            else
                return false;
        }

        public long getTotalHours() {
            return this.totalHours;
        }

        public void addTotalHours(long time) {
            count++;
            this.totalHours = this.totalHours + time;
        }
    }

}
