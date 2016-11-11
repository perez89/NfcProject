package Support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 19/10/2016.
 */

public class WeekClass {
    final static int MAX_DAYS = 7;
    private List<DayClass> listOfDays;
    int weekOfTheMonth;
    int weekOfTheYear;

    public WeekClass(int weekOfTheMonth, int weekOfTheYear) {
        listOfDays = new ArrayList<DayClass>();
        this.weekOfTheMonth = weekOfTheMonth;
        this.weekOfTheYear = weekOfTheYear;
    }

    public List<DayClass> getListOfDays() {
        return listOfDays;
    }

    public void addDayToList(DayClass day) {
        listOfDays.add(day);
    }

    public int getWeekOfTheMonth() {
        return weekOfTheMonth;
    }

    public int getWeekOfTheYear() {
        return weekOfTheYear;
    }

    public long getWeekTotalDuration(){
        long totalDuration = 0;

        for(DayClass event: listOfDays
                ) {
            totalDuration = totalDuration + event.getDayTotalDuration();
        }
        return totalDuration;
    }

    public long getDurationSpecificMonth(int month){
        long totalDuration = 0;

        for(DayClass dayObject: listOfDays
                ) {
            if(dayObject.getMonth() == month)
                totalDuration = totalDuration + dayObject.getDayTotalDuration();
        }
        return totalDuration;
    }

    public int getDaysChecked(int month) {
        int daysChecked = 0;

        for(DayClass dayObject: listOfDays
                ) {
            if(dayObject.getMonth() == month)
                daysChecked++;
        }
        return daysChecked;
    }
}
