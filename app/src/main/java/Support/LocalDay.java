package Support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 19/10/2016.
 */

public class LocalDay {
    private List<LocalEvent> listOfEvents;

    int day;
    int month;
    int year;
    long totalDuration;
    String durationString;

    public LocalDay(int day, int month, int year){


        listOfEvents = new ArrayList<LocalEvent>();
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }

    public void addAllEvents(List<LocalEvent> listOfEvents){

        this.listOfEvents = listOfEvents;
        setDuration();
    }

    public List<LocalEvent> getListOfEvents() {
        return listOfEvents;
    }

    public int getNumberOfEvents(){
        return listOfEvents.size();
    }

    public void setDuration(){
        long totalDuration = 0;

        for(LocalEvent event: listOfEvents
                ) {
            totalDuration = totalDuration + event.getData().getDuration();

        }

        this.totalDuration = totalDuration;

        int minutes = (int) ((totalDuration / (1000*60)) % 60);
        int hours   = (int) ((totalDuration / (1000*60*60)) % 24);

        if(hours>0 || minutes>0){
             if(minutes<10)
                 durationString = hours +".0"+minutes;
            else
                 durationString = hours +"."+minutes;

        }else{
            durationString = "-";
        }


    }

    public long getDayTotalDuration(){
        return totalDuration;
    }

    public String toString(){
        return ""+durationString;
    }
}
