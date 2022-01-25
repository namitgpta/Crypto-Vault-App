package com.dora.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dora.myapplication.ImageSteganography.ImgSteg1;

public class MainActivity extends AppCompatActivity {

    Button imgStegBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgStegBtn = findViewById(R.id.imgStegBtn);
        imgStegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ImgSteg1.class));
            }
        });
    }
}