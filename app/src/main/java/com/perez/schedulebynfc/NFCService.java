package com.perez.schedulebynfc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by User on 11/11/2016.
 */
public class NFCService extends Service {

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

    public class NfcWorker extends Thread {
        Context _context;
        public NfcWorker(Context applicationContext) {
            _context = applicationContext;
        }

        @Override
        public void run() {
        //inserir o novo evento ou fechar o evento
            System.out.println("NFCService - NfcWorker THREAD");
            long idCalendar = MainActivity.getIdCalendar();
            RegisterNfc.getInstance().newNfcDetected(_context, idCalendar);
        }
    }
}

