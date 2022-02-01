package com.dora.myapplication.Cryptography.ASymmetric;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dora.myapplication.Cryptography.ASymmetric.RSA.RSAMainActivity;
import com.dora.myapplication.R;

public class ASymmetricMainActivity extends AppCompatActivity {

    Button RSABtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asymmetric_main);

        RSABtn = findViewById(R.id.RSABtn);

        RSABtn.setOnClickListener(view -> {
            startActivity(new Intent(ASymmetricMainActivity.this, RSAMainActivity.class));
        });
    }
}