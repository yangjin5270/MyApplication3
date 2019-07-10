package com.td.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";

    @Override
    protected void onStart() {
        Log.i(this.getClass().getSimpleName(),"onStart");
        super.onStart();

    }

    @Override
    protected void onStop() {
        Log.i(this.getClass().getSimpleName(),"onStop");
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(this.getClass().getSimpleName(),"onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onRestart() {
        Log.i(this.getClass().getSimpleName(),"onResStart");
        super.onRestart();

    }

    @Override
    protected void onResume() {
        Log.i(this.getClass().getSimpleName(),"onReusme");
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        Log.i(this.getClass().getSimpleName(),"onDestroy");
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        Log.i(this.getClass().getSimpleName(),"onPause");
        super.onPause();

    }

}
