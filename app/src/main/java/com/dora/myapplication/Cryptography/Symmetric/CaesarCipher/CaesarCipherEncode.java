package com.dora.myapplication.Cryptography.Symmetric.CaesarCipher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dora.myapplication.R;

public class CaesarCipherEncode extends AppCompatActivity {

    EditText inputMessageEditText, inputKeyEditText;
    Button encodeBtn, tapToCopyBtn;
    ConstraintLayout afterEncodingConstraintLayout;
    TextView successMessageEncodedTextView, encodedMessageTextView;
    String encodedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar_cipher_encode);

        inputMessageEditText = findViewById(R.id.inputMessageCaesarCipherEncodeEditText);
        inputKeyEditText = findViewById(R.id.inputKeyCaesarCipherEncodeEditText);
        encodeBtn = findViewById(R.id.encodeCaesarCipherEncodeBtn);
        tapToCopyBtn = findViewById(R.id.copyToClipboardBtnCaesarEncrypt);
        afterEncodingConstraintLayout = findViewById(R.id.afterEncodingConstraintLayoutCaesarEncrypt);
        successMessageEncodedTextView = findViewById(R.id.successMessageEncodedCaesarTextView);
        encodedMessageTextView = findViewById(R.id.encodedMessageTextViewCaesarEncrypt);

        encodedMessage = null;

        successMessageEncodedTextView.setVisibility(View.INVISIBLE);
        afterEncodingConstraintLayout.setVisibility(View.INVISIBLE);

        encodeBtn.setOnClickListener(view -> {
            successMessageEncodedTextView.setVisibility(View.INVISIBLE);
            afterEncodingConstraintLayout.setVisibility(View.INVISIBLE);
            String inputMessage = inputMessageEditText.getText().toString().trim();
            if (inputMessage.isEmpty() || inputKeyEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Empty Inputs !!!", Toast.LENGTH_SHORT).show();
            } else {
                int inputKey = Integer.parseInt(inputKeyEditText.getText().toString());

                // start encoding:
                encodedMessage = startEncodingFunction(inputMessage, inputKey);
                successMessageEncodedTextView.setVisibility(View.VISIBLE);
                afterEncodingConstraintLayout.setVisibility(View.VISIBLE);
                encodedMessageTextView.setText(encodedMessage);
            }
        });

        tapToCopyBtn.setOnClickListener(view -> {
            if (encodedMessage != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("RSAEncodedMessage_CryptoVault", encodedMessage);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String startEncodingFunction(String message, int key) {
        // C = (M+K) % 26
        StringBuilder cipherText = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            int c = message.charAt(i);
            if (c >= 97 && c <= 122) {
                c = startEncodingHelper(c, key, 26, 97);
            } else if (c >= 65 && c <= 90) {
                c = startEncodingHelper(c, key, 26, 65);
            } else if (c >= 48 && c <= 57) {
                c = startEncodingHelper(c, key, 10, 48);
            } else {
                c += key;
            }
            cipherText.append((char) c);

        }
        return cipherText.toString();
    }

    private int startEncodingHelper(int c, int key, int mod, int initial) {
        c -= initial;
        c += key;
        c %= mod;
        c += initial;
        return c;
    }

}