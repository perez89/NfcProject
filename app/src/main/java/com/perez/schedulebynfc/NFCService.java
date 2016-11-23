package com.perez.schedulebynfc;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Support.LocalCalendar;
import Support.LocalTime;

import static Support.LocalTime.getMilliSeconds;
import static Support.LocalTime.getSeconds;
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

    private static class NfcWorker extends Thread {
        private WeakReference<Context> mWeakRefContext;
        private static Lock _lock = new ReentrantLock();

        public NfcWorker(Context applicationContext) {
            mWeakRefContext= new WeakReference<Context>(applicationContext);

        }

        @Override
        public void run() {
   //         System.out.println("Thread running");
            if(NfcWorker._lock.tryLock()){
               // System.out.println("Inside lock");
                try{
                    long currentMilleseconds = LocalTime.getCurrentMilliseconds();

                    //We only the certain Hour:Minute, we need to remove the seconds and milliseconds
                    currentMilleseconds = removeSecondsAndMilliseconds(currentMilleseconds);
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
            finishThread();
        }

        private void finishThread(){
            if(isAlive())
                interrupt();
        }

        private long removeSecondsAndMilliseconds(long currentMilliseconds) {
            long milliSeconds = getMilliSeconds(currentMilliseconds);
            milliSeconds = milliSeconds + ((getSeconds(currentMilliseconds))*1000);
            return currentMilliseconds-milliSeconds;
        }

        private void notificationRegistered(boolean inOrOut, long currentMilliseconds) {
            String content = "";
            if(inOrOut){
                content = "Entrada";
            }else{
                content = "Saida";
            }
            content = content + " : " + currentMilliseconds;
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(mWeakRefContext.get())
                            .setSmallIcon(R.drawable.icon_nfc)
                            .setContentTitle("NFC Schedule")
                            .setContentText(""+content);

            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) mWeakRefContext.get().getSystemService(mWeakRefContext.get().NOTIFICATION_SERVICE);

            mNotifyMgr.cancelAll();
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }

        private boolean verifyRangeBetweenNfcDetection(long currentMilleseconds) {
            String value = getDefaults(LAST_TIME_NFC_DETECTED, mWeakRefContext.get());
            long lastTimeNfcDetected=0;

            if(value != null  && !value.equals("")){
                lastTimeNfcDetected = Long.parseLong(value);
            }

            if(currentMilleseconds-lastTimeNfcDetected > 5000){
                MainActivity.setDefaults(LAST_TIME_NFC_DETECTED, ""+currentMilleseconds, mWeakRefContext.get());
                return true;
            }

            return false;
        }

        private void registerNFC(long currentMilleseconds) {
        //    System.out.println("NFCService - registerNFC");
            long idCalendar = LocalCalendar.getIdCalendar(mWeakRefContext.get());
            System.out.println("registerNFC id= " + idCalendar);
            RegisterNfc.getInstance().newNfcDetected(mWeakRefContext, idCalendar, currentMilleseconds);
        }
    }

}

