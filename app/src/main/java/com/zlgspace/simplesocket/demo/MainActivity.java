package com.zlgspace.simplesocket.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.startSvrBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleSvrSocketMng.start();
            }
        });


        findViewById(R.id.startClientBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleSocketMng.connect();
            }
        });

        findViewById(R.id.stopSvrBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleSvrSocketMng.stop();
            }
        });


        findViewById(R.id.stopClientBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleSocketMng.close();
            }
        });

    }
}
