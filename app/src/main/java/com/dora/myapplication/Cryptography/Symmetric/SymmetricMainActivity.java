package com.dora.myapplication.Cryptography.Symmetric;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dora.myapplication.Cryptography.Symmetric.CaesarCipher.CaesarCipherMainActivity;
import com.dora.myapplication.R;

public class SymmetricMainActivity extends AppCompatActivity {

    Button caesarCipherBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symmetric_main);

        caesarCipherBtn = findViewById(R.id.caesarCipherBtn);

        caesarCipherBtn.setOnClickListener(view -> startActivity(new Intent(SymmetricMainActivity.this, CaesarCipherMainActivity.class)));
    }
}