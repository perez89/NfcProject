package com.perez.schedulebynfc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.List;

import Support.CreateMonth;
import Support.GlobalStrings;
import Support.LocalDay;
import Support.LocalTime;
import Support.LocalWeek;

/**
 * Created by User on 09/08/2017.
 */

public class ChartActivity extends AppCompatActivity{
    XYPlot chart;
    private int yearToShow, monthToShow, weekToShow;
    private int defaultNumber=-1;
    private String chartType = "";
    private Support.LocalMonth monthData;
    private long maxTotalDay = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            yearToShow = savedInstanceState.getInt("year_CurrentView", defaultNumber);
            monthToShow = savedInstanceState.getInt("month_CurrentView", defaultNumber);
        }else{
            Intent i = getIntent();
            Bundle extras = i.getExtras();
            weekToShow = extras.getInt("week_CurrentView", defaultNumber);
            yearToShow = extras.getInt("year_CurrentView", defaultNumber);
            monthToShow = extras.getInt("month_CurrentView", defaultNumber);
            chartType = extras.getString("chartType", "Month");
        }
        System.out.println(" weekToShow= " +weekToShow + " yearToShow= " +yearToShow + " monthToShow= " +monthToShow + " chartType= " +chartType );
        setContentView(R.layout.fragment_dialog_chart);
        initialize();

        switch(chartType){
            case GlobalStrings.WEEK_TYPE:
                if(weekToShow>defaultNumber && monthToShow > defaultNumber && yearToShow>defaultNumber)
                  initializationChartForWeek();
                break;
            case GlobalStrings.MONTH_TYPE:
                if(yearToShow>defaultNumber && monthToShow > defaultNumber)
                    initializationChartForMonth();
                break;
            case GlobalStrings.YEAR_TYPE:
                if(yearToShow>defaultNumber)
                    initializationChartForYear();
                break;
            default:
                if(yearToShow>defaultNumber && monthToShow > defaultNumber)
                    initializationChartForMonth();
                break;
        }
    }

    private void initializationChartForWeek() {
        if(chart != null){
            //Fragment frag_new = ArrayOfEvents[1];
            loadMonthData();
            loadDataWeekChart();
        }
    }

    private void initializationChartForMonth() {
        if(chart != null){
            //Fragment frag_new = ArrayOfEvents[1];
            loadMonthData();
            loadMonthChart();
        }

    }
    private void initializationChartForYear() {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("year_CurrentView",yearToShow) ;
        savedInstanceState.putInt("month_CurrentView",monthToShow) ;
        super.onSaveInstanceState(savedInstanceState);
    }

    private void initialize(){
        chart = (XYPlot) findViewById(R.id.plot);
    };



    private void loadMonthData() {
        CreateMonth month = new CreateMonth(this, yearToShow, monthToShow);
         monthData = month.loadAndGetMonth();
    }
    private void loadDataWeekChart() {
        List<Float> entries= new ArrayList<Float>();
        Number[] series1Numbers ;
        final String[] domainLabels = {"Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg"};
       LocalWeek week = monthData.getListOfWeeks().get(weekToShow);

        for (LocalDay day : week.getListOfDays()) {
            if(day!=null){

                long dayTotalHour= LocalTime.getHour(day.getDayTotalDuration());
                if(dayTotalHour>maxTotalDay)
                    maxTotalDay=dayTotalHour;

                long dayTotalTime= day.getDayTotalDuration();
                String dayTime = LocalTime.getFormatTime(dayTotalTime);
                dayTime = dayTime.replace(':', '.');

                if(dayTime.equals("-")){
                    dayTime = "0";
                }
                float dayinLongFormat =(float)Double.parseDouble(dayTime);


                entries.add(dayinLongFormat);
            }
        }



        XYSeries series1 = new SimpleXYSeries(entries,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Hours of work");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        BarFormatter bf = new BarFormatter(Color.RED, Color.WHITE);

        // add a new series' to the xyplot:


        chart.addSeries(series1, bf);
        BarRenderer renderer = chart.getRenderer(BarRenderer.class);
        renderer.setBarOrientation(BarRenderer.BarOrientation.OVERLAID);
    }

    private void loadMonthChart() {

      List<Float> entries= new ArrayList<Float>();
        Number[] series1Numbers ;
        final String[] domainLabels = {"Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom", "Seg"};
        for (LocalWeek week : monthData.getListOfWeeks()) {
            int dayOfTheWeek = 0;
            for (LocalDay day : week.getListOfDays()) {
                if(day!=null && day.getMonth() == (monthToShow-1)){

                    long dayTotalHour= LocalTime.getHour(day.getDayTotalDuration());
                    if(dayTotalHour>maxTotalDay)
                        maxTotalDay=dayTotalHour;


                    long dayTotalTime= day.getDayTotalDuration();
                String dayTime = LocalTime.getFormatTime(dayTotalTime);
                    dayTime = dayTime.replace(':', '.');

                if(dayTime.equals("-")){
                    dayTime = "0";
                }
                float dayinLongFormat =(float)Double.parseDouble(dayTime);

               // System.out.println(count + " "+ day.getDay() + " " + dayTime + " " + dayinLongFormat);
                    entries.add(dayinLongFormat);
                    //entries.add(new BarEntry(count, dayinLongFormat, ">"+day.getDay()));
                //    legendEntries.add(new LegendEntry(day.getDay()+"", Legend.LegendForm.CIRCLE, 10f, 5f, new DashPathEffect(new float[] {10f,5f}, 5f), 3));
             // entries.add(new Entry(count, Double.valueOf(LocalTime.getHour(day.getDayTotalDuration()) +"."+  LocalTime.getMinute(day.getDayTotalDuration())).longValue()));
                }

                dayOfTheWeek++;
            }
            dayOfTheWeek=0;
            // turn your data into Entry objects

        }

        XYSeries series1 = new SimpleXYSeries(entries,SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Hours of work");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.RED, Color.GREEN, Color.BLUE, null);
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        BarFormatter bf = new BarFormatter(Color.RED, Color.WHITE);

        // add a new series' to the xyplot:


        chart.addSeries(series1, bf);
        BarRenderer renderer = chart.getRenderer(BarRenderer.class);
        renderer.setBarOrientation(BarRenderer.BarOrientation.OVERLAID);
     /*  chart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domainLabels[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });*/


        /*BarDataSet dataSet = new BarDataSet(entries,"Grafico de " + LocalTime.getMonthStringFormat(monthToShow-1) +" de "+ yearToShow); // add entries to dataset

        dataSet.setColor(Color.GRAY);
        dataSet.setValueTextColor(Color.BLACK); // styling, ...
        BarData lineData = new BarData(dataSet);

        System.out.println(" lineData.getDataSetCount()="+ lineData.getEntryCount());
        chart.setData(lineData);
         /*
         String[] labels ={"luis","paulo","lemos","perez"};
        chart.getXAxis().setValueFormatter(new LabelFormatter(labels));

        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        */
        // set a custom value formatter

     /*   chart.setVisibleXRange(0,monthData.getListOfWeeks().size()*7+1);
        chart.setVisibleYRange(0,maxTotalDay+2, null);





        chart.invalidate();*/ // refresh
    }

    @Override
    public void onBackPressed() {
        finish();
        startMainActivity();
        //  super.onBackPressed();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }
}
