package com.dora.myapplication.Cryptography.ASymmetric.RSA;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dora.myapplication.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaDecrypt extends AppCompatActivity {

    Button chooseKeyBtn, startDecryptBtn, tapToCopyBtn;
    byte[] privateKeyBytes;
    PrivateKey privateKey;
    TextView successKeyImportTextView, successMessageDecryptedTextView, decodedMessageTextView;
    EditText messageToDecryptEditText;
    boolean decryptionSuccessFlag;
    ConstraintLayout afterDecodingConstraintLayout;

    String decodedMessage;

    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_decrypt);

        chooseKeyBtn = findViewById(R.id.chooseRsaDecryptBtn);
        startDecryptBtn = findViewById(R.id.startDecryptBtnRsaDecrypt);
        tapToCopyBtn = findViewById(R.id.copyToClipboardBtnRsaDecrypt);
        successKeyImportTextView = findViewById(R.id.successKeyImportRsaDecryptTextView);
        successMessageDecryptedTextView = findViewById(R.id.successMessageDecryptedRsaDecryptTextView);
        decodedMessageTextView = findViewById(R.id.decodedMessageTextViewRsaDecrypt);
        messageToDecryptEditText = findViewById(R.id.editTextMessageRsaDecrypt);
        afterDecodingConstraintLayout = findViewById(R.id.afterDecodingConstraintLayoutRsaDecrypt);

        privateKeyBytes = null;
        privateKey = null;
        decryptionSuccessFlag = false;
        decodedMessage = null;

        successKeyImportTextView.setVisibility(View.INVISIBLE);
        successMessageDecryptedTextView.setVisibility(View.INVISIBLE);
        afterDecodingConstraintLayout.setVisibility(View.INVISIBLE);

        // new Alert Loading Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        loadingDialog = builder.create();

        chooseKeyBtn.setOnClickListener(view -> fileChooser());
        startDecryptBtn.setOnClickListener(view -> {
            successMessageDecryptedTextView.setVisibility(View.INVISIBLE);
            afterDecodingConstraintLayout.setVisibility(View.INVISIBLE);
            decodedMessage = null;

            String messageToDecrypt = messageToDecryptEditText.getText().toString().trim();
            if (privateKeyBytes == null) {
                Toast.makeText(this, "Choose your Private Key !!!", Toast.LENGTH_SHORT).show();
            } else if (messageToDecrypt.isEmpty()) {
                Toast.makeText(this, "Enter the Encoded Message to Decrypt !!!", Toast.LENGTH_SHORT).show();
            } else {
                startDecryptionRSA(messageToDecrypt);
            }
        });

        tapToCopyBtn.setOnClickListener(view -> {
            if (decodedMessage != null) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("RSADecodedMessage_CryptoVault", decodedMessage);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(this, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fileChooser() {
        Intent i = new Intent();
        i.setType("text/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        launchSomeActivity.launch(i);
    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // your operation....
                    if (data != null && data.getData() != null) {
                        Uri keyUri = data.getData();
                        InputStream iStream;
                        try {
                            iStream = getContentResolver().openInputStream(keyUri);
                            privateKeyBytes = getBytes(iStream);
                            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
                            privateKey = keyFactory.generatePrivate(privateKeySpec);
                            successKeyImportTextView.setVisibility(View.VISIBLE);
                        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
                            Toast.makeText(this, "Error importing the key !!!", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }


                }
            });

    // helper function for key file chooser
    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void startDecryptionRSA(String messageToDecrypt) {
        loadingDialog.show();
        new Thread(() -> {
            try {
                Cipher decryptCipher = Cipher.getInstance("RSA");
                decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);

                // Remember we have used strictly, Base64 encoding and decoding, for conversions in between String and byte array.
                byte[] encryptedMessageBytes = Base64.getDecoder().decode(messageToDecrypt);

                byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
                decodedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
                decryptionSuccessFlag = true;

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                decryptionSuccessFlag = false;
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // after the thread job is finished:

                if (decryptionSuccessFlag) {
                    successMessageDecryptedTextView.setVisibility(View.VISIBLE);
                    afterDecodingConstraintLayout.setVisibility(View.VISIBLE);
                    if (decodedMessage == null) {
                        decodedMessage = "None";
                        Toast.makeText(this, "Some Internal Error while decrypting!!!. Check logs for error", Toast.LENGTH_LONG).show();
                    }
                    decodedMessageTextView.setText(decodedMessage);
                } else {
                    Toast.makeText(this, "Unable to Decrypt !!!. Check logs for error", Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            });
        }).start();
    }
}