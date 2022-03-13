package com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dora.myapplication.R;

public class AllSymmetricCiphersMainActivity extends AppCompatActivity {

    Button encodeBtn, decodeBtn;
    String whichCipher;
    TextView heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_symmetric_ciphers_main);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            whichCipher = extras.getString("whichCipher");
        }

        encodeBtn = findViewById(R.id.encodeCaesarCipherBtn);
        decodeBtn = findViewById(R.id.decodeCaesarCipherBtn);
        heading = findViewById(R.id.symmetricCiphersHeadingTextView);

        switch (whichCipher) {
            case "AES":
                heading.setText("AES");
                break;
            case "CAESAR":
            default:
                heading.setText("Caesar Cipher");
                whichCipher = "CAESAR";
        }

        encodeBtn.setOnClickListener(view -> {
            Intent i = new Intent(AllSymmetricCiphersMainActivity.this, EncodeSymmetricCiphers.class);
            // value in CAPITALS:
            i.putExtra("whichCipher", whichCipher);
            startActivity(i);
        });
        decodeBtn.setOnClickListener(view -> {
            Intent i = new Intent(AllSymmetricCiphersMainActivity.this, DecodeSymmetricCiphers.class);
            // value in CAPITALS:
            i.putExtra("whichCipher", whichCipher);
            startActivity(i);
        });
    }
}