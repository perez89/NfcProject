package com.perez.schedulebynfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.text.DateFormatSymbols;

import Support.CurrentTimeShow;
import Support.LocalCalendar;
import Support.LocalPreferences;

public class MainActivity extends AppCompatActivity {

    MainShowFragment[] ArrayOfEvents;
    final String move_next = "next";
    final String move_previous = "previous";
    //NfcAdapter nfcAdapter;
    CurrentTimeShow currentTimeToShow;
    TextSwitcher tsSwitcher;
   // TextView tvCurrentDate;
    Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayOfEvents = new MainShowFragment[3];
        this.savedInstanceState = savedInstanceState;
        initialization();
        //test(savedInstanceState);
    }

    private void debug(MainShowFragment new_frag) {

        System.out.println("debug-1");
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            //MainShowFragment frag = ArrayOfEvents[1];
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            // Add the fragment to the 'fragment_container' FrameLayout
            String tag = getTag(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());

            if(new_frag!=null)
                fragmentTransaction.replace(R.id.fragment_container, new MainFragment(), tag).commit();
        }
        System.out.println("debug-2");
    }

    private String getTag(int month, int year) {
        return ("frag_tag_" + month + "_" + year);
    }

    void refreshFragment(String move) {
        System.out.println("refreshFragment");
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment frag_new = ArrayOfEvents[1];
        String tag;
        if (move.equals(move_previous)) {
            tag = getTag(currentTimeToShow.getMonth_next(), currentTimeToShow.getYear_next());
            Fragment frag = fragmentManager.findFragmentByTag(tag);
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right).remove(frag);

           // MainShowFragment frag_new = ArrayOfEvents[1];
            tag = getTag(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right).add(R.id.fragment_container, frag_new, tag).commit();
            changeTextViewMonth("" + (new DateFormatSymbols().getMonths()[currentTimeToShow.getMonth_CurrentView()]), "" + currentTimeToShow.getYear_CurrentView(), 'l');
        }
        if (move.equals(move_next)) {
            tag = getTag(currentTimeToShow.getMonth_previous(), currentTimeToShow.getYear_previous());
            Fragment frag = fragmentManager.findFragmentByTag(tag);
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left).remove(frag);

            //MainShowFragment frag_new = ArrayOfEvents[1];
            tag = getTag(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView());
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).add(R.id.fragment_container, frag_new, tag).commit();
            changeTextViewMonth("" + (new DateFormatSymbols().getMonths()[currentTimeToShow.getMonth_CurrentView()]), "" + currentTimeToShow.getYear_CurrentView(), 'd');
        }

        System.out.println("refreshFragment-2");
    }

    private void initialization() {
        System.out.println("initialization");
        checkLocalCalendar();
        currentTimeToShow = CurrentTimeShow.getInstance();
        loadFirstTime();
        buttons();
        textViews();
        verifyNfc();
    }

    private void textViews() {
        System.out.println("textViews");
        tsSwitcher = (TextSwitcher) findViewById(R.id.tsSwitcher);
       // tvCurrentDate = (TextView) findViewById(R.id.tvCurrentDate);
        changeTextViewMonth("" + (new DateFormatSymbols().getMonths()[currentTimeToShow.getMonth_CurrentView()]), "" + currentTimeToShow.getYear_CurrentView(), 'n');

    }

    private void changeTextViewMonth(final String month, final String year, char _dir) {
        System.out.println("changeTextViewMonth");

        System.out.println("current = " + month + " year=" + year);
        Animation in;
        Animation out;
        switch(_dir){
            case 'd':
                in = AnimationUtils.loadAnimation(this,R.anim.slide_in_right);
                out = AnimationUtils.loadAnimation(this,R.anim.slide_out_left);
                tsSwitcher.setInAnimation(in);
                tsSwitcher.setOutAnimation(out);
                break;
            case 'l':
                in = AnimationUtils.loadAnimation(this,R.anim.slide_in_left);
                out = AnimationUtils.loadAnimation(this,R.anim.slide_out_right);
                tsSwitcher.setInAnimation(in);
                tsSwitcher.setOutAnimation(out);
                break;
            case 'n': ;
                break;

        }
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
       // tvCurrentDate.setText(month + "  " + year);
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
                removeFrag("last");
                refreshFragment(move_previous);
                new LoadFragment().execute(currentTimeToShow.getMonth_previous(), currentTimeToShow.getYear_previous(), 0);

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

                removeFrag("first");
                refreshFragment(move_next);
                new LoadFragment().execute(currentTimeToShow.getMonth_next(), currentTimeToShow.getYear_next(), 2);
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

    private void loadFirstTime() {
        new LoadFragment().execute(currentTimeToShow.getMonth_CurrentView(), currentTimeToShow.getYear_CurrentView(), 1);
        new LoadFragment().execute(currentTimeToShow.getMonth_previous(), currentTimeToShow.getYear_previous(), 0);
        new LoadFragment().execute(currentTimeToShow.getMonth_next(), currentTimeToShow.getYear_next(), 2);
    }


    public void addFragment(int pos, MainShowFragment frag) {
        ArrayOfEvents[pos] = frag;
    }

    private void removeFrag(String str) {
        System.out.println("removeFrag");
        if (str.equals("first")) {

            // ArrayOfEvents[0]=null;
            ArrayOfEvents[0] = ArrayOfEvents[1];
            ArrayOfEvents[1] = ArrayOfEvents[2];
            ArrayOfEvents[2] = null;

        }

        if (str.equals("last")) {
            ArrayOfEvents[2] = ArrayOfEvents[1];
            ArrayOfEvents[1] = ArrayOfEvents[0];
            ArrayOfEvents[0] = null;
        }
    }


    @Override
    protected void onNewIntent(Intent newIntent) {
        System.out.println("onNewIntent");
      //  super.onNewIntent(newIntent);

      //  if (newIntent.hasExtra(NfcAdapter.EXTRA_TAG))
        //    Toast.makeText(this, "Nfc intent received", Toast.LENGTH_SHORT);
    }

    private void enableForgroundDispatchSystem() {
        Intent newIntent = new Intent(this, MainActivity.class);
        newIntent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
        IntentFilter[] intentFilter = new IntentFilter[]{};
     //   if (nfcAdapter != null)
       //     nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilter, null);
    }

    private void disableForgroundDispatchSystem() {
     //   if (nfcAdapter != null)
       //     nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onResume() {
        System.out.println("onResume");
        //enableForgroundDispatchSystem();
        super.onResume();
        checkLocalCalendar();
    }

    @Override
    protected void onPause() {
        System.out.println("onPause");
        //disableForgroundDispatchSystem();
        super.onPause();
    }

    private void checkLocalCalendar() {
        System.out.println("checkLocalCalendar");
        String value = LocalPreferences.getInstance().getPreference(LocalPreferences.ID_CALENDAR, this);
        if(value==null)
            value="0";
        long calendarID = Long.parseLong(value);
        System.out.println("1 - calendarID= " + calendarID);

        if (calendarID < 1)
            calendarID = LocalCalendar.createCalendar(this);
        System.out.println("2 - calendarID= " + calendarID);

    }

    public static void setDefaults(String key, String value, Context context) {
        LocalPreferences tmp = LocalPreferences.getInstance();
        tmp.setPreference(key, value, context);
    }

    public static String getDefaults(String key, Context context) {
        LocalPreferences tmp = LocalPreferences.getInstance();
        return tmp.getPreference(key, context);
    }

    public class LoadFragment extends AsyncTask<Integer, Void, MainShowFragment> {
        int pos;

        @Override
        protected MainShowFragment doInBackground(Integer... integers) {
            System.out.println("LoadFragment= "+ pos);
            pos = integers[2];
            MainShowFragment frag = MainShowFragment.newInstance(integers[1],integers[0]);
            Bundle bundle;
            bundle = new Bundle();
            bundle.putInt("year_CurrentView", integers[1]);
            bundle.putInt("month_CurrentView", integers[0]);
            frag.setArguments(bundle);
            return frag;

        }

        protected void onPostExecute(MainShowFragment result) {
            addFragment(pos, result);
            if(pos==1){
                debug(result);
            }
        }

    }


}

