package com.github.fallblank.customplayerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.fallblank.customplayerview.customview.CustomPlayer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CustomPlayer player = (CustomPlayer) findViewById(R.id.player_view);
    }
}
