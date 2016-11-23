package com.perez.schedulebynfc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by User on 10/11/2016.
 */

    public class NfcActivity extends Activity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //REMOVI O getBaseContext() - se algo correr mal voltar a substituir
            startService(new Intent(getApplicationContext(), NFCService.class));
            //setContentView(R.layout.activity_nfc_activity);
            //Toast.makeText(this, "NfcActivity",                Toast.LENGTH_LONG).show();
            finish();
        }
    }

