package com.dora.myapplication.Cryptography.Symmetric;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.AllSymmetricCiphersMainActivity;
import com.dora.myapplication.R;

public class SymmetricMainActivity extends AppCompatActivity {

    Button caesarCipherBtn, aesBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symmetric_main);

        caesarCipherBtn = findViewById(R.id.caesarCipherSymmetricBtn);
        aesBtn = findViewById(R.id.aesCipherSymmetricBtn);

        caesarCipherBtn.setOnClickListener(view -> {
            Intent i = new Intent(SymmetricMainActivity.this, AllSymmetricCiphersMainActivity.class);
            // value in CAPITALS:
            i.putExtra("whichCipher", "CAESAR");
            startActivity(i);
        });

        aesBtn.setOnClickListener(view -> {
            Intent i = new Intent(SymmetricMainActivity.this, AllSymmetricCiphersMainActivity.class);
            i.putExtra("whichCipher", "AES");
            startActivity(i);
        });
    }
}