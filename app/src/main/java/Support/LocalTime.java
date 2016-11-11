package Support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by User on 18/10/2016.
 */

public class LocalTime {
    public static long getCurrentMilliseconds() {
        long currentMillis = System.currentTimeMillis();
        return currentMillis;
    }

    public static int getMinute(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.MINUTE);
    }

    public static int getHour(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.HOUR);
    }

    public static int getDay(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekOfYear(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getWeekOfMonth(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getMonth(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.MONTH);
    }

    public static int getYear(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.YEAR);
    }

    public static int getDayOfWeek(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.DAY_OF_WEEK);
    }

    public static long getMillesecondsFromDate() {
       // Calendar cl = Calendar.getInstance();
        //cl.setTimeInMillis(millis);  //here your time in miliseconds
        //return cl.get(Calendar.DAY_OF_WEEK);
        return 0;
    }

    public static class DateString{
        String year; String month; String day; String hour; String minute; String seconds;

        public String getYear() {
            return year;
        }

        public String getMonth() {
            return month;
        }

        public String getDay() {
            return day;
        }

        public String getHour() {
            return hour;
        }

        public String getMinute() {
            return minute;
        }

        public String getSeconds() {
            return seconds;
        }

        public void setYear(String year) {
            if(year == null || year.equals(""))
                this.year = "1970";
            else
                this.year=year;
        }

        public void setMonth(String month) {
            if(month == null || month.equals(""))
                this.month = "01";
            else
                this.month=month;
        }

        public void setDay(String day) {
            if(day == null || day.equals(""))
                this.day = "01";
            else
                this.day=day;
        }

        public void setHour(String hour) {
            if(hour == null || hour.equals(""))
                this.hour = "00";
            else
                this.hour=hour;
        }

        public void setMinute(String minute) {
            if(minute == null || minute.equals("00"))
                this.minute = "00";
            else
                this.minute=minute;
        }

        public void setSeconds(String seconds) {
            if(seconds == null || seconds.equals(""))
                this.seconds = "00";
            else
                this.seconds=seconds;
        }

        public DateString(String year, String month, String day, String hour, String minute, String seconds) {
            setYear(year);
            setYear(month);
            setYear(day);
            setYear(hour);
            setYear(minute);
            setYear(seconds);
        }

        public DateString(int year, int month, int day, int hour, int minute, int seconds) {
            setYear(""+year);
            setYear(""+month);
            setYear(""+day);
            setYear(""+hour);
            setYear(""+minute);
            setYear(""+seconds);
        }

        public long getMilliseconds() throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String startDateInString = getDay()+"-"+getMonth()+"-"+getYear()+ " "+getHour()+":"+getMinute()+":"+getSeconds();
            Date first_month = sdf.parse(startDateInString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(first_month);
            return calendar.getTimeInMillis();
        }
    }

    public static class DayRange {
        final static private int MAX_DAYS_SHOW = 42;
        private long startDay;
        private long endDay;

        public DayRange() {
            Calendar c = Calendar.getInstance();
            int seconds = c.get(Calendar.SECOND);
            int minutes = c.get(Calendar.MINUTE);
            int hour = c.get(Calendar.HOUR_OF_DAY);

            long currentMilliseconds = LocalTime.getCurrentMilliseconds();
            startDay = currentMilliseconds - ((hour * 3600000) + (minutes * 60000) + (seconds * 1000));
            //86400000 -> number of milleseconds of a day
            endDay = startDay + 86400000;
        }

        public long getStartDay() {
            return startDay;
        }

        public long getEndDay() {
            return endDay;
        }
    }

    public static class WeeksToShow {
        private long startWeeksToShow;

        public long getStartMonth() {
            return startWeeksToShow;
        }

        public WeeksToShow(int year, int month) throws ParseException {


            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            String startDateInString = "01-" + month + "-" + year + " 00:00:00";


            Date first_month = sdf.parse(startDateInString);


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(first_month);
            startWeeksToShow = calendar.getTimeInMillis();

            int weekOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

            if(weekOfMonth==1)
                weekOfMonth = 7;
            else
                weekOfMonth--;
            startWeeksToShow = startWeeksToShow - (86400000*(weekOfMonth-1));

        }
    }



}
