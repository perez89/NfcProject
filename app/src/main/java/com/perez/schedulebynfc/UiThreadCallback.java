package com.perez.schedulebynfc;
import android.os.Message;

/**
 * Created by User on 18/11/2016.
 */

public interface UiThreadCallback {
    void publishToUiThread(Message message);
}
