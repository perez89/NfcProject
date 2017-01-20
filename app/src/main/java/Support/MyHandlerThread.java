package Support;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by User on 20/01/2017.
 */

public class MyHandlerThread extends HandlerThread {

    private Handler handler;

    public MyHandlerThread(String name) {
        super(name);
    }

    public void postTask(Runnable task) {
        handler.post(task);
    }

    public void prepareHandler() {
        handler = new Handler(getLooper());
    }
}