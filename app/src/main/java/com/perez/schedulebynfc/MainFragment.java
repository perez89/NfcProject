package com.perez.schedulebynfc;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Support.GlobalStrings;
import Support.LocalEvent;
import Support.LocalEventService;
import Support.LocalTime;
import Support.MyHandlerThread;

import static Support.LocalTime.getCurrentMilliseconds;
import static Support.LocalTime.getFormatTime;


/**
 * Created by User on 07/10/2016.
 */
public class MainFragment extends Fragment {
    DialogDayDetail dialog;
    private static final String TAG_DIALOG_DAY_DETAIL = "tagDialogDayDetail";
    private Button btCreateCalendar, btSimulateNFC;
    private View rootView;
    // Worker _workerThread;
    private Context context;
    List<TextView> listDaysOfTheWeek;
    List<TextView> listWeekHeader;
    List<TextView> listWeekTotalTime;
    long month_time = 0;
    TextView tvMinTime, tvMaxTime, tvAverageTime, tvMonthTime;
    CardView cvMonth;
    TextView[][] tvDayTime = new TextView[7][6];
    //static TextView[][] tvDay = new TextView[7][5];
    static TextView[][] tvDay = new TextView[7][6];
    Runnable progressThread;
    //MyHandler myHandler;
    HandlerThread handlerThread;
    long CurrentDayTime = 0;
    long CurrentWeekTime = 0;
    int year_frag_show;
    int month_frag_show;
    int row = 0;
    int col = 0;


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
        context = getActivity();
        Bundle b = getArguments();
        year_frag_show = b.getInt("year_CurrentView");
        month_frag_show = b.getInt("month_CurrentView");
        //currentDay();
        month_frag_show++;
        initialization();
        setHandlerAndThread();
        firstTime();
        return rootView;

    }

    private void firstTime() {
        String currentDayTime = "";
        String currentWeekTime = "";
        String currentMonthTime = "";

        long milli = getCurrentMilliseconds();
        int current_year = LocalTime.getYear(milli);
        int current_month = LocalTime.getMonth(milli);
        if (current_year == year_frag_show && current_month == month_frag_show) {
            Bundle bundle = ((MainActivity) getContext()).getCurrentTimes();
            if (!(bundle.isEmpty())) {
                currentDayTime = bundle.getString("dayTime", "");
                currentWeekTime = bundle.getString("weekTime", "");
                currentMonthTime = bundle.getString("monthTime", "");
                refreshTime(currentDayTime, currentWeekTime, currentMonthTime);
            }
        }

    }

    private void setHandlerAndThread() {
        myHandlerThread = new MyHandlerThread("myHandlerThread");
        myHandlerThread.start();
        myHandlerThread.prepareHandler();
        try {
            myHandlerThread.postTask(new CustomRunnable(context, handler, year_frag_show, month_frag_show));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    class MyHandler extends Handler {

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {
            WeakReference<Context> mWeakRefContext = (WeakReference<Context>) msg.obj;
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:

                    int day = msg.getData().getInt("dayPosition");
                    String dayOfWeekValue = msg.getData().getString("dayValue");
                    if (mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvDaysOfTheWeek(day, dayOfWeekValue);
                    break;
                case 1:

                    int week = msg.getData().getInt("weekPosition");
                    String weekValue = msg.getData().getString("weekValue");
                    if (mWeakRefContext != null && mWeakRefContext.get() != null)
                        setTvWeekHeader(week, weekValue);
                    break;
                case 2:
                    int dayPos = msg.getData().getInt("dayPosition");
                    int weekPos = msg.getData().getInt("weekPosition");
                    String timeValue = msg.getData().getString("timeValue");
                    String dayValue = msg.getData().getString("dayValue");
                    long dayTimeToFindValue = msg.getData().getLong("dayTimeToFindValue");
                    Boolean currentDayB = msg.getData().getBoolean("currentDayB");

                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        setTvDayTime(dayPos, weekPos, timeValue, currentDayB, dayValue, dayTimeToFindValue);
                        setTvDay(dayPos, weekPos, dayValue);
                    }

                    break;
                case 3:
                    int weekPosition = msg.getData().getInt("weekPosition");
                    String totalTimeValue = msg.getData().getString("totalTimeValue");
                    //TODO - verificar se é == "-"
                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        //   (">>>>" + totalTimeValue + "<<<<<");
                        setTvWeekTotalTime(weekPosition, totalTimeValue);
                    }

                    break;
                case 4:
                    String min = msg.getData().getString("minTime");
                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        setMinTime(min);
                    }
                    break;
                case 5:
                    String max = msg.getData().getString("maxTime");
                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        setMaxTime(max);
                    }
                    break;
                case 6:
                    String average = msg.getData().getString("averageTime");
                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        setAverageTime(average);
                    }
                    break;
                case 7:
                    String month = msg.getData().getString("monthTime");
                    if (mWeakRefContext != null && mWeakRefContext.get() != null) {
                        setMonthTime(month);
                    }
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
        if (myHandlerThread != null) {
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
        initializationAditionalInfo();
    }

    private void initializationAditionalInfo() {
        tvMinTime = (TextView) rootView.findViewById(R.id.tvMinTime);
        tvMaxTime = (TextView) rootView.findViewById(R.id.tvMaxTime);
        tvAverageTime = (TextView) rootView.findViewById(R.id.tvAverageTime);
        tvMonthTime = (TextView) rootView.findViewById(R.id.tvMonthTime);
        cvMonth = (CardView) rootView.findViewById(R.id.cvMonth);
    }

    private void setMinTime(String text) {
        tvMinTime.setText("" + text);
    }

    private void setMaxTime(String text) {
        tvMaxTime.setText("" + text);
    }

    private void setAverageTime(String text) {
        tvAverageTime.setText("" + text);
    }

    private void setMonthTime(String text) {
        tvMonthTime.setText("" + text);
        cvMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialogChart(-1, GlobalStrings.MONTH_TYPE);
            }
        });
    }

    private void initializationTotalWeekTime() {
        listWeekTotalTime = new ArrayList<TextView>();

        TextView tvDay = (TextView) rootView.findViewById(R.id.tvW1Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvW2Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvW3Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvW4Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvW5Total);
        listWeekTotalTime.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvW6Total);
        listWeekTotalTime.add(tvDay);
    }

    void setTvWeekTotalTime(final int week, String value) {
        listWeekTotalTime.get(week).setText("" + value);
        listWeekTotalTime.get(week).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialogChart(week, GlobalStrings.WEEK_TYPE);
            }
        });
    }

    private void startDialogChart(int week, String chart_type){

  /*    Bundle bundle = new Bundle();
        bundle.putInt("week_CurrentView", week);
        bundle.putInt("year_CurrentView", year_frag_show);
        bundle.putInt("month_CurrentView", month_frag_show);
        bundle.putString("chartType", chart_type);

        DialogChart newFragment = DialogChart.newInstance(bundle);

        newFragment.show(getActivity().getSupportFragmentManager(), "dialog");
*/
        getActivity().finish();


        Intent intent = new Intent(getActivity(), ChartActivity.class);

        Bundle bundle = new Bundle();
        bundle.putInt("week_CurrentView", week);
        bundle.putInt("year_CurrentView", year_frag_show);
        bundle.putInt("month_CurrentView", month_frag_show);
        bundle.putString("chartType", chart_type);
        intent.putExtras(bundle);
        startActivity(intent);


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

        //week six
        tvDayTime[0][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D1);
        tvDayTime[1][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D2);
        tvDayTime[2][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D3);
        tvDayTime[3][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D4);
        tvDayTime[4][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D5);
        tvDayTime[5][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D6);
        tvDayTime[6][5] = (TextView) rootView.findViewById(R.id.tvFloatW6D7);

    }

    void setTvDayTime(int day, int week, String value, Boolean currentDayB, final String dayValue, final long time) {
        if (value.equals("0"))
            value = "0:00";
        final String value2 = value;
        tvDayTime[day][week].setText("" + value);
        if (currentDayB) {
            CardView card = (CardView) tvDayTime[day][week].getParent().getParent();
            if (card != null) {
                card.setCardBackgroundColor((Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.color_current_day)))));
            }

        }
        if (time > 0) {
            RelativeLayout rl = (RelativeLayout) tvDayTime[day][week].getParent();
            if (rl != null) {
                rl.setClickable(true);

                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // showDialogDayDetail(time);
                        startActivityDetail(time, value2);

                    }
                });
            }
        }


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

        //week five
        tvDay[0][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D1);
        tvDay[1][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D2);
        tvDay[2][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D3);
        tvDay[3][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D4);
        tvDay[4][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D5);
        tvDay[5][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D6);
        tvDay[6][5] = (TextView) rootView.findViewById(R.id.tvFloatDayW6D7);

    }

    static void setTvDay(int day, int week, String value) {
        if (!value.equals(""))
            tvDay[day][week].setText("" + value);
    }

    private void initializationWeekHeader() {
        listWeekHeader = new ArrayList<TextView>();
        TextView tvDay = (TextView) rootView.findViewById(R.id.tvWeek1);
        listWeekHeader.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvWeek2);
        listWeekHeader.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvWeek3);
        listWeekHeader.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvWeek4);
        listWeekHeader.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvWeek5);
        listWeekHeader.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvWeek6);
        listWeekHeader.add(tvDay);

        tvDay = null;
    }

    void setTvWeekHeader(int week, String value) {
        listWeekHeader.get(week).setText("" + value);
    }

    private void initializationDaysOfTheWeek() {
        listDaysOfTheWeek = new ArrayList<TextView>();
        TextView tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek0);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek1);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek2);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek3);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek4);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek5);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek6);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvDayOfWeek7);
        listDaysOfTheWeek.add(tvDay);

        tvDay = (TextView) rootView.findViewById(R.id.tvTotalPartial);
        listDaysOfTheWeek.add(tvDay);

        tvDay = null;
    }

    void setTvDaysOfTheWeek(int dayOfWeek, String value) {
        listDaysOfTheWeek.get(dayOfWeek).setText("" + value);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class CustomRunnable implements Runnable {
        private WeakReference<Context> mWeakRefContext;
        long timeStartOfWeek;
        Handler handler;
        long min_time = -1;
        long max_time = 0;

        long average_time = 0;
        int cont = 0;

        public CustomRunnable(Context context, Handler h, int year, int month) throws ParseException {
            handler = h;
            this.mWeakRefContext = new WeakReference<Context>(context);
            timeStartOfWeek = LocalTime.getInitialTimeOfLayout(year, month);
        }

        @Override
        public void run() {
            //Main task execution logic here
            loadColumns();
            loadAdditionalInfo();
        }

        private void loadAdditionalInfo() {

            loadMinTime();
            loadMaxTime();
            loadAverageTime();
            loadMonthTime();

        }

        private void loadMinTime() {
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("minTime", LocalTime.getFormatTime(min_time)); // for example
            message.setData(b);
            message.obj = mWeakRefContext;
            message.what = 4;
            handler.sendMessage(message);
        }

        private void loadMaxTime() {
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("maxTime", LocalTime.getFormatTime(max_time)); // for example
            message.setData(b);
            message.obj = mWeakRefContext;
            message.what = 5;
            handler.sendMessage(message);
        }

        private void loadAverageTime() {
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            if (cont > 0)
                average_time = average_time / cont;
            b.putString("averageTime", LocalTime.getFormatTime(average_time)); // for example
            message.setData(b);
            message.obj = mWeakRefContext;
            message.what = 6;
            handler.sendMessage(message);
        }

        private void loadMonthTime() {
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("monthTime", LocalTime.getFormatTime(month_time + CurrentDayTime)); // for example
            message.setData(b);
            message.obj = mWeakRefContext;
            message.what = 7;
            handler.sendMessage(message);
        }

        private void setAdditionalInfo(long _time) {
            cont++;
            if (_time < min_time || min_time == -1)
                min_time = _time;
            if (_time > max_time)
                max_time = _time;

            average_time = average_time + _time;
        }

        public void loadColumns() {

            //Log.i("while null", "Not init yet"); //It keeps on looping here
            final long week_milli = 604800000;
            createLeftColumn();
            for (int week = 0; week < 6; week++) {
                final long initial_week_time = timeStartOfWeek + (week * week_milli);
                createWeekColumns(week, initial_week_time);
            }
        }

        private void createLeftColumn() {
            DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
            // for the current Locale :
            //   DateFormatSymbols symbols = new DateFormatSymbols();
            //   DateFormatSymbols symbols = new DateFormatSymbols();
            String[] dayNames = symbols.getShortWeekdays();

            //for (String s : dayNames) {
            //System.out.print(s + " ");
            //}
            // ("perez");
            for (int i = 0; i < 9; i++) {
                // ("perez-+1");

                Message message = handler.obtainMessage();
                Bundle b = new Bundle();

                b.putInt("dayPosition", i); // for example
                if (i == 0) {
                    b.putString("dayValue", " "); // for example
                } else if (i == 8) {
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
                message.obj = mWeakRefContext;
                message.what = 0;
                handler.sendMessage(message);
                // message.sendToTarget();
            }
        }

        private void createWeekColumns(int week, long timeStartOfWeek) {
            long iniWeek;
            long total = 0;

            createHeaderColumns(week, timeStartOfWeek);
            long dayTime = 86400000;
            long totalWeekDuration = 0;


            for (int day = 0; day < 7; day++) {

                // createHeaderColumns(week, timeStartOfWeek, layout); //-> so para verificar que esta correto
                iniWeek = timeStartOfWeek + (dayTime * day);

                totalWeekDuration = totalWeekDuration + createDays(week, day, iniWeek);

            }
            createTotalWeekHour(week, totalWeekDuration);
        }

        private long createDays(final int local_week, final int local_day, final long timeStartOfWeek) {

            boolean currentDayB = false;
            //  final int currentWeekOfMonth = local_week;
            final LocalEventService lEventService = new LocalEventService(mWeakRefContext);

            List<LocalEvent> listOfEvents = lEventService.getEventsForDay(timeStartOfWeek, (timeStartOfWeek + 86400000));
            // ("local_week =" + local_week + " | local_day= " + local_day + " | listOfEvents= " + listOfEvents.size());
            DayClassTMP dayObject = new DayClassTMP(listOfEvents);

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            int year_event = LocalTime.getYear(timeStartOfWeek);
            int month_event = LocalTime.getMonth(timeStartOfWeek);
            int day_event = LocalTime.getDay(timeStartOfWeek);
            int week_event = local_week;
            month_event++;
            long milli = getCurrentMilliseconds();
            //    ("week_event= " + week_event);

            int currentWeekOfMonth = LocalTime.getWeekOfMonth(milli);
            int currentDay = LocalTime.getDay(milli);
            int currentYear = LocalTime.getYear(milli);
            int currentMonth = LocalTime.getMonth(milli);
            currentMonth++;
            long time = dayObject.getTotalDuration();
            // ("zerep1week= " + currentWeekOfMonth + " local_week= "+local_week);

            if (year_event == year_frag_show && month_event == month_frag_show) {
                if (day_event == currentDay && year_event == currentYear && month_event == currentMonth) {
                    currentDayB = true;

                    //totalCurrentDayTime = dayObject.getTotalDuration();

                    if (listOfEvents.size() > 0 && !(listOfEvents.get(listOfEvents.size() - 1).isClose())) {
                        time = time + (milli - listOfEvents.get(listOfEvents.size() - 1).getData().getStartTime());
                        CurrentDayTime = time;
                        col = local_week;
                        row = local_day;

                    }
                } else {
                    if (time > 0) {
                        setAdditionalInfo(time);

                        if (currentWeekOfMonth == week_event)
                            CurrentWeekTime = CurrentWeekTime + time;

                        month_time = month_time + time;
                    }

                }

            } else {

            }


            //obter o tempo de todos os dias desse mes
            // if (year_event == year_frag_show && month_event == month_frag_show && day_event < currentDay && time > 0) {
            //   setAdditionalInfo(time);

            //obter o tempo do corrente dia
            //     } else if (year_event == year_frag_show && month_event == month_frag_show && day_event == currentDay) {
            //          if (currentYear == year_frag_show && currentMonth == month_frag_show)

            //           ("totalDayTime= " + CurrentDayTime);
            //      }

            b.putInt("dayPosition", local_day); // for example
            b.putInt("weekPosition", local_week); // for example
            b.putString("dayValue", "" + day_event); // for example
            b.putString("timeValue", getFormatTime(time)); // for example
            b.putLong("dayTimeToFindValue", timeStartOfWeek); // for example
            b.putBoolean("currentDayB", currentDayB); // for example

            message.setData(b);

            message.obj = mWeakRefContext;
            message.what = 2;
            handler.sendMessage(message);

            listOfEvents.clear();
            dayObject = null;
            return time;
        }

        private void createHeaderColumns(int pos, long timeStartOfWeek) {
            // ("createHeaderColumns= " + timeStartOfWeek);

            //  (timeStartOfWeek + " >>>> " + pos + " >>>> " + timeStartOfWeek);
            int week = LocalTime.getWeekOfYear(timeStartOfWeek);
            //week= week-1;

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();


            b.putInt("weekPosition", pos); // for example

            if (week < 10)
                b.putString("weekValue", ("W0" + week));
            else
                b.putString("weekValue", ("W" + week));
            message.setData(b);
            message.obj = mWeakRefContext;
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
            private List<LocalEvent> listOfEvents;

            long totalDuration;
            String durationString;

            public DayClassTMP(List<LocalEvent> listOfEvents) {
                this.listOfEvents = listOfEvents;
                setDuration();
            }

            public void setDuration() {
                long totalDuration = 0;

                for (LocalEvent event : listOfEvents
                        ) {

                    totalDuration = totalDuration + event.getData().getDuration();
                }

                this.totalDuration = totalDuration;

                int minutes = (int) ((totalDuration / (1000 * 60)) % 60);
                int hours = (int) ((totalDuration / (1000 * 60 * 60)) % 24);

                long numOfHours = 0;
                if (totalDuration >= 86400000) {
                    numOfHours = totalDuration / 86400000;
                    numOfHours = numOfHours * 24;
                    hours = hours + (int) numOfHours;
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

        private void createTotalWeekHour(int week, long duration) {

            //long totalDuration = listOfWeeks.get(col - 1).getTotalHours();
            String durationString = "-";
            Message message = handler.obtainMessage();
            Bundle b = new Bundle();

            // ("col= " + col + " > " + listOfWeeks.get(col - 1).getTotalHours());
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
                        durationString = hours + ":0" + minutes;
                    else
                        durationString = hours + ":" + minutes;

                }
                b.putString("totalTimeValue", durationString); // for example
                message.setData(b);

            } else {
                b.putString("totalTimeValue", durationString); // for example
                message.setData(b);
            }
            b.putInt("weekPosition", week); // for example
            message.obj = mWeakRefContext;
            message.what = 3;
            handler.sendMessage(message);


            //listOfWeeks.get(week).addTotalHours(dayObject.getTotalDuration());
            //qemsg.obj = new Position(row, col, Long.parseLong(dayObject.toString()));


            //          setTvTotalHoursWeek(col, durationString, layout);

        }
    }

    private class CurrentDay {
        private int x;
        private int y;

        public CurrentDay(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }


    public void refreshTime(String dayTime, String weekTime, String monthTime) {

        Message message = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putString("monthTime", monthTime); // for example
        message.setData(b);
        message.obj = new WeakReference<Context>(getContext());
        message.what = 7;
        handler.sendMessage(message);

        message = handler.obtainMessage();
        b = new Bundle();

        b.putInt("dayPosition", row); // for example
        b.putInt("weekPosition", col); // for example
        b.putString("dayValue", ""); // for example
        b.putString("timeValue", dayTime); // for example
        message.setData(b);

        message.obj = new WeakReference<Context>(getContext());
        message.what = 2;
        handler.sendMessage(message);

        message = handler.obtainMessage();
        b = new Bundle();
        b.putInt("weekPosition", col); // for example
        b.putString("totalTimeValue", weekTime); // for example
        //  ("wwww= " + getFormatTime(CurrentWeekTime+time));
        message.setData(b);
        message.obj = new WeakReference<Context>(getContext());
        message.what = 3;
        handler.sendMessage(message);
    }

    private void showDialogDayDetail(long time) {
        dismissDialog();
        dialog = new DialogDayDetail();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong("timeDayDetails", time);
        dialog.setArguments(args);

        //show dialog
        dialog.show(getActivity().getSupportFragmentManager(), TAG_DIALOG_DAY_DETAIL);
    }

    public void startActivityDetail(long time, String dayDuration) {
        getActivity().finish();
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        Bundle args = new Bundle();
        args.putLong("timeDayDetails", time);
        args.putString("dayDurationDetails", dayDuration);

        intent.putExtras(args);
        // intent.putExtra("timeDayDetails", time);
        startActivity(intent);
        //startActivityForResult(intent, 1);
    }

    private void dismissDialog() {
        if (showingDialog()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    private boolean showingDialog() {
        if (dialog != null)
            return true;
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        dismissDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        dismissDialog();
    }

}