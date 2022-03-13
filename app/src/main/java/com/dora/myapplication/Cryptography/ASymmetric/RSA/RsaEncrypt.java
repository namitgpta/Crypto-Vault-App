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
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaEncrypt extends AppCompatActivity {

    Button chooseKeyBtn, startEncryptBtn, tapToCopyBtn;
    byte[] publicKeyBytes;
    PublicKey publicKey;
    TextView successKeyImportTextView, successMessageEncryptedTextView, encodedMessageTextView, encodedMessageHeadingTextView;
    EditText messageToEncryptEditText;
    boolean encryptionSuccessFlag;
    ConstraintLayout afterEncodingConstraintLayout;
    String encodedMessage;
    AlertDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_encrypt);

        chooseKeyBtn = findViewById(R.id.chooseRsaEncryptBtn);
        startEncryptBtn = findViewById(R.id.startEncryptBtnRsaEncrypt);
        tapToCopyBtn = findViewById(R.id.copyToClipboardBtnRsaEncrypt);
        successKeyImportTextView = findViewById(R.id.successKeyImportRsaEncryptTextView);
        successMessageEncryptedTextView = findViewById(R.id.successMessageEncryptedRsaEncryptTextView);
        encodedMessageTextView = findViewById(R.id.encodedMessageTextViewRsaEncrypt);
        messageToEncryptEditText = findViewById(R.id.editTextMessageRsaEncrypt);
        afterEncodingConstraintLayout = findViewById(R.id.afterEncodingConstraintLayoutRsaEncrypt);
        encodedMessageHeadingTextView = findViewById(R.id.encodedMessageHeadingTextViewRsaEncrypt);

        publicKeyBytes = null;
        publicKey = null;
        encryptionSuccessFlag = false;
        encodedMessage = null;

        successKeyImportTextView.setVisibility(View.INVISIBLE);
        successMessageEncryptedTextView.setVisibility(View.INVISIBLE);
        afterEncodingConstraintLayout.setVisibility(View.INVISIBLE);

        // new Alert Loading Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        loadingDialog = builder.create();

        chooseKeyBtn.setOnClickListener(view -> fileChooser());
        startEncryptBtn.setOnClickListener(view -> {
            successMessageEncryptedTextView.setVisibility(View.INVISIBLE);
            afterEncodingConstraintLayout.setVisibility(View.INVISIBLE);
            encodedMessage = null;

            String messageToEncrypt = messageToEncryptEditText.getText().toString().trim();
            if (publicKeyBytes == null) {
                Toast.makeText(this, "Choose the Public Key !!!", Toast.LENGTH_SHORT).show();
            } else if (messageToEncrypt.isEmpty()) {
                Toast.makeText(this, "Enter Message to Encrypt !!!", Toast.LENGTH_SHORT).show();
            } else {
                startEncryptionRSA(messageToEncrypt);
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

//    private void openFile(Uri pickerInitialUri) {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("text/plain");
//
//        // Optionally, specify a URI for the file that should appear in the
//        // system file picker when it loads.
//        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
//
//        startActivityForResult(intent, PICK_PDF_FILE);
//    }

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
                            publicKeyBytes = getBytes(iStream);
                            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
                            publicKey = keyFactory.generatePublic(publicKeySpec);
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

    private void startEncryptionRSA(String messageToEncrypt) {
        loadingDialog.show();
        new Thread(() -> {
            try {
                Cipher encryptCipher = Cipher.getInstance("RSA");
                encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);

                byte[] secretMessageBytes = messageToEncrypt.getBytes(StandardCharsets.UTF_8);
                byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);

                // Remember we have used strictly, Base64 encoding and decoding, for conversions in between String and byte array.
                encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
                encryptionSuccessFlag = true;

            } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
                encryptionSuccessFlag = false;
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // after the thread job is finished:

                if (encryptionSuccessFlag) {
                    successMessageEncryptedTextView.setVisibility(View.VISIBLE);
                    afterEncodingConstraintLayout.setVisibility(View.VISIBLE);
                    if (encodedMessage == null) {
                        encodedMessage = "None";
                        Toast.makeText(this, "Some Internal Error while encrypting!!!. Check logs for error", Toast.LENGTH_LONG).show();
                    }
                    if (encodedMessage.length() > 200) {
                        String shortenedEncodedMessage = encodedMessage.substring(0, 200);
                        shortenedEncodedMessage += ".........................";
                        encodedMessageTextView.setText(shortenedEncodedMessage);
                        String heading = "Encoded Message: " + encodedMessage.length() + " bits";
                        encodedMessageHeadingTextView.setText(heading);
                    } else {
                        encodedMessageTextView.setText(encodedMessage);
                    }
                } else {
                    Toast.makeText(this, "Unable to Encrypt !!!. Check logs for error", Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
            });
        }).start();
    }

}