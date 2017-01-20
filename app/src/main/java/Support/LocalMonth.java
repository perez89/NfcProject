package Support;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 19/10/2016.
 */

public class LocalMonth {
    final static int MAX_WEEKS = 6;
    int month;
    int year;
    List<LocalWeek> listOfWeeks;

    public LocalMonth(int year, int month) {
        listOfWeeks = new ArrayList<LocalWeek>();
        this.year = year;
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public List<LocalWeek> getListOfWeeks() {
        return listOfWeeks;
    }

    public void addWeekToList(LocalWeek week) {
        listOfWeeks.add(week);
    }

    public long getDurationSpecificMonth(){
        long totalDuration = 0;

        for(LocalWeek weekObject: listOfWeeks
                ) {
            totalDuration = totalDuration + weekObject.getDurationSpecificMonth(getMonth());
        }
        return totalDuration;
    }

    public long getDaysChecked(){
        int totalDays = 0;

        for(LocalWeek weekObject: listOfWeeks
                ) {
            totalDays = totalDays + weekObject.getDaysChecked(getMonth());
        }
        return totalDays;
    }
}
