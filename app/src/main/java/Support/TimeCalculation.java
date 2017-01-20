package Support;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static Support.LocalTime.getCurrentMilliseconds;
import static Support.LocalTime.getWeekOfYear;

/**
 * Created by User on 11/01/2017.
 */

public  class TimeCalculation {
    private long weekTime = 0;
    private long dayTime = 0;
    private long monthTime = 0;
    private final long DAY_MILLISECONDS = 86400000;

    private WeakReference<Context> mWeakRefContext;
    private LocalEventService lEventService;

    int secs_real = 0;
    int week_real = 0;
    int year_real= 0;
    int month_real = 0;
    int secs= 0;
    int day_real= 0;
    boolean working = false;

    public TimeCalculation(WeakReference<Context> mWeakRefContext) {

        this.mWeakRefContext = mWeakRefContext;
        lEventService = new LocalEventService(this.mWeakRefContext);
        long milli = getCurrentMilliseconds();
        secs_real = LocalTime.getSeconds(milli);
        week_real = LocalTime.getWeekOfYear(milli);
        year_real = LocalTime.getYear(milli);

        month_real = LocalTime.getMonth(milli);
        day_real = LocalTime.getDay(milli);
        secs = LocalTime.getSeconds(milli);

        setDayTime();

        setWeekTime();

        setMonthTime();

        checkOpenEvent();
        }


    public long getDayTime(){
        return dayTime;
    }
    public long getWeekTime(){
        return weekTime;
    }
    public long getMonthTime(){
        return monthTime;
    }

    private void setDayTime() {
        long timeStartOfMonth = 0;
        long currentDayTime = 0;
        int month = month_real+1;
        LocalTime.DateString dataString = new LocalTime.DateString(year_real + "", month+"",  day_real+"", "", "", "");

        try {
            timeStartOfMonth = dataString.getMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeEnd = timeStartOfMonth + DAY_MILLISECONDS;
        List<LocalEvent> listOfEvents;
        //System.out.println("timeStartOfMonth= " + timeStartOfMonth + " timeEnd="+ timeEnd);
        listOfEvents = lEventService.getEventsForDay(timeStartOfMonth, timeEnd);

        currentDayTime =  getTotalDayTime(listOfEvents);

        dayTime =  currentDayTime;
    }

    private void setWeekTime() {
        long timeStart = 0, currentWeekTime=0;

        List<LocalEvent> listOfEvents;
        int month = month_real+1;
        LocalTime.DateString dataString = new LocalTime.DateString(year_real + "", month+"", day_real+"",  "", "", "");
        try {
            timeStart = dataString.getMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //timeStartOfMonth = timeStartOfMonth - DAY_MILLISECONDS;
        int cont = 1;
        do{
            int test_week = getWeekOfYear(timeStart - DAY_MILLISECONDS);
            if(test_week == week_real){
                cont++;
                timeStart = timeStart - DAY_MILLISECONDS;
            }
            else{
                break;
            }
        }while(true);
        timeStart = timeStart + DAY_MILLISECONDS;
        for(int d=0; d<cont ; d++){
            listOfEvents = lEventService.getEventsForDay(timeStart, timeStart+DAY_MILLISECONDS);
            currentWeekTime = currentWeekTime + getTotalDayTime2(listOfEvents);

            timeStart = timeStart + DAY_MILLISECONDS;
        }
        weekTime=currentWeekTime;
    }
    public boolean isWorking(){
        return working;
    }

    private void setMonthTime() {
        long timeStartOfMonth = 0;
        long currentMonthTime = 0;
        int month = month_real+1;
        LocalTime.DateString  dataString = new LocalTime.DateString(year_real + "", month + "", "", "", "", "");
        int numOfDays = LocalTime.getNumberDaysMonth(year_real, month_real);
        List<LocalEvent> listOfEvents = new ArrayList<LocalEvent>();

        try {
            timeStartOfMonth = dataString.getMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < day_real; i++) {
            long timeEnd = timeStartOfMonth + DAY_MILLISECONDS;
            listOfEvents = lEventService.getEventsForDay(timeStartOfMonth, timeEnd);
            currentMonthTime = currentMonthTime +  getTotalDayTime(listOfEvents);
            timeStartOfMonth = timeStartOfMonth + DAY_MILLISECONDS;
        }



        monthTime = currentMonthTime;
    }
    private void checkOpenEvent() {
        long timeOpenEvent = 0;
        long timeStart = 0;
        int month = month_real+1;

        LocalTime.DateString dataString = new LocalTime.DateString(year_real + "", month+"",  day_real+"", "", "", "");

        try {
            timeStart = dataString.getMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timeEnd = timeStart + DAY_MILLISECONDS;
        List<LocalEvent> listOfEvents;
        listOfEvents = lEventService.getEventsForDay(timeStart, timeEnd);

        if (listOfEvents.size() > 0 && !(listOfEvents.get(listOfEvents.size() - 1).isClose())) {

            timeOpenEvent = (LocalTime.getCurrentMilliseconds() - listOfEvents.get(listOfEvents.size() - 1).getData().getStartTime());
            working = true;
            dayTime = dayTime + timeOpenEvent;
            weekTime = weekTime + timeOpenEvent;
            monthTime = monthTime + timeOpenEvent;
        }
    }

    private long getTotalDayTime(List<LocalEvent> listOfEvents) {
        long total = 0;
        for (int i = 0; i < listOfEvents.size(); i++) {
           total = total + listOfEvents.get(i).getData().getDuration();

        }
        return total;
    }

    private long getTotalDayTime2(List<LocalEvent> listOfEvents) {

        long total = 0;
        for (int i = 0; i < listOfEvents.size(); i++) {

            total = total + listOfEvents.get(i).getData().getDuration();

        }
        return total;
    }

    public int getSecs() {
        return secs_real;
    }
}
