package com.perez.schedulebynfc;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import Support.CreateMonth;
import Support.GlobalStrings;
import Support.LocalPreferences;

/**
 * Created by User on 09/08/2017.
 */

public class ExcelMainFragment extends Fragment {
    private int yearToShow, monthToShow;
    private Context context;

    public static ExcelMainFragment newInstance(int year, int month) {
        ExcelMainFragment myFragment = new ExcelMainFragment();

        Bundle args = new Bundle();
        args.putInt("year_CurrentView", year);
        args.putInt("month_CurrentView", month);
        myFragment.setArguments(args);

        return myFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_show_main_save, container, false);
        context = getActivity();
        Bundle b = getArguments();
        yearToShow = b.getInt("year_CurrentView");
        monthToShow = b.getInt("month_CurrentView");
        monthToShow++;
         initiatilization();
        return rootView;
    }

    private void initiatilization() {
        CreateMonth month = new CreateMonth(context, yearToShow, monthToShow);
        Support.LocalMonth monthData = month.loadAndGetMonth();
    }

    private void xpto() {
        LocalPreferences lp = LocalPreferences.getInstance();
        int min_month_app =  Integer.parseInt(lp.getPreference(GlobalStrings.MINIMIUM_MONTH_APP, context));
        int min_year_app = Integer.parseInt(lp.getPreference(GlobalStrings.MINIMIUM_YEAR_APP, context));
        int max_month_app = Integer.parseInt(lp.getPreference(GlobalStrings.MAXIMUM_MONTH_APP, context));
        int max_year_app = Integer.parseInt(lp.getPreference(GlobalStrings.MAXIMUM_YEAR_APP, context));

    }
}
