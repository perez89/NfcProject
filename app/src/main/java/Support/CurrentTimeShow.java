package Support;

/**
 * Created by User on 25/10/2016.
 */

public class CurrentTimeShow{
    int year_CurrentView;
    int month_CurrentView;
    int year_previous;
    int year_next;
    int month_previous;
    int month_next;

    private static CurrentTimeShow singleton = new CurrentTimeShow( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private CurrentTimeShow() {
        year_CurrentView = LocalTime.getYear(LocalTime.getCurrentMilliseconds());
        month_CurrentView = LocalTime.getMonth(LocalTime.getCurrentMilliseconds());
        refreshPreviousAndNext();
    }

    /* Static 'instance' method */
    public static CurrentTimeShow getInstance( ) {
        return singleton;
    }

    private void refreshPreviousAndNext() {
        if(month_CurrentView == 11){
            year_previous = year_CurrentView;
            year_next= year_CurrentView + 1;
            month_previous = month_CurrentView - 1;
            month_next = 0;
        }else if(month_CurrentView == 0){
            year_previous = year_CurrentView - 1;
            year_next = year_CurrentView;
            month_previous = 11;
            month_next = month_CurrentView + 1;
        }else{
            year_previous = year_CurrentView;
            year_next = year_CurrentView;
            month_previous = month_CurrentView - 1;
            month_next = month_CurrentView + 1;
        }
    }

    public void nextMonth(){
        goToNextMonth();
    }

    public void previousMonth(){
        goToPreviousMonth();
    }

    private void goToNextMonth(){
        if(month_CurrentView==11){
            month_CurrentView=0;
            year_CurrentView++;
        }else{
            month_CurrentView++;
        }
        refreshPreviousAndNext();
    }

    private void goToPreviousMonth(){
        if(month_CurrentView==0){
            month_CurrentView=11;
            year_CurrentView--;
        }else{
            month_CurrentView--;
        }

        refreshPreviousAndNext();
    }
    public int getMonth_next() {
        return month_next;
    }

    public int getYear_CurrentView() {
        return year_CurrentView;
    }

    public int getMonth_CurrentView() {
        return month_CurrentView;
    }

    public int getYear_previous() {
        return year_previous;
    }

    public int getYear_next() {
        return year_next;
    }

    public int getMonth_previous() {
        return month_previous;
    }

}
