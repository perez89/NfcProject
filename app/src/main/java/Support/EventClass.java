package Support;

/**
 * Created by User on 10/10/2016.
 */

public class EventClass {
    private long eventID;
    private EventData lData;


    public EventClass(long eventID, long startTime, long endTime){
      //  System.out.println("EventClass ! startTimeL= " + startTime +" endTimeL= " + startTime);
        this.eventID = eventID;
        lData = new EventData(startTime, endTime);
    }

    public long getEventID() {
        return eventID;
    }

    public EventData getData() {
        return lData;
    }

    public boolean isClose() {
        return lData.isClosed();
    }

}