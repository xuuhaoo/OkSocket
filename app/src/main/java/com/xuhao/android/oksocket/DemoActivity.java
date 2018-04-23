package com.xuhao.android.oksocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by didi on 2018/4/20.
 */

public class DemoActivity extends AppCompatActivity {

    private Button mSimpleBtn;

    private Button mComplexBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        mSimpleBtn = findViewById(R.id.btn1);
        mComplexBtn = findViewById(R.id.btn2);


        mSimpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, SimpleDemoActivity.class);
                startActivity(intent);
            }
        });
        mComplexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, ComplexDemoActivity.class);
                startActivity(intent);
            }
        });
    }
}
