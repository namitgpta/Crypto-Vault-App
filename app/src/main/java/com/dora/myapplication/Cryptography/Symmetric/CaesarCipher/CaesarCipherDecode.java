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

public class CaesarCipherDecode extends AppCompatActivity {

    EditText inputEncodedMessageEditText, inputKeyEditText;
    Button decodeBtn, tapToCopyBtn;
    ConstraintLayout afterDecodingConstraintLayout;
    TextView successMessageDecodedTextView, decodedMessageTextView;
    String decodedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caesar_cipher_decode);

        inputEncodedMessageEditText = findViewById(R.id.inputMessageCaesarCipherDecodeEditText);
        inputKeyEditText = findViewById(R.id.inputKeyCaesarCipherDecodeEditText);
        decodeBtn = findViewById(R.id.decodeCaesarCipherDecodeBtn);
        tapToCopyBtn = findViewById(R.id.copyToClipboardBtnCaesarDecrypt);
        afterDecodingConstraintLayout = findViewById(R.id.afterDecodingConstraintLayoutCaesarDecrypt);
        successMessageDecodedTextView = findViewById(R.id.successMessageDecodedCaesarTextView);
        decodedMessageTextView = findViewById(R.id.decodedMessageTextViewCaesarDecrypt);

        decodedMessage = null;

        successMessageDecodedTextView.setVisibility(View.INVISIBLE);
        afterDecodingConstraintLayout.setVisibility(View.INVISIBLE);

        decodeBtn.setOnClickListener(view -> {
            successMessageDecodedTextView.setVisibility(View.INVISIBLE);
            afterDecodingConstraintLayout.setVisibility(View.INVISIBLE);
            String encodedMessage = inputEncodedMessageEditText.getText().toString().trim();
            if (encodedMessage.isEmpty() || inputKeyEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Empty Inputs !!!", Toast.LENGTH_SHORT).show();
            } else {
                int inputKey = Integer.parseInt(inputKeyEditText.getText().toString());

                // start decoding:
                decodedMessage = startDecodingFunction(encodedMessage, inputKey);
                successMessageDecodedTextView.setVisibility(View.VISIBLE);
                afterDecodingConstraintLayout.setVisibility(View.VISIBLE);
                decodedMessageTextView.setText(decodedMessage);
            }
        });

        tapToCopyBtn.setOnClickListener(view -> {
            if (decodedMessage != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("RSAEncodedMessage_CryptoVault", decodedMessage);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String startDecodingFunction(String encodedMessage, int key) {
        // M = (C-K) % 26
        StringBuilder plainText = new StringBuilder();
        for (int i = 0; i < encodedMessage.length(); i++) {
            int c = encodedMessage.charAt(i);
            if (c >= 97 && c <= 122) {
                c = startDecodingHelper(c, key, 26, 97);
            } else if (c >= 65 && c <= 90) {
                c = startDecodingHelper(c, key, 26, 65);
            } else if (c >= 48 && c <= 57) {
                c = startDecodingHelper(c, key, 10, 48);
            } else {
                c -= key;
            }
            plainText.append((char) c);

        }
        return plainText.toString();
    }

    private int startDecodingHelper(int c, int key, int mod, int initial) {
        c -= initial;
        c -= key;
        c %= mod;
        if (c < 0) c = mod + c;
        c += initial;
        return c;
    }
}