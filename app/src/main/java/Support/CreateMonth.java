package Support;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by User on 19/10/2016.
 */

public class CreateMonth{
    WeakReference<Context> mWeakRefContext;
    private MonthClass monthObj;
    long startWeeksToShow;
    int year;
    int month;

    public CreateMonth(Context c, int year, int month) {
        mWeakRefContext= new WeakReference<Context>(c);
        this.year = year;
        this.month = month;
       // LocalTime.WeeksToShow lTime;

        try {
            //lTime = new LocalTime.WeeksToShow(year, month);
            System.out.println("yearmonth = "+ year + " " + month);
            startWeeksToShow =  LocalTime.getInitialTimeOfLayout(year, month);
            System.out.println("startWeeksToShow = "+ startWeeksToShow);

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public long getStartWeeksToShow(){
        return startWeeksToShow;
    }
    public MonthClass loadAndGetMonth(){
        startLoadingDays();
        return monthObj;
    }
    private void startLoadingDays() {
        int countWeeks = 0;
        int countDaysOfTheWeek = 0;

        monthObj = new MonthClass(year, month);
        WeekClass weekObject = createWeek(startWeeksToShow);
        LocalEventService lEventService = new LocalEventService(mWeakRefContext);
        while(true){
            List<EventClass> listOfEvents = lEventService.getEventsForDay(startWeeksToShow, startWeeksToShow+86400000);
            DayClass dayObject = createDay(startWeeksToShow);
            dayObject.addAllEvents(listOfEvents);
            listOfEvents.clear();
            weekObject.addDayToList(dayObject);

            //obterDias

            startWeeksToShow = startWeeksToShow+86400000;

            countDaysOfTheWeek++;
            if(countDaysOfTheWeek == 7){
                countWeeks++;

                monthObj.addWeekToList(weekObject);
                weekObject = createWeek(startWeeksToShow);
                countDaysOfTheWeek=0;

            }
            if(countWeeks == MonthClass.MAX_WEEKS){
                break;
            }
        }

    }

    private WeekClass createWeek(long startWeeksToShow) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startWeeksToShow);  //here your time in miliseconds
        int weekOfTheMonth = cl.get(Calendar.WEEK_OF_MONTH);
        int weekOfTheYear = cl.get(Calendar.WEEK_OF_YEAR);

        return new WeekClass(weekOfTheMonth,weekOfTheYear);
    }

    private DayClass createDay(long startWeeksToShow) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(startWeeksToShow);  //here your time in miliseconds
        int year = cl.get(Calendar.YEAR);
        int month = cl.get(Calendar.MONTH);
        int day = cl.get(Calendar.DAY_OF_MONTH);

        return new DayClass(day, month, year);
    }

}
