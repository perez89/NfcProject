package Support;

import java.util.Calendar;

/**
 * Created by User on 18/10/2016.
 */

public class EventData {
    long startTime;
    long endTime;
    long duration;

    int day;
    int weekOfMonth;

    int startHour;
    int startMinute;

    int endHour;
    int endMinute;

    boolean closed;

    public EventData(long startTimeL, long endTimeL) {
     //   System.out.println("startTimeL= " + startTimeL +" endTimeL= " + endTimeL);
        startTime = startTimeL;
        endTime = endTimeL;
        splitDate();
    }

    private void splitDate() {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startTime);  //here your time in miliseconds
        day = cl.get(Calendar.DAY_OF_MONTH);
        weekOfMonth = cl.get(Calendar.WEEK_OF_MONTH);

        startHour = cl.get(Calendar.HOUR_OF_DAY);
        startMinute = cl.get(Calendar.MINUTE);

        cl.clear();
        cl = Calendar.getInstance();
        cl.setTimeInMillis(endTime);
        endHour = cl.get(Calendar.HOUR_OF_DAY);
        endMinute = cl.get(Calendar.MINUTE);

        setDuration();
    }

    public long getDuration() {
        return duration;
    }

    public long getStartTime(){
        return startTime;
    }
    private long setDuration() {
        if (endTime > startTime) {
            closed = true;
            duration = endTime - startTime;
            return endTime - startTime;
        } else {
            closed = false;
            return 0;
        }
    }

    public int getDay() {
        return day;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public boolean isClosed() {
        return closed;
    }
}
