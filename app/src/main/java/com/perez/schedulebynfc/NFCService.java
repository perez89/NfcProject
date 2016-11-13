package com.perez.schedulebynfc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by User on 11/11/2016.
 */
public class NFCService extends Service {

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service starting - onStartCommand ");
        Toast.makeText(this, "service starting - onStartCommand ", Toast.LENGTH_SHORT).show();
        Worker _worker = new Worker(startId);
        _worker.start();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    class Worker extends Thread {
        private int idService;
        public Worker(int startId) {
            idService = startId;

        }

        @Override
        public void run() {
            //inserir o novo evento ou fechar o evento
            long idCalendar = MainActivity.getIdCalendar();
            RegisterNfc.getInstance().newNfcDetected(getApplicationContext(), idCalendar);
            stopSelf(idService);
        }

    }
}
