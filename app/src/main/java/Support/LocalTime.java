package Support;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by User on 18/10/2016.
 */

public class LocalTime {
    public static long getCurrentMilliseconds() {
        long currentMillis = System.currentTimeMillis();
        return currentMillis;
    }

    public static int getMilliSeconds(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.MILLISECOND);
    }

    public static int getSeconds(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.SECOND);
    }

    public static int getMinute(long millis) {
        Calendar cl = Calendar.getInstance();
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.MINUTE);
    }

    public static int getHour(long millis) {
        Calendar cl = Calendar.getInstance();

        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.HOUR_OF_DAY);
    }

    public static int getDay(long millis) {
        Calendar cl = Calendar.getInstance();

        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekOfYear(long millis) {
        Calendar cl = Calendar.getInstance();
        //cl.setFirstDayOfWeek(Calendar.MONDAY);
        cl.setMinimalDaysInFirstWeek(4);
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return cl.get(Calendar.WEEK_OF_YEAR);
    }

    public static int getWeekOfMonth(long millis) {
        Calendar cl = Calendar.getInstance();
        // cl.setFirstDayOfWeek(Calendar.MONDAY);
        cl.setTimeInMillis(millis);  //here your time in miliseconds
        return (cl.get(Calendar.WEEK_OF_MONTH));
    }

    public static String getMonthStringFormat(int month) {
        if (month >= 0 && month < 12) {
            DateFormatSymbols dfs = new DateFormatSymbols();
            String[] months = dfs.getMonths();
            return months[month];
        }
        return "";
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

    public static int getNumberDaysMonth(int year, int month) {
        Calendar mycal = new GregorianCalendar(year, month, 1);

        // Get the number of days in that month
        int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 28
        return daysInMonth;
    }

    public static long getMillesecondsFromDate() {
        // Calendar cl = Calendar.getInstance();
        //cl.setTimeInMillis(millis);  //here your time in miliseconds
        //return cl.get(Calendar.DAY_OF_WEEK);
        return 0;
    }

    public static String getDayOfWeekFormatText(long time) {
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        // for the current Locale :
        //   DateFormatSymbols symbols = new DateFormatSymbols();
        //   DateFormatSymbols symbols = new DateFormatSymbols();
        String[] dayNames = symbols.getWeekdays();
        int dayOfTheWeek = getDayOfWeek(time);

        return dayNames[dayOfTheWeek];
    }

    public static long getTimeWithoutSeconds() {
        long time = LocalTime.getCurrentMilliseconds();
        int year = LocalTime.getYear(time);
        int month = LocalTime.getMonth(time);
        month++;
        int day = LocalTime.getDay(time);
        int hour = LocalTime.getHour(time);
        int min = LocalTime.getMinute(time);
      //  System.out.println("gettime" + year + " " + month + " " + day + " " + hour + " " + min);
        LocalTime.DateString dataString = new LocalTime.DateString(year + "", month + "", day + "", hour + "", min + "", "");

        try {
            time = dataString.getMilliseconds();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;

    }

    public static class DateString {
        String year;
        String month;
        String day;
        String hour;
        String minute;
        String seconds;

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
            if (year == null || year.equals("") || year.equals(""))
                this.year = "1970";
            else
                this.year = year;
        }

        public void setMonth(String month) {
            if (month == null || month.equals("") || month.equals(""))
                this.month = "01";
            else
                this.month = month;
        }

        public void setDay(String day) {
            if (day == null || day.equals("") || day.equals(""))
                this.day = "01";
            else
                this.day = day;
        }

        public void setHour(String hour) {
            if (hour == null || hour.equals("") || hour.equals(""))
                this.hour = "00";
            else
                this.hour = hour;
        }

        public void setMinute(String minute) {
            if (minute == null || minute.equals("00") || minute.equals(""))
                this.minute = "00";
            else
                this.minute = minute;
        }

        public void setSeconds(String seconds) {
            if (seconds == null || seconds.equals("") || seconds.equals(""))
                this.seconds = "00";
            else
                this.seconds = seconds;
        }

        public DateString(String year, String month, String day, String hour, String minute, String seconds) {
            setYear(year);
            setMonth(month);
            setDay(day);
            setHour(hour);
            setMinute(minute);
            setSeconds(seconds);
        }

        public DateString(int year, int month, int day, int hour, int minute, int seconds) {
            setYear("" + year);
            setYear("" + month);
            setYear("" + day);
            setYear("" + hour);
            setYear("" + minute);
            setYear("" + seconds);
        }

        public long getMilliseconds() throws ParseException {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");
            String startDateInString = getDay() + "-" + getMonth() + "-" + getYear() + " " + getHour() + ":" + getMinute() + ":" + getSeconds();
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


    public static long getInitialTimeOfLayout(int year, int month) throws ParseException {
        long startWeeksToShow;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        String startDateInString = "01-" + month + "-" + year + " 00:00:00";


        Date first_month = sdf.parse(startDateInString);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(first_month);
        startWeeksToShow = calendar.getTimeInMillis();

        int weekOfMonth = calendar.get(Calendar.DAY_OF_WEEK);

        if (weekOfMonth == 1)
            weekOfMonth = 7;
        else
            weekOfMonth--;
        startWeeksToShow = startWeeksToShow - (86400000 * (weekOfMonth - 1));

        return startWeeksToShow;
    }

    public static String getFormatTime(long time) {
        if (time > 0) {
            String durationString = "";
            long numOfDays = 0;
            if (time >= 86400000) {
                numOfDays = time / 86400000;
                numOfDays = numOfDays * 24;
            }

            int minutes = (int) ((time / (1000 * 60)) % 60);
            int hours = (int) ((time / (1000 * 60 * 60)) % 24);
          //  System.out.println("hours= " + hours);
            //System.out.println("minutes= " + minutes);
            hours = (int) numOfDays + hours;
           // System.out.println("hours2= " + hours);
            if (hours > 0 || minutes > 0) {
                if (minutes < 10)
                    durationString = hours + ":0" + minutes;
                else
                    durationString = hours + ":" + minutes;

            }
            if (durationString.equals("") || durationString.equals(" "))
                return "0";
            else
                return durationString;
        }
        return "-";
    }

    public static String getDateFormatIso(long milliseconds) {
        if (milliseconds > 0) {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
            df.setTimeZone(tz);
            return (df.format(milliseconds));
        }
        return "";
    }

}
