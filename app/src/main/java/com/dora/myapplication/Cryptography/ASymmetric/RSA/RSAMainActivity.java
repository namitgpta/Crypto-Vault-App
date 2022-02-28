package com.dora.myapplication.Cryptography.ASymmetric.RSA;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dora.myapplication.R;

public class RSAMainActivity extends AppCompatActivity {

    Button keyGenerateBtn, encryptBtn, decryptBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsamain);

        keyGenerateBtn = findViewById(R.id.rsaKeyGenerateBtn);
        encryptBtn = findViewById(R.id.encryptRSABtn);
        decryptBtn = findViewById(R.id.decryptRSABtn);

        keyGenerateBtn.setOnClickListener(view -> {
            startActivity(new Intent(RSAMainActivity.this, RsaKeyGeneration.class));
        });
        encryptBtn.setOnClickListener(view -> {
            startActivity(new Intent(RSAMainActivity.this, RsaEncrypt.class));
        });
        decryptBtn.setOnClickListener(view -> {
            startActivity(new Intent(RSAMainActivity.this, RsaDecrypt.class));
        });


    }
}