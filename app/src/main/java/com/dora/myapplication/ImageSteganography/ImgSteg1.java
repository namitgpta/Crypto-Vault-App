package com.dora.myapplication.ImageSteganography;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dora.myapplication.R;

public class ImgSteg1 extends AppCompatActivity {

    Button encodeBtn, decodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_steg1);

        encodeBtn = findViewById(R.id.encodeImgSteg);
        decodeBtn = findViewById(R.id.decodeImgSteg);

        encodeBtn.setOnClickListener(view -> {
            startActivity(new Intent(ImgSteg1.this, EncodeImgSteg1.class));
        });

        decodeBtn.setOnClickListener(view -> {
            startActivity(new Intent(ImgSteg1.this, DecodeImgSteg1.class));
        });
    }
}