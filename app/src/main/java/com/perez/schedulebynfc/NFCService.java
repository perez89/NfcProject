package com.perez.schedulebynfc;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Support.LocalCalendar;
import Support.LocalPreferences;
import Support.LocalTime;

import static com.perez.schedulebynfc.MainActivity.getDefaults;

/**
 * Created by User on 11/11/2016.
 */
public class NFCService extends Service {
    private static String LAST_TIME_NFC_DETECTED = "lastTimeNfcDetected";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();


        NfcWorker _nfcWorker = new NfcWorker(getApplicationContext());
        _nfcWorker.start();

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    public static class NfcWorker extends Thread {
        private Context _context;
        private static Lock _lock = new ReentrantLock();

        public NfcWorker(Context applicationContext) {
            _context = applicationContext;
        }

        @Override
        public void run() {
   //         System.out.println("Thread running");
            if(NfcWorker._lock.tryLock()){
               // System.out.println("Inside lock");
                try{
                    long currentMilleseconds = LocalTime.getCurrentMilliseconds();
                    if(verifyRangeBetweenNfcDetection(currentMilleseconds)) {
                        //System.out.println("registerNFC");
                        registerNFC(currentMilleseconds);

                        //notificationRegistered(true, currentMilleseconds);

                    }else{
                        //notificationNotRegistered();
                    }

                }catch (Exception e){
                    System.out.println(e.getMessage());

                }finally {
                 //   System.out.println("finally unlock");
                    NfcWorker._lock.unlock();

                }
            }
        }

        private void notificationRegistered(boolean inOrOut, long currentMilleseconds) {
            String content = "";
            if(inOrOut){
                content = "Entrada";
            }else{
                content = "Saida";
            }
            content = content + " : " + currentMilleseconds;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(_context)
                            .setSmallIcon(R.drawable.icon_nfc)
                            .setContentTitle("NFC Schedule")
                            .setContentText(""+content);

            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) _context.getSystemService(_context.NOTIFICATION_SERVICE);

            mNotifyMgr.cancelAll();
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }

        private boolean verifyRangeBetweenNfcDetection(long currentMilleseconds) {
            String value = getDefaults(LAST_TIME_NFC_DETECTED, _context);
            long lastTimeNfcDetected=0;

            if(value != null  && !value.equals("")){
                lastTimeNfcDetected = Long.parseLong(value);
            }

            if(currentMilleseconds-lastTimeNfcDetected > 5000){
                MainActivity.setDefaults(LAST_TIME_NFC_DETECTED, ""+currentMilleseconds, _context);
                return true;
            }

            return false;
        }

        private void registerNFC(long currentMilleseconds) {
        //    System.out.println("NFCService - registerNFC");
            long idCalendar = checkLocalCalendar();
            System.out.println("registerNFC id= " + idCalendar);
            RegisterNfc.getInstance().newNfcDetected(_context, idCalendar, currentMilleseconds);
        }

        private long  checkLocalCalendar() {
          //  System.out.println("checkLocalCalendar");
            String value = LocalPreferences.getInstance().getPreference(LocalPreferences.ID_CALENDAR, _context);
            if(value==null)
                value="0";

            long calendarID = Long.parseLong(value);
            if (calendarID < 1)
                calendarID = LocalCalendar.createCalendar(_context);
        //    System.out.println("2 - calendarID= " + calendarID);
            return calendarID;
        }
    }

}

