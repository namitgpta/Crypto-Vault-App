package com.dora.myapplication.Cryptography;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dora.myapplication.Cryptography.ASymmetric.ASymmetricMainActivity;
import com.dora.myapplication.Cryptography.Symmetric.SymmetricMainActivity;
import com.dora.myapplication.R;

public class CryptographyMainActivity extends AppCompatActivity {

    Button symmCiphersBtn, ASymmCiphersBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptography_main);

        symmCiphersBtn = findViewById(R.id.symmCiphersBtn);
        ASymmCiphersBtn = findViewById(R.id.ASymmCiphersBtn);

        symmCiphersBtn.setOnClickListener(view -> startActivity(new Intent(CryptographyMainActivity.this, SymmetricMainActivity.class)));
        ASymmCiphersBtn.setOnClickListener(view -> startActivity(new Intent(CryptographyMainActivity.this, ASymmetricMainActivity.class)));
    }
}