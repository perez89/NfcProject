package com.perez.schedulebynfc;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Support.CurrentTimeShow;
import Support.LocalCalendar;
import Support.LocalEvent;
import Support.LocalEventService;
import Support.LocalJson;
import Support.LocalPreferences;
import Support.LocalTime;
import Support.MyHandlerThread;

import static Support.LocalTime.getCurrentMilliseconds;

public class MainActivity extends AppCompatActivity implements BottomFragment.RefreshTime {

    //  MainFragment[] ArrayOfEvents;
    final String MOVE_NEXT = "next";
    final String MOVE_PREVIOUS = "previous";
    //NfcAdapter nfcAdapter;
    CurrentTimeShow currentTimeToShow;
    TextSwitcher tsSwitcher;
    // TextView tvCurrentDate;
    private int _day;
    private int _week;
    private int _weekOfmonth;
    private int _month;
    private int _year;

    private String currentDayTime = "";
    private String currentWeekTime = "";
    private String currentMonthTime = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        setContentView(R.layout.activity_main);
        // ArrayOfEvents = new MainFragment[3];
        initialization(savedInstanceState);
        //test(savedInstanceState);
    }


    void refreshFragment(String move) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Fragment frag_new = ArrayOfEvents[1];
        String tag;
        MainFragment new_frag = MainFragment.newInstance(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
        String tag_new = getTag(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());

        if (move.equals(MOVE_PREVIOUS)) {
            tag = getTag(currentTimeToShow.getMonth_next(), currentTimeToShow.getYear_next());
            Fragment frag = fragmentManager.findFragmentByTag(tag);
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right).remove(frag);

            // MainShowFragment frag_new = ArrayOfEvents[1];

            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).add(R.id.fragment_container, new_frag, tag_new).commit();
            changeTextViewMonth("" + (new DateFormatSymbols().getMonths()[currentTimeToShow.getMonth_CurrentView()]), "" + currentTimeToShow.getYear_CurrentView(), 'l');
        }
        if (move.equals(MOVE_NEXT)) {
            tag = getTag(currentTimeToShow.getMonth_previous(), currentTimeToShow.getYear_previous());
            Fragment frag = fragmentManager.findFragmentByTag(tag);
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left).remove(frag);

            //MainShowFragment frag_new = ArrayOfEvents[1];
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).add(R.id.fragment_container, new_frag, tag_new).commit();
            changeTextViewMonth("" + (new DateFormatSymbols().getMonths()[currentTimeToShow.getMonth_CurrentView()]), "" + currentTimeToShow.getYear_CurrentView(), 'd');
        }
    }

    private String getTag(int month, int year) {
        return ("frag_tag_" + month + "_" + year);
    }

    private void initialization(Bundle savedInstanceState) {
        System.out.println("initialization");
        //LocalCalendar.getCalendars(this);
        checkLocalCalendar();
        //deleteCalendarUnderSameAccount(this);
        // LocalCalendar.getCalendars(this);
        currentTimeToShow = CurrentTimeShow.getInstance();

        loadFragment();
        laodBottomFrag();
        //loadFirstTime();
        buttons();
        textViews();
        verifyNfc();
    }


    public void laodBottomFrag() {
        if (findViewById(R.id.fragment_container_bottom) != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            //Fragment frag_new = ArrayOfEvents[1];

            String tag_new = "TAG_BOTTOM";
            Fragment fragment = fragmentManager.findFragmentByTag(tag_new);
            BottomFragment new_frag = BottomFragment.newInstance();
            if (fragment == null) {

                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom).add(R.id.fragment_container_bottom, new_frag, tag_new).commit();


            } else {

                fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom).replace(R.id.fragment_container_bottom, new_frag, tag_new).commit();

            }
        }
    }

    private void loadFragment() {
        if (findViewById(R.id.fragment_container) != null) {

            //MainShowFragment frag = ArrayOfEvents[1];
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the fragment to the 'fragment_container' FrameLayout
            MainFragment new_frag = MainFragment.newInstance(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
            String tag = getTag(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
            if (new_frag != null)
                fragmentTransaction.replace(R.id.fragment_container, new_frag, tag).commit();
        }
        if (findViewById(R.id.fragment_container_bottom) != null) {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the fragment to the 'fragment_container' FrameLayout
            MainFragment new_frag = MainFragment.newInstance(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
            String tag = getTag(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
            if (new_frag != null)
                fragmentTransaction.replace(R.id.fragment_container, new_frag, tag).commit();
        }
    }

    private void textViews() {
        System.out.println("textViews");
        tsSwitcher = (TextSwitcher) findViewById(R.id.tsSwitcher);
        // tvCurrentDate = (TextView) findViewById(R.id.tvCurrentDate);
        changeTextViewMonth("" + (new DateFormatSymbols().getMonths()[currentTimeToShow.getMonth_CurrentView()]), "" + currentTimeToShow.getYear_CurrentView(), 'n');

    }

    private void changeTextViewMonth(final String month, final String year, char _dir) {
        Animation in;
        Animation out;

      /*  switch(_dir) {
            case 'd':
                out = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
                tsSwitcher.setOutAnimation(out);
                tsSwitcher.animate();
                break;
            case 'l':
                out = AnimationUtils.loadAnimation(this,R.anim.slide_out_right);
                tsSwitcher.setOutAnimation(out);
                tsSwitcher.animate();
                break;
        }

        switch(_dir) {
            case 'd':
                in = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
                tsSwitcher.setInAnimation(in);
                tsSwitcher.animate();
                break;
            case 'l':
                in = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
                tsSwitcher.setInAnimation(in);
                tsSwitcher.animate();
                break;
        }*/

        /*
        System.out.println("changeTextViewMonth");

        System.out.println("current = " + month_frag_show + " year=" + year);

        switch(_dir){
            case 'd':
                in = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
                out = AnimationUtils.loadAnimation(this,R.anim.slide_out_right);
                tsSwitcher.setInAnimation(in);

                tsSwitcher.setOutAnimation(out);
                break;
            case 'l':

                in = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
                out = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);

                tsSwitcher.setInAnimation(in);
                tsSwitcher.setOutAnimation(out);
                break;
            case 'n': ;
                break;

        }*/
        tsSwitcher.removeAllViews();

        tsSwitcher.setFactory(new ViewSwitcher.ViewFactory() {

            public View makeView() {
                // TODO Auto-generated method stub
                // create new textView and set the properties like clolr, size etc
                TextView myText = new TextView(MainActivity.this);
                myText.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);


                return myText;
            }
        });
        tsSwitcher.setText(month + "  " + year);
        // tvCurrentDate.setText(month_frag_show + "  " + year);
    }

    private void buttons() {
        System.out.println("buttons");
        final ImageButton btPrevious = (ImageButton) findViewById(R.id.ibPreviousMonth);
        final ImageButton btNext = (ImageButton) findViewById(R.id.ibNextMonth);

        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeToShow.previousMonth();
                System.out.println("move back<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

                refreshFragment(MOVE_PREVIOUS);

                btPrevious.setEnabled(false);
                btNext.setEnabled(false);

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btNext.setEnabled(true);
                                btPrevious.setEnabled(true);

                            }
                        });
                    }
                }).start();
            }
        });


        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentTimeToShow.nextMonth();
                System.out.println("move next>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

                // removeFrag("first");
                refreshFragment(MOVE_NEXT);
                //new LoadFragment().execute(currentTimeToShow.getMonth_next(), currentTimeToShow.getYear_next(), 2);

                btNext.setEnabled(false);
                btPrevious.setEnabled(false);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(700);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        MainActivity.this.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                btNext.setEnabled(true);
                                btPrevious.setEnabled(true);

                            }
                        });
                    }
                }).start();
            }
        });

    }

    private void verifyNfc() {
        //nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // if (nfcAdapter != null && nfcAdapter.isEnabled()) {

        //} else {

        //  finish();
        //}
    }

   /* private void loadFirstTime() {
        new LoadFragment().execute(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView(), 1);
        new LoadFragment().execute(currentTimeToShow.getMonth_previous(), currentTimeToShow.getYear_previous(), 0);
        new LoadFragment().execute(currentTimeToShow.getMonth_next(), currentTimeToShow.getYear_next(), 2);
    }*/


    //public void addFragment(int pos, MainFragment frag) {
    // ArrayOfEvents[pos] = frag;
    //}

   /* private void removeFrag(String str) {
        System.out.println("removeFrag");
        if (str.equals("first")) {

            // ArrayOfEvents[0]=null;
     /*       ArrayOfEvents[0] = ArrayOfEvents[1];
            ArrayOfEvents[1] = ArrayOfEvents[2];
            ArrayOfEvents[2] = null;
               */
    // }

    /*    if (str.equals("last")) {
          /*  ArrayOfEvents[2] = ArrayOfEvents[1];
            ArrayOfEvents[1] = ArrayOfEvents[0];
            ArrayOfEvents[0] = null; */
     /*   }
    }
*/
    @Override
    protected void onResume() {
        System.out.println("onResume");
        //enableForgroundDispatchSystem();
        super.onResume();
        getCurrentData();
        updateWidget();
        //checkLocalCalendar();
    }

    public void updateWidget() {
        new MyWidgetProvider.WidgetUpdate().Update(new WeakReference<Context>(this));
    }

    private void getCurrentData() {
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        //disableForgroundDispatchSystem();
        super.onPause();
    }

    private void checkLocalCalendar() {
        LocalPreferences.getInstance().setPreference(LocalPreferences.ID_CALENDAR, "" + 8, this);
        if (!(LocalCalendar.getIdCalendar(getApplicationContext()) > 0)) {
            Context context = getApplicationContext();
            CharSequence text = "There is a problem with the calendar!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }


    }

    public static void setDefaults(String key, String value, Context context) {
        LocalPreferences tmp = LocalPreferences.getInstance();
        tmp.setPreference(key, value, context);
    }

    public static String getDefaults(String key, Context context) {
        LocalPreferences tmp = LocalPreferences.getInstance();
        return tmp.getIdCalendarPreference(key, context);
    }

    /* public class LoadFragment extends AsyncTask<Integer, Void, MainFragment> {
         int pos;

         @Override
         protected MainFragment doInBackground(Integer... integers) {
             System.out.println("LoadFragment= "+ pos);
             pos = integers[2];
             MainFragment frag = MainFragment.newInstance(integers[1],integers[0]);
             Bundle bundle;
             bundle = new Bundle();
             bundle.putInt("year_CurrentView", integers[1]);
             bundle.putInt("month_CurrentView", integers[0]);
             frag.setArguments(bundle);
             return frag;

         }

         protected void onPostExecute(MainFragment result) {
             addFragment(pos, result);
             if(pos==1){
                 debug(result);
             }
         }

     }*/
    public int get_day() {
        return _day;
    }

    public int get_week() {
        return _week;
    }

    public int get_weekOfmonth() {
        return _weekOfmonth;
    }

    public int get_month() {
        return _month;
    }

    public int get_year() {
        return _year;
    }

    @Override
    protected void onStop() {
        System.out.println("onStop");
        finish();
        super.onStop();
    }

    public Bundle getCurrentTimes() {
        Bundle bundle = new Bundle();
        bundle.putString("dayTime", currentDayTime);
        bundle.putString("weekTime", currentWeekTime);
        bundle.putString("monthTime", currentMonthTime);
        return bundle;
    }

    @Override
    public void sendRefreshTime(String dayTime, String weekTime, String monthTime) {
        //System.out.println("refresh activity-1= " + time);

        int currentShowMonth = currentTimeToShow.getMonth_CurrentView();
        int currentShowYear = currentTimeToShow.getYear_CurrentView();
        long milli = getCurrentMilliseconds();
        int year = LocalTime.getYear(milli);
        int month = LocalTime.getMonth(milli);

        currentDayTime = dayTime;
        currentWeekTime = weekTime;
        currentMonthTime = monthTime;
        if (currentShowMonth == month && currentShowYear == year) {
            // System.out.println("refresh activity-2= " + time);
            String tag = getTag(currentShowMonth, currentShowYear);
            if (tag != null) {
                //  System.out.println("refresh activity-3= " + time);
                MainFragment frag = (MainFragment) getSupportFragmentManager().findFragmentByTag(tag);
                if (frag != null)

                    frag.refreshTime(dayTime, weekTime, monthTime);
            }
        }
    }

    @Override
    public MenuInflater getMenuInflater() {
        return super.getMenuInflater();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.share:
                setHandlerAndThread();
                return true;
            case R.id.settings:
                settings();
                return true;
            case R.id.help:
                help();
                return true;
            case R.id.aboutUs:
                aboutUs();
                return true;
            case R.id.reportError:
                reportError();
                return true;
            case R.id.generatepdf:
                generatePdf();
                return true;
            case R.id.simulateNfc:
                simulateNfc();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void generatePdf() {
    }

    private void simulateNfc() {
    }

    private void reportError() {
    }

    private void aboutUs() {
    }

    private void share()  {
    }

    private class CustomRunnable implements Runnable {
        private WeakReference<Context> mWeakRefContext;
        Handler handler;


        public CustomRunnable(Context context, Handler h) throws ParseException {
            handler = h;
            this.mWeakRefContext = new WeakReference<Context>(context);
        }

        @Override
        public void run() {

            System.out.println("share pressed");
            LocalTime.DateString dataStringStart = new LocalTime.DateString(currentTimeToShow.getYear_CurrentView() + "", currentTimeToShow.getMonth_CurrentView() + "", "1", "", "", "");
            Calendar cal = Calendar.getInstance();

            LocalTime.DateString dataStringEnd = new LocalTime.DateString(currentTimeToShow.getYear_CurrentView() + "", currentTimeToShow.getMonth_CurrentView() + "", "30", "", "", "");
            LocalEventService lEventService = new LocalEventService(mWeakRefContext);
            List<LocalEvent> listOfEvents = lEventService.getEventsForDay(0, LocalTime.getCurrentMilliseconds());
            LocalPreferences tmp = LocalPreferences.getInstance();

            for(LocalEvent localEvent : listOfEvents){
                String key =null;
                if(localEvent.isClose() && key==null){
                    System.out.println("dia= " + LocalTime.getDay(localEvent.getData().getStartTime()) + " mes= "+LocalTime.getMonth(localEvent.getData().getStartTime()) + " ano= "+ LocalTime.getYear(localEvent.getData().getStartTime()));

                    LocalJson _json = new LocalJson(localEvent);

                    JsonObjectRequest jsObjRequest = null;
                    try {
                        jsObjRequest = new JsonObjectRequest
                                (Request.Method.POST, "https://www.toggl.com/api/v8/time_entries", _json.generateJson(), new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {

                                        System.out.println("JSONObject response= " + response.toString());
                                        // tmp.setPreference("", "true", getApplicationContext());
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // TODO Auto-generated method stub
                                        System.out.println("ERROR response= " + error.toString());

                                    }
                                }) {
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> headers = new HashMap<String, String>();
                                //  params.put("Content-Type", "application/json");
                                headers.put("Content-Type", "application/json; charset=utf-8");

                                // headers.put("api_token", "acbb58a1e09ba85343a6c7720119f955");
                                String creds = String.format("%s:%s", "lplperez@hotmail.com", "wherchojo89");

                                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                                headers.put("Authorization", auth);
                                return headers;
                            }


                        };
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(mWeakRefContext.get()!=null){
                        RequestQueue queue = Volley.newRequestQueue(mWeakRefContext.get());
                        queue.add(jsObjRequest);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Message message = handler.obtainMessage();
         //   Bundle b = new Bundle();

            //b.putInt("dayPosition", i); // for example

          //  message.setData(b);
            message.obj = mWeakRefContext;
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    private MyHandler handler = new MyHandler();
    private MyHandlerThread myHandlerThread;

    private void setHandlerAndThread() {
        myHandlerThread = new MyHandlerThread("myHandlerThread");
        myHandlerThread.start();
        myHandlerThread.prepareHandler();
        try {
            myHandlerThread.postTask(new CustomRunnable(this, handler));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    class MyHandler extends Handler {

        // simply show a toast message
        @Override
        public void handleMessage(Message msg) {
            WeakReference<Context> mWeakRefContext = null;
            if (msg.obj != null)
                mWeakRefContext = (WeakReference<Context>) msg.obj;

            super.handleMessage(msg);
            switch (msg.what) {
                //handle result from handler
                case 0:
                    System.out.println("FIM DE ENVIO");
                    break;
            }
        }
    }

    private void help() {

    }

    private void settings() {


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, "https://www.toggl.com/api/v8/me", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("JSONObject response= " + response.toString());


                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        System.out.println("ERROR response= " + error.toString());

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //  params.put("Content-Type", "application/json");
                headers.put("Content-Type", "application/json; charset=utf-8");
             //   String creds = String.format("%s:%s", "acbb58a1e09ba85343a6c7720119f955", "api_token");
                String creds = String.format("%s:%s", "lplperez@hotmail.com", "wherchojo89");
                String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
                headers.put("Authorization", auth);
                return headers;
            }


        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsObjRequest);
        //MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        // MySingleton.getInstance(this).addToRequestQueue(request);

    }


}

