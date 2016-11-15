package com.perez.schedulebynfc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
            System.out.println("Thread running");
            if(NfcWorker._lock.tryLock()){
                System.out.println("Inside lock");
                try{
                    long currentMilleseconds = LocalTime.getCurrentMilliseconds();
                    if(verifyRangeBetweenNfcDetection(currentMilleseconds)) {
                        System.out.println("registerNFC");
                        registerNFC(currentMilleseconds);
                    }

                }catch (Exception e){
                    System.out.println(e.getMessage());

                }finally {
                    System.out.println("finally unlock");
                    NfcWorker._lock.unlock();

                }
            }
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
            System.out.println("NFCService - registerNFC");
            long idCalendar = MainActivity.getIdCalendar();
            RegisterNfc.getInstance().newNfcDetected(_context, idCalendar, currentMilleseconds);
        }
    }
}

