package com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dora.myapplication.R;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncodeSymmetricCiphers extends AppCompatActivity {

    EditText inputMessageEditText, inputKeyEditText;
    Button encodeBtn, tapToCopyBtn;
    ConstraintLayout afterEncodingConstraintLayout;
    TextView successMessageEncodedTextView, encodedMessageTextView, heading, keyHeading;
    String encodedMessage, whichCipher, clipboardLabel;
    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_symmetric_ciphers);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            whichCipher = extras.getString("whichCipher");
        }

        inputMessageEditText = findViewById(R.id.inputMessageCaesarCipherEncodeEditText);
        inputKeyEditText = findViewById(R.id.inputKeyCaesarCipherEncodeEditText);
        encodeBtn = findViewById(R.id.encodeCaesarCipherEncodeBtn);
        tapToCopyBtn = findViewById(R.id.copyToClipboardBtnCaesarEncrypt);
        afterEncodingConstraintLayout = findViewById(R.id.afterEncodingConstraintLayoutCaesarEncrypt);
        successMessageEncodedTextView = findViewById(R.id.successMessageEncodedCaesarTextView);
        encodedMessageTextView = findViewById(R.id.encodedMessageTextViewCaesarEncrypt);
        heading = findViewById(R.id.encodeSymmetricCiphersHeadingTextView);
        keyHeading = findViewById(R.id.keyEncodeSymmetricCiphersHeadingTextView);

        encodedMessage = null;

        successMessageEncodedTextView.setVisibility(View.INVISIBLE);
        afterEncodingConstraintLayout.setVisibility(View.INVISIBLE);

        // new Alert Loading Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        loadingDialog = builder.create();

        switch (whichCipher) {
            case "AES":
                heading.setText("AES Encode");
                keyHeading.setText("Secret Key:");
                successMessageEncodedTextView.setText("Message Encoded Successfully using AES !!!");
                inputKeyEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                clipboardLabel = "AESEncodedMessage_CryptoVault";
                break;
            case "CAESAR":
            default:
                heading.setText("Caesar Cipher Encode");
                keyHeading.setText("Key (1-25):");
                successMessageEncodedTextView.setText("Message Encoded Successfully using Caesar Cipher !!!");
                inputKeyEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                clipboardLabel = "CaesarEncodedMessage_CryptoVault";
                whichCipher = "CAESAR";
        }

        encodeBtn.setOnClickListener(view -> {
            successMessageEncodedTextView.setVisibility(View.INVISIBLE);
            afterEncodingConstraintLayout.setVisibility(View.INVISIBLE);
            String inputMessage = inputMessageEditText.getText().toString().trim();
            if (inputMessage.isEmpty() || inputKeyEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Empty Inputs !!!", Toast.LENGTH_SHORT).show();
            } else {
                String inputKey = inputKeyEditText.getText().toString();

                // start encoding:
                startEncodingFunction(inputMessage, inputKey);
            }
        });

        tapToCopyBtn.setOnClickListener(view -> {
            if (encodedMessage != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(clipboardLabel, encodedMessage);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startEncodingFunction(String message, String keyString) {
        StringBuilder cipherText = new StringBuilder();
        loadingDialog.show();
        new Thread(() -> {
            switch (whichCipher) {
                case "AES":
                    String salt_AES = "1234567890987654321";
                    SecretKey secretKey = getKeyFromPassword_AES(keyString, salt_AES);
//                    IvParameterSpec ivParameterSpec = generateIv_AES();
                    byte[] iv = {27, -70, 124, 46, -71, 116, -58, -24, 92, 125, -35, -92, 97, 49, -59, 121};
                    IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
                    String algorithm = "AES/CBC/PKCS5Padding";
                    cipherText.append(encrypt_AES(algorithm, message, secretKey, ivParameterSpec));
                    break;
                case "CAESAR":
                default:
                    // C = (M+K) % 26
                    int key = Integer.parseInt(keyString);
                    for (int i = 0; i < message.length(); i++) {
                        int c = message.charAt(i);
                        if (c >= 97 && c <= 122) {
                            c = startEncodingHelperCaesarCipher(c, key, 26, 97);
                        } else if (c >= 65 && c <= 90) {
                            c = startEncodingHelperCaesarCipher(c, key, 26, 65);
                        } else if (c >= 48 && c <= 57) {
                            c = startEncodingHelperCaesarCipher(c, key, 10, 48);
                        } else {
                            c += key;
                        }
                        cipherText.append((char) c);
                    }
            }

            runOnUiThread(() -> {
                // after the thread job is finished:

                encodedMessage = cipherText.toString();
                successMessageEncodedTextView.setVisibility(View.VISIBLE);
                afterEncodingConstraintLayout.setVisibility(View.VISIBLE);
                encodedMessageTextView.setText(encodedMessage);
                loadingDialog.dismiss();
            });
        }).start();
    }

    private int startEncodingHelperCaesarCipher(int c, int key, int mod, int initial) {
        c -= initial;
        c += key;
        c %= mod;
        c += initial;
        return c;
    }

    private String encrypt_AES(String algorithm, String input, SecretKey key,
                               IvParameterSpec iv) {
        Cipher cipher;
        byte[] cipherText = null;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return Base64.getEncoder().encodeToString(cipherText);
    }

    private SecretKey getKeyFromPassword_AES(String password, String salt) {
        SecretKey secret = null;
        SecretKeyFactory factory;
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return secret;
    }

}