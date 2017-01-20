package Support;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
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

    String timeZone;
    String title;
    String description;

    public EventData(long startTimeL, long endTimeL, String timeZone, String title, String description) {
        //   System.out.println("startTimeL= " + startTimeL +" endTimeL= " + endTimeL);
        this.startTime = startTimeL;
        this.endTime = endTimeL;

        this.timeZone = timeZone;
        this.title = title;
        this.description= description;
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

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
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

    public JSONObject toJson() throws JSONException {

        JSONObject _eventObject = new JSONObject();

        _eventObject.put("title", this.title);
        _eventObject.put("Description", this.description);
        _eventObject.put("startTime", this.startTime);
        _eventObject.put("endTime", this.endTime);
        _eventObject.put("timeZone", this.timeZone);
        _eventObject.put("created", "Schedule by NFC");
        return _eventObject;
    }

    public JSONObject toJsonToggl() throws JSONException {
        JSONObject _eventObject = new JSONObject();
        _eventObject.put("pid", 29485539);
        System.out.println(LocalTime.getDateFormatIso(this.startTime));
        System.out.println(LocalTime.getDateFormatIso(this.endTime));
        _eventObject.put("start", LocalTime.getDateFormatIso(this.startTime));
        _eventObject.put("description", ""+this.description);
        String mStringArray[] = { "billed" };
        JSONArray mJSONArray = new JSONArray(Arrays.asList(mStringArray));
        _eventObject.put("tags",mJSONArray );
        if(this.startTime>=this.endTime){
            _eventObject.put("duration", -100);
        }else{
            int time = (int)((this.endTime-this.startTime)/1000);
            _eventObject.put("duration", time);
            System.out.println("time=" + time);
        }

        _eventObject.put("created_with", "nfc");
        return _eventObject;
    }
}
