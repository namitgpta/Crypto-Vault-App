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

public class DecodeSymmetricCiphers extends AppCompatActivity {

    EditText inputEncodedMessageEditText, inputKeyEditText;
    Button decodeBtn, tapToCopyBtn;
    ConstraintLayout afterDecodingConstraintLayout;
    TextView successMessageDecodedTextView, decodedMessageTextView, heading, keyHeading;
    String decodedMessage, whichCipher, clipboardLabel;
    AlertDialog loadingDialog;
    boolean invalidKey = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_symmetric_ciphers);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            whichCipher = extras.getString("whichCipher");
        }

        inputEncodedMessageEditText = findViewById(R.id.inputMessageCaesarCipherDecodeEditText);
        inputKeyEditText = findViewById(R.id.inputKeyCaesarCipherDecodeEditText);
        decodeBtn = findViewById(R.id.decodeCaesarCipherDecodeBtn);
        tapToCopyBtn = findViewById(R.id.copyToClipboardBtnCaesarDecrypt);
        afterDecodingConstraintLayout = findViewById(R.id.afterDecodingConstraintLayoutCaesarDecrypt);
        successMessageDecodedTextView = findViewById(R.id.successMessageDecodedCaesarTextView);
        decodedMessageTextView = findViewById(R.id.decodedMessageTextViewCaesarDecrypt);
        heading = findViewById(R.id.decodeSymmetricCiphersHeadingTextView);
        keyHeading = findViewById(R.id.keyDecodeSymmetricCiphersHeadingTextView);

        decodedMessage = null;
        invalidKey = false;

        successMessageDecodedTextView.setVisibility(View.INVISIBLE);
        afterDecodingConstraintLayout.setVisibility(View.INVISIBLE);

        // new Alert Loading Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        loadingDialog = builder.create();

        switch (whichCipher) {
            case "AES":
                heading.setText("AES Decode");
                keyHeading.setText("Secret Key:");
                successMessageDecodedTextView.setText("Message Decoded Successfully using AES !!!");
                inputKeyEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                clipboardLabel = "AESDecodedMessage_CryptoVault";
                break;
            case "CAESAR":
            default:
                heading.setText("Caesar Cipher Decode");
                keyHeading.setText("Key (1-25):");
                successMessageDecodedTextView.setText("Message Decoded Successfully using Caesar Cipher !!!");
                inputKeyEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                clipboardLabel = "CaesarDecodedMessage_CryptoVault";
                whichCipher = "CAESAR";
        }

        decodeBtn.setOnClickListener(view -> {
            successMessageDecodedTextView.setVisibility(View.INVISIBLE);
            afterDecodingConstraintLayout.setVisibility(View.INVISIBLE);
            invalidKey = false;
            String inputEncodedMessage = inputEncodedMessageEditText.getText().toString().trim();
            if (inputEncodedMessage.isEmpty() || inputKeyEditText.getText().toString().isEmpty()) {
                Toast.makeText(this, "Empty Inputs !!!", Toast.LENGTH_SHORT).show();
            } else {
                String inputKey = inputKeyEditText.getText().toString();

                // start encoding:
                startDecodingFunction(inputEncodedMessage, inputKey);
            }
        });

        tapToCopyBtn.setOnClickListener(view -> {
            if (decodedMessage != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(clipboardLabel, decodedMessage);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startDecodingFunction(String encodedMessage, String keyString) {
        StringBuilder plainText = new StringBuilder();
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
                    plainText.append(decrypt_AES(algorithm, encodedMessage, secretKey, ivParameterSpec));
                    break;
                case "CAESAR":
                default:
                    // M = (C-K) % 26
                    int key = Integer.parseInt(keyString);
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
            }

            runOnUiThread(() -> {
                // after the thread job is finished:

                if (!invalidKey) {
                    successMessageDecodedTextView.setVisibility(View.VISIBLE);
                }
                decodedMessage = plainText.toString();
                afterDecodingConstraintLayout.setVisibility(View.VISIBLE);
                decodedMessageTextView.setText(decodedMessage);
                loadingDialog.dismiss();
            });
        }).start();
    }

    private int startDecodingHelper(int c, int key, int mod, int initial) {
        c -= initial;
        c -= key;
        c %= mod;
        if (c < 0) c = mod + c;
        c += initial;
        return c;
    }

    private String decrypt_AES(String algorithm, String cipherText, SecretKey key,
                               IvParameterSpec iv) {
        Cipher cipher;
        byte[] plainText = null;
        try {
            cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
            plainText = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidKeyException | BadPaddingException e) {
            e.printStackTrace();
            invalidKey = true;
            return "Invalid Key entered";
        }
        return new String(plainText, StandardCharsets.UTF_8);
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