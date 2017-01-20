package Support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 19/10/2016.
 */

public class LocalWeek {
    final static int MAX_DAYS = 7;
    private List<LocalDay> listOfDays;
    int weekOfTheMonth;
    int weekOfTheYear;

    public LocalWeek(int weekOfTheMonth, int weekOfTheYear) {
        listOfDays = new ArrayList<LocalDay>();
        this.weekOfTheMonth = weekOfTheMonth;
        this.weekOfTheYear = weekOfTheYear;
    }

    public List<LocalDay> getListOfDays() {
        return listOfDays;
    }

    public void addDayToList(LocalDay day) {
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

        for(LocalDay event: listOfDays
                ) {
            totalDuration = totalDuration + event.getDayTotalDuration();
        }
        return totalDuration;
    }

    public long getDurationSpecificMonth(int month){
        long totalDuration = 0;

        for(LocalDay dayObject: listOfDays
                ) {
            if(dayObject.getMonth() == month)
                totalDuration = totalDuration + dayObject.getDayTotalDuration();
        }
        return totalDuration;
    }

    public int getDaysChecked(int month) {
        int daysChecked = 0;

        for(LocalDay dayObject: listOfDays
                ) {
            if(dayObject.getMonth() == month)
                daysChecked++;
        }
        return daysChecked;
    }
}
