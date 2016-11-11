package com.perez.schedulebynfc;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by User on 10/11/2016.
 */

public class NfcActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nfc_activity);
        Toast.makeText(this, "NfcActivity",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent newIntent) {

          super.onNewIntent(newIntent);

          if (newIntent.hasExtra(NfcAdapter.EXTRA_TAG)){
              Toast.makeText(this, "Nfc intent received", Toast.LENGTH_SHORT);
              System.out.println("Nfc intent received");
          }

    }
}
