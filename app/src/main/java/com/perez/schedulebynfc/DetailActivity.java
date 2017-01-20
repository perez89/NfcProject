package com.perez.schedulebynfc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import Support.LocalEvent;
import Support.EventData;
import Support.LocalEventService;
import Support.LocalTime;
import Support.MyAdaterDayDetails;

import static com.perez.schedulebynfc.R.id.rvListDays;

/**
 * Created by User on 17/01/2017.
 */

public class DetailActivity extends AppCompatActivity {
    TextView tvEmptyRv;
    TextView tvDayDate;
    TextView tvDayDateDayOfTheWeek;
    TextView tvDayDuration;
    List<EventData> listOfEvents;
    private RecyclerView mRecyclerView;
    private MyAdaterDayDetails mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private DetailActivity.MyHandler handler = new DetailActivity.MyHandler();
    private DetailActivity.MyHandlerThread myHandlerThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getIntent().getExtras();

        if (b == null) {
            System.out.println("DetailActivity==null");
            return;
        } else {
            System.out.println("DetailActivity!=null");
            setContentView(R.layout.activity_detail);
            long time = b.getLong("timeDayDetails", 0);
            String dayDuration = b.getString("dayDurationDetails", "");

            // ArrayOfEvents = new MainFragment[3];
            initialization(time);
            setDayDuration(dayDuration);
            //test(savedInstanceState);
        }
    }

    private void initialization(long time) {
        mRecyclerView = (RecyclerView) findViewById(rvListDays);
        tvEmptyRv = (TextView) findViewById(R.id.tvEmptyRv);
        tvDayDate = (TextView) findViewById(R.id.tvDialogDate);
        tvDayDateDayOfTheWeek = (TextView) findViewById(R.id.tvDialogDayOfTheWeek);
        tvDayDuration = (TextView) findViewById(R.id.tvDuration);
        //setDate(ails);

        if (time > 0) {
            recycleView();
            setHandlerAndThread(time);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyRv.setVisibility(View.VISIBLE);
        }
    }

    private void setDateDayWeek(String text) {
        tvDayDateDayOfTheWeek.setText(text);
    }

    private void setDate(String text) {
        tvDayDate.setText(text);
    }

    private void setHandlerAndThread(long time) {
        myHandlerThread = new DetailActivity.MyHandlerThread("myHandlerThread");
        myHandlerThread.start();
        myHandlerThread.prepareHandler();

        try {
            myHandlerThread.postTask(new DetailActivity.CustomRunnableGetDate(this, handler, time));
            myHandlerThread.postTask(new DetailActivity.CustomRunnableGetEvents(this, handler, time));

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private class CustomRunnableGetDate implements Runnable {
        WeakReference<Context> mWeakRefContext;
        Handler handler;
        long time = 0;

        public CustomRunnableGetDate(Context context, Handler h, long _time) throws ParseException {
            handler = h;
            this.mWeakRefContext = new WeakReference<Context>(context);
            this.time = _time;

        }

        @Override
        public void run() {

            String dayOfWeekText = LocalTime.getDayOfWeekFormatText(time);
            int day = LocalTime.getDay(time);
            int year = LocalTime.getYear(time);
            int tmpMonth = LocalTime.getMonth(time);
            String month = LocalTime.getMonthStringFormat(tmpMonth);

            String textDisplay = day + " " + month + " " + year;

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putString("textDisplayDayWeek", dayOfWeekText); // for example
            b.putString("textDisplay", textDisplay); // for example
            message.setData(b);
            message.obj = mWeakRefContext;
            message.what = 1;

            handler.sendMessage(message);
        }
    }

    private class CustomRunnableGetEvents implements Runnable {
        WeakReference<Context> mWeakRefContext;
        Handler handler;
        long time;

        public CustomRunnableGetEvents(Context context, Handler h, long _time) throws ParseException {
            handler = h;
            this.mWeakRefContext = new WeakReference<Context>(context);
            this.time = _time;
        }

        @Override
        public void run() {
            final LocalEventService lEventService = new LocalEventService(mWeakRefContext);

            List<LocalEvent> tmpListOfEvents = lEventService.getEventsForDay(time, (time + 86400000));
            //  listOfEvents = tmpListOfEvents;

            if (tmpListOfEvents.size() > 0) {
                System.out.println("size of events= " + tmpListOfEvents.size());
                for (LocalEvent event :
                        tmpListOfEvents) {

                    Message message = handler.obtainMessage();
                    message.obj = event.getData();
                    message.what = 0;

                    handler.sendMessage(message);

                }
            }

            // System.out.println("local_week =" + local_week + " | local_day= " + local_day + " | listOfEvents= " + listOfEvents.size());
        }
    }

    private class MyHandlerThread extends HandlerThread {

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

    class MyHandler extends Handler {

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    EventData event = (EventData) msg.obj;
                    addEventToList(event);
                    // loadRecycleView(mWeakRefContext);
                    //addCardView(createCardView("in", "out", "total"));
                    break;
                case 1:
                    String textDisplay = msg.getData().getString("textDisplay");
                    String textDisplayDayWeek = msg.getData().getString("textDisplayDayWeek");
                    setDate(textDisplay);
                    setDateDayWeek(textDisplayDayWeek);
                    break;

            }
        }
    }

    private void addEventToList(EventData event) {
        listOfEvents.add(event);
        mAdapter.notifyData(listOfEvents);
    }

    private void setDayDuration(String text) {
        tvDayDuration.setText("" + text);
    }


    private void recycleView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        listOfEvents = new ArrayList<EventData>();
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdaterDayDetails(listOfEvents);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        startMainActivity();
        //  super.onBackPressed();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}
