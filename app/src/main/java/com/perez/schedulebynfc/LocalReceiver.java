package com.perez.schedulebynfc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.widget.Toast;

import static android.nfc.NfcAdapter.ACTION_NDEF_DISCOVERED;

/**
 * Created by User on 09/11/2016.
 */

public class LocalReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println(">>>>>> NFC DISCOVERED");

      //  CharSequence text = context.getResources().getString(R.string.unlock_message);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "NFC DISCOVERED", duration);
        toast.show();

        if (intent.getAction().equals(ACTION_NDEF_DISCOVERED)) {


                Toast nfc_toast = Toast.makeText(context, "ACTION_NDEF_DISCOVERED", duration);
            System.out.println(">>>>>> ACTION_NDEF_DISCOVERED");
            nfc_toast.show();

        }else if ( intent.getAction().equals(NfcAdapter.ACTION_TAG_DISCOVERED)){

                Toast nfc_toast = Toast.makeText(context, "ACTION_TAG_DISCOVERED", duration);
            System.out.println(">>>>>> ACTION_TAG_DISCOVERED");
            nfc_toast.show();

        }
    }
}