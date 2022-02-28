package com.dora.myapplication.Cryptography.ASymmetric.RSA;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dora.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.Objects;

public class RsaKeyGeneration extends AppCompatActivity {

    int keyLength;
    Button generateBtn, saveKeysBtn;
    EditText customKeyLengthEditText;
    TextView keysGenerateSuccessTextView;

    AlertDialog loadingDialog;

    PrivateKey privateKey;
    PublicKey publicKey;

    boolean keysSavedOrNot = true;
    boolean keysGeneratedOrNot = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_key_generation);

        generateBtn = findViewById(R.id.generateKeyRsaButton);
        saveKeysBtn = findViewById(R.id.saveKeysRsaBtn);
        customKeyLengthEditText = findViewById(R.id.editTextKeyLength);
        keysGenerateSuccessTextView = findViewById(R.id.keysGenerateSuccessRsaTextView);

        keysGenerateSuccessTextView.setVisibility(View.INVISIBLE);

        privateKey = null;
        publicKey = null;

        // new Alert Loading Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        loadingDialog = builder.create();

        generateBtn.setOnClickListener(view -> {
            keysGenerateSuccessTextView.setVisibility(View.INVISIBLE);
            keyLength = 256; //set default Key Length for RSA
            String customKeyLength = customKeyLengthEditText.getText().toString().trim();
            if (!customKeyLength.isEmpty()) {
                try {
                    if (Integer.parseInt(customKeyLength) < 256) {
                        Toast.makeText(this, "Key Length < 256 not supported. Using the default value", Toast.LENGTH_LONG).show();
                    } else {
                        keyLength = Integer.parseInt(customKeyLength);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Invalid Key Length. Using the default value", Toast.LENGTH_SHORT).show();
                }
            }

            loadingDialog.show();
            new Thread(() -> {
                try {
                    KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                    generator.initialize(keyLength);
                    KeyPair pair = generator.generateKeyPair();

                    privateKey = pair.getPrivate();
                    publicKey = pair.getPublic();

                    keysGeneratedOrNot = true;

                } catch (NoSuchAlgorithmException e) {
                    // | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException
                    keysGeneratedOrNot = false;
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    // after the thread job is finished:
                    loadingDialog.dismiss();
                    if (keysGeneratedOrNot) {
                        keysGenerateSuccessTextView.setVisibility(View.VISIBLE);
                    } else
                        Toast.makeText(this, "Error in Key Generation. Check logs", Toast.LENGTH_LONG).show();
                });
            }).start();
        });

        saveKeysBtn.setOnClickListener(view -> {
            if (privateKey == null) {
                Toast.makeText(this, "First Generate the Keys....", Toast.LENGTH_SHORT).show();
            } else {
                saveKeysScopedStorage();
                loadingDialog.show();
            }

        });


    }

    public void saveKeysScopedStorage() {
        keysSavedOrNot = true;

        new Thread(() -> {
            OutputStream os = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();

                    // for saving public key:
                    LocalDateTime currDateTime = java.time.LocalDateTime.now();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Public_Key_" + keyLength + "_" + currDateTime + ".TXT");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + "CryptoVault Keys");

                    Uri keyUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    os = contentResolver.openOutputStream(Objects.requireNonNull(keyUri));
                    os.write(publicKey.getEncoded());
//                    try (FileOutputStream fos = new FileOutputStream("public.key")) {
//                        fos.write(publicKey.getEncoded());
//                    }
                    Objects.requireNonNull(os);

                    // for saving private key:
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Private_Key_" + keyLength + "_" + currDateTime + ".TXT");
                    keyUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    os = contentResolver.openOutputStream(Objects.requireNonNull(keyUri));
                    os.write(privateKey.getEncoded());
//                    try (FileOutputStream fos = new FileOutputStream("public.key")) {
//                        fos.write(publicKey.getEncoded());
//                    }
                    Objects.requireNonNull(os);

                    // Toasts not allowed in threads
                }
            } catch (Exception e) {
                keysSavedOrNot = false;
                Log.e("Stego Image Save Error: ", e.getMessage());
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                // after the thread job is finished:
                keysSavedToastFunction();
                loadingDialog.dismiss();
            });

        }).start();
    }

    public void keysSavedToastFunction() {
        if (keysSavedOrNot) {
            Toast.makeText(this, "Keys Saved to Downloads!!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Unable to Save the Keys. Check logs for error", Toast.LENGTH_LONG).show();
        }
    }

}