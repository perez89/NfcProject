package com.perez.schedulebynfc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by User on 11/11/2016.
 */
public class NFCService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Worker _worker = new Worker();
        _worker.start();
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class Worker extends Thread {

        @Override
        public void run() {
//inserir o novo evento ou fechar o evento

        }

    }
}
