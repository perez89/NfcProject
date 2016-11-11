package com.perez.schedulebynfc;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
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

/**
 * Created by User on 12/10/2016.
 */

public class MainShowFragmentSave extends Fragment {
    View rootView;
    Context context;
    LayoutInflater inflater;
    LinearLayout ll_WeekZero, ll_WeekOne, ll_WeekTwo, ll_WeekThree, ll_WeekFour, ll_WeekFive, ll_WeekSix;
    MonthClass monthToShow;

    int year;
    int month;
    List<TextView> listOfTextViews;
    ArrayList<LinearLayout> listOfVerticalRL = new ArrayList<LinearLayout>();
    List<WeekTotal> listOfWeeks = new ArrayList<WeekTotal>();


    TextView [ ][ ] tvDay = new TextView[7][6] ;
    List<TextView>  tvWeek = new ArrayList<TextView>();


    public static MainShowFragmentSave newInstance(int year, int month) {
        MainShowFragmentSave myFragment = new MainShowFragmentSave();

        Bundle args = new Bundle();
        args.putInt("year_CurrentView", year);
        args.putInt("month_CurrentView", month);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        System.out.println("MainShowFragment - onCreateView");
        this.inflater=inflater;
        context = getActivity();
        rootView = inflater.inflate(R.layout.fragment_show_main_save, container, false);

        listOfTextViews = new ArrayList<TextView>();
        Bundle b = getArguments();
        year = b.getInt("year_CurrentView");
        month = b.getInt("month_CurrentView");
        month++;
        //loadInitialMonth();

        listOfWeeks.add(new WeekTotal(0));
        listOfWeeks.add(new WeekTotal(1));
        listOfWeeks.add(new WeekTotal(2));
        listOfWeeks.add(new WeekTotal(3));
        listOfWeeks.add(new WeekTotal(4));
        listOfWeeks.add(new WeekTotal(5));
        TextView tv = (TextView)rootView.findViewById(R.id.tvTest);
        tv.setText("year= " + year+" " + " month= " + month);
        try {
            initialization();
        }catch (Exception e){

        }

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

    private void initViewsDaysOfTheWeek(){

    }

    private void initWeeks(){

    }

    private void initializationWeekColumns() {
        ll_WeekZero = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w0);
        ll_WeekZero.removeAllViews();
        listOfVerticalRL.add(ll_WeekZero);

        ll_WeekOne = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w1);
        ll_WeekOne.removeAllViews();
        listOfVerticalRL.add(ll_WeekOne);

        ll_WeekTwo  = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w2);
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


        ll_WeekSix = (LinearLayout) rootView.findViewById(R.id.ll_vertical_w6);
        ll_WeekSix.removeAllViews();
        listOfVerticalRL.add(ll_WeekSix);
    }



    @Override
    public Context getContext() {
        return context;
    }

    //Perez - criar o layout
    private void loadLayout(){
        CreateMonth monthToLoad = new CreateMonth(getActivity(), year, month);
        long timeStartOfWeek = monthToLoad.getStartWeeksToShow();

        loadColumns(timeStartOfWeek);



        //loadWeeks(timeStartOfWeek);

        //loadTotalWeeks();
        loadBottom();
    }

    private void loadBottom() {
    }

    private void loadColumns(long timeStartOfWeek) {
        long week_milli = 604800000;
        for(int week = 0 ; week<7 ; week++){
            ViewGroup layout = getLayout(week);
            if(week>0){
                long initial_week_time = timeStartOfWeek + ((week-1)*week_milli);
                createWeekColumns(week, initial_week_time, layout);
                //createInitialWeek(week, timeStartOfWeek);
                //
            }else{
                createLeftColumn();
            }

        }
    }




    void createWeekColumns(int week, long timeStartOfWeek, ViewGroup layout){
        createHeaderColumns(week, timeStartOfWeek, layout);
            long dayTime = 86400000;
            for(int day = 0 ; day<7 ; day++){
                   timeStartOfWeek = timeStartOfWeek + (dayTime*day);
                   createDays((week-1), day, timeStartOfWeek, layout);
              }
        createTotalWeekHour(week, layout);
    }

    private void createDays(final int local_week, final int local_day, long timeStartOfWeek, ViewGroup layout) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout rlDay = (RelativeLayout) inflater.inflate(R.layout.item_day_rl, layout, false);

        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(local_week+1);
        param.rowSpec = GridLayout.spec(local_day+1);
        rlDay.setLayoutParams(param);

        RelativeLayout rl_day = (RelativeLayout) rlDay.findViewById(R.id.rlDay);
        tvDay[local_day][local_week] = (TextView) rlDay.findViewById(R.id.tvMainValue);
        TextView tvSecondValue = (TextView) rlDay.findViewById(R.id.tvSecondValue);
        tvSecondValue.setText(""+LocalTime.getDay(timeStartOfWeek));
        DaySchedule(new Position(local_day, local_week, timeStartOfWeek));


        rl_day.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // do you work here
                int day = local_day;
                int week = local_week;

                Context context = getContext();
                CharSequence text = "Hello Dialog day=!" + day + " | week= " + week;
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        //loadAnimation(rlDay, (1000-local_week*70)*local_week);
        layout.addView(rlDay);
        //return monthToShow.getListOfWeeks().get(local_week-1).getListOfDays().get(local_day-1).getDayTotalDuration();


    }

    private void createTotalWeekHour(int col, ViewGroup layout) {
        String durationString;


        long totalDuration = listOfWeeks.get(col).getTotalHours();;

        int minutes = (int) ((totalDuration / (1000*60)) % 60);
        int hours   = (int) ((totalDuration / (1000*60*60)) % 24);

        if(hours>0 || minutes>0){
            if(minutes<10)
                durationString = hours +".0"+minutes;
            else
                durationString = hours +"."+minutes;

        }else{
            durationString = "-";
        }
        setTvTotalHoursWeek(col, durationString, layout);

    }


    private void createHeaderColumns(int pos, long timeStartOfWeek, ViewGroup layout) {
        System.out.println(timeStartOfWeek + " >>>> " + pos + " >>>> " + timeStartOfWeek);
        int week = LocalTime.getWeekOfMonth(timeStartOfWeek);
        LayoutInflater inflater = LayoutInflater.from(getContext());


        RelativeLayout rlWeek = (RelativeLayout) inflater.inflate(R.layout.item_week_rl, layout, false);

        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(pos);
        param.rowSpec = GridLayout.spec(0);
        rlWeek.setLayoutParams(param);

        RelativeLayout ll_Week = (RelativeLayout) rlWeek.findViewById(R.id.rlHeaderWeek);
        TextView tvWeek = (TextView) rlWeek.findViewById(R.id.tvHeaderWeek);
        if(week<10)
            tvWeek.setText("W0"+week);
        else
            tvWeek.setText("W"+week);


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

    private void loadWeeks(long timeStartOfWeek) {

        long dayTime = 86400000;
        long weekTime =dayTime*7;
        for(int w = 1 ; w<7 ; w++){
            for (int i = 1; i<7 ; i++){
                long timeToStartLocalWeek = timeStartOfWeek + (weekTime*(w-1)) + (dayTime * (i-1));
                createDay(w,i);
            }
        }

    }

    private void loadTotalWeeks() {
    }

    private void createDay(int w, int i) {
        monthToShow.getDurationSpecificMonth();
    }


    private void createLeftColumn() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        // for the current Locale :
        //   DateFormatSymbols symbols = new DateFormatSymbols();
        String[] dayNames = symbols.getShortWeekdays();

        for (String s : dayNames) {
            System.out.print(s + " ");
        }

        for(int i = 0 ; i < 8 ; i++){
            RelativeLayout item_left = (RelativeLayout) inflater.inflate(R.layout.item_left_rl, null, false);

            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.setGravity(Gravity.CENTER);
            param.columnSpec = GridLayout.spec(0);
            param.rowSpec = GridLayout.spec(i);
            item_left.setLayoutParams(param);

            RelativeLayout ll_WeekOne = (RelativeLayout) item_left.findViewById(R.id.rlDayLeft);
            TextView tvMainValue = (TextView) item_left.findViewById(R.id.tvDayWeek);
            if(i==0) {
                tvMainValue.setText("");
            }else{
                if (i==7) {
                    tvMainValue.setText("" + dayNames[1]);

                } else {
                    tvMainValue.setText("" + dayNames[i+1]);
                }
            }

            //gridLayoutCalendar.addView(item_left);
        }
    }


    private void createTitleRL(int i) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ViewGroup viewLayout = getLayout(i);
        RelativeLayout rlWeek = (RelativeLayout) inflater.inflate(R.layout.item_week_rl, viewLayout, false);

        RelativeLayout ll_Week = (RelativeLayout) rlWeek.findViewById(R.id.rlHeaderWeek);
        TextView tvWeek = (TextView) rlWeek.findViewById(R.id.tvHeaderWeek);
        tvWeek.setText("W0"+i);

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
        viewLayout.addView(rlWeek);
    }

    private ViewGroup getLayout(int i) {
        switch(i)
        {
            case 0 :
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

            case 6:
                return ll_WeekSix;

            default:
                return ll_WeekZero;
        }
    }

    private void loadAnimation(View ll_WeekOne, long time) {
        Animation animation   =    AnimationUtils.loadAnimation(getContext(), R.anim.animation);
        animation.setDuration(time);
        ll_WeekOne.setAnimation(animation);
        ll_WeekOne.animate();
        animation.start();
    }



    private void setTvTotalHoursWeek(final int local_week, String duration, ViewGroup layout){

        LayoutInflater inflater = LayoutInflater.from(getContext());
        RelativeLayout rlDay = (RelativeLayout) inflater.inflate(R.layout.item_total_rl, layout, false);

        RelativeLayout ll_WeekOne = (RelativeLayout) rlDay.findViewById(R.id.rlTotalWeek);
        TextView tvMainValue = (TextView) rlDay.findViewById(R.id.tvTotalWeek);

        tvMainValue.setText(duration);


        //loadAnimation(rlDay, (1000-local_week*70)*local_week);
        layout.addView(rlDay);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    public class Position{
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

    private void DaySchedule(Position position){
            long time = position.getTime();
            int row = position.getRow();
            int col = position.getCol();

            LocalEventService lEventService = new LocalEventService(context);

            List<EventClass> listOfEvents = lEventService.getEventsForDay(time, (time+86400000));
            DayClassTMP dayObject = new DayClassTMP(listOfEvents);
            setTextView(row, col, dayObject.toString());
            listOfWeeks.get(col).addTotalHours(dayObject.getTotalDuration());

    }

    private void incWeekFinish(int col) {

       // listWeekCount.get(col).addCount();
    }


    private void setTextView(int row, int col, String duration){
        tvDay[row][col].setText(""+duration);
    }

    private synchronized void addWeekDuration(int col, long duration){

        incWeekFinish(col);
        testWeekFinish(col);
        //tvDay[row][col].setText(""+duration);
    }

    private void testWeekFinish(int col) {

    }


    public class DayClassTMP {
        private List<EventClass> listOfEvents;

        long totalDuration;
        String durationString;



        public DayClassTMP(List<EventClass> listOfEvents) {
            this.listOfEvents = listOfEvents;
            setDuration();
        }

        public void setDuration(){
            long totalDuration = 0;

            for(EventClass event: listOfEvents
                    ) {
                totalDuration = totalDuration + event.getData().getDuration();

            }

            this.totalDuration = totalDuration;

            int minutes = (int) ((totalDuration / (1000*60)) % 60);
            int hours   = (int) ((totalDuration / (1000*60*60)) % 24);


            if(hours>0 || minutes>0){
                if(minutes<10)
                    this.durationString = hours +".0"+minutes;
                else
                    this.durationString = hours +"."+minutes;
            }else{
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

    private class WeekTotal{
        private int count;
        private long totalHours;
        private int week;

        public WeekTotal(int week) {
            this.count = 0;
            this.totalHours = 0;
            this.week = week;

        }

        public long getTotalHours() {
            return totalHours;
        }

        public void addTotalHours(long totalHours) {
            this.totalHours = this.totalHours + totalHours;
        }
    }
}
