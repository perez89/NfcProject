package com.perez.schedulebynfc;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by User on 23/11/2016.
 */

public class BottomFragment extends Fragment {

    private View rootView;
    private Context context;

    public static BottomFragment newInstance(int _month, int _year) {
        BottomFragment myFragment = new BottomFragment();
        Bundle args = new Bundle();
        args.putInt("year_CurrentView", _year);
        args.putInt("month_CurrentView", _month);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        System.out.println(" MainFragment - OnCreate");
        rootView = inflater.inflate(R.layout.fragment_bottom, container, false);
        context= getActivity();
        Bundle b = getArguments();
        int year = b.getInt("year_CurrentView");
        int month = b.getInt("month_CurrentView");
        month++;

        return rootView;

    }
}

