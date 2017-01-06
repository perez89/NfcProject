package Support;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * Created by User on 05/01/2017.
 */

public class MyCalendarObserver extends ContentObserver {
    Handler handler;
    public MyCalendarObserver(Handler handler) {
        super(handler);
        this.handler=handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Message message = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putBoolean("CalendarChange", true); //Long
        message.setData(b);
        message.what = 3;
        handler.sendMessage(message);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }
}
