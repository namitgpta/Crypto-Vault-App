package com.dora.myapplication.Cryptography.Symmetric.CaesarCipher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dora.myapplication.R;

public class CaesarCipherMainActivity extends AppCompatActivity {

    Button encodeBtn, decodeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar_cipher_main);

        encodeBtn = findViewById(R.id.encodeCaesarCipherBtn);
        decodeBtn = findViewById(R.id.decodeCaesarCipherBtn);

        encodeBtn.setOnClickListener(view -> startActivity(new Intent(CaesarCipherMainActivity.this, CaesarCipherEncode.class)));
        decodeBtn.setOnClickListener(view -> startActivity(new Intent(CaesarCipherMainActivity.this, CaesarCipherDecode.class)));
    }
}