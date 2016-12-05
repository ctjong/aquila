package com.projectaquila;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.widget_actionbar,
                null);

        // Set up your ActionBar
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(actionBarLayout);
    }
}
