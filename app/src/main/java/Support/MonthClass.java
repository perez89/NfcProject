package Support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 19/10/2016.
 */

public class MonthClass {
    final static int MAX_WEEKS = 6;
    int month;
    int year;
    List<WeekClass> listOfWeeks;

    public MonthClass(int year, int month) {
        listOfWeeks = new ArrayList<WeekClass>();
        this.year = year;
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public List<WeekClass> getListOfWeeks() {
        return listOfWeeks;
    }

    public void addWeekToList(WeekClass week) {
        listOfWeeks.add(week);
    }

    public long getDurationSpecificMonth(){
        long totalDuration = 0;

        for(WeekClass weekObject: listOfWeeks
                ) {
            totalDuration = totalDuration + weekObject.getDurationSpecificMonth(getMonth());
        }
        return totalDuration;
    }

    public long getDaysChecked(){
        int totalDays = 0;

        for(WeekClass weekObject: listOfWeeks
                ) {
            totalDays = totalDays + weekObject.getDaysChecked(getMonth());
        }
        return totalDays;
    }
}
