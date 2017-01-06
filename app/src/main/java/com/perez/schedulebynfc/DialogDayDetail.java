package com.perez.schedulebynfc;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import Support.EventClass;
import Support.EventData;
import Support.LocalEventService;
import Support.LocalTime;
import Support.MyAdaterDayDetails;

import static com.perez.schedulebynfc.R.id.rvListDays;

/**
 * Created by User on 29/12/2016.
 */

public class DialogDayDetail extends DialogFragment {
    TextView tvEmptyRv;
    TextView tvDayDate;
    List<EventData> listOfEvents;
    private RecyclerView mRecyclerView;
    private MyAdaterDayDetails mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MyHandler handler = new MyHandler();
    private MyHandlerThread myHandlerThread;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long time = 0;

        if (getArguments() != null) {
            time = getArguments().getLong("timeDayDetails");

        }



        Context context = getActivity();
        LayoutInflater linf = LayoutInflater.from(context);
        final View inflator = linf.inflate(R.layout.dialog_day, null);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);

        alert.setTitle(R.string.dialogDayDetailTitle);
        alert.setView(inflator);
        mRecyclerView = (RecyclerView) inflator.findViewById(rvListDays);
        tvEmptyRv = (TextView) inflator.findViewById(R.id.tvEmptyRv);
        tvDayDate = (TextView) inflator.findViewById(R.id.tvDialogDate);

        //setDate(ails);

        alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // String s1=et1.getText().toString();
                // String s2=et2.getText().toString();
                dialog.cancel();
                // //do operations using s1 and s2 here...
            }
        });

        if (time > 0) {
            recycleView();
            setHandlerAndThread(time);
        } else {
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyRv.setVisibility(View.VISIBLE);
        }


        return alert.create();

    }

    private void setDate(String text) {
        tvDayDate.setText(text);
    }


    private void setHandlerAndThread(long time) {
        myHandlerThread = new MyHandlerThread("myHandlerThread");
        myHandlerThread.start();
        myHandlerThread.prepareHandler();
        try {
            myHandlerThread.postTask(new CustomRunnableGetDate(getActivity(), handler, time));
            myHandlerThread.postTask(new CustomRunnableGetEvents(getActivity(), handler, time));

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
            int day = LocalTime.getDay(time);
            int year = LocalTime.getYear(time);
            int tmpMonth = LocalTime.getMonth(time);
            String month = LocalTime.getMonthStringFormat(tmpMonth);

            String textDisplay = day + " " + month + " " + year;

            Message message = handler.obtainMessage();
            Bundle b = new Bundle();
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

            List<EventClass> tmpListOfEvents = lEventService.getEventsForDay(time, (time + 86400000));
            //  listOfEvents = tmpListOfEvents;

            if (tmpListOfEvents.size() > 0) {
                System.out.println("size of events= " +tmpListOfEvents.size());
                for (EventClass event :
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

                    setDate(textDisplay);
                    break;
            }
        }
    }

    private void addEventToList(EventData event) {
        listOfEvents.add(event);
        mAdapter.notifyData(listOfEvents);
    }


    private void recycleView() {
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        listOfEvents = new ArrayList<EventData>();
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdaterDayDetails(listOfEvents);
        mRecyclerView.setAdapter(mAdapter);
    }
}
