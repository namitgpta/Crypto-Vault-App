package com.dora.myapplication.ImageSteganography;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;
import com.dora.myapplication.R;

import java.io.IOException;

public class DecodeImgSteg1 extends AppCompatActivity implements TextDecodingCallback {

    ImageView imageViewPreviewTop;
    EditText secretKeyInput;
    Button chooseImgBtn, decodeBtn;
    TextView successDecodingTextView, hiddenMessageTextView, messageOutputTextView;

    private Bitmap encodedImageBitmap;
//    ImageSteganography result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode_img_steg1);

        secretKeyInput = findViewById(R.id.keyDecodeImgSteg);
        chooseImgBtn = findViewById(R.id.chooseImgDecodeImgSteg);
        decodeBtn = findViewById(R.id.decodeProceedImgSteg);
        imageViewPreviewTop = findViewById(R.id.imagePreviewDecodeImgSteg);
        successDecodingTextView = findViewById(R.id.successTextViewDecodeImgSteg);
        hiddenMessageTextView = findViewById(R.id.hiddenMessageTextViewDecodeImgSteg);
        messageOutputTextView = findViewById(R.id.messageOutputDecodeImgSteg);

        hiddenMessageTextView.setVisibility(View.INVISIBLE);
        messageOutputTextView.setVisibility(View.INVISIBLE);
        successDecodingTextView.setVisibility(View.INVISIBLE);

        // Set no_image_selected as ImageView
        imageViewPreviewTop.setImageResource(R.drawable.no_img_selected);

        chooseImgBtn.setOnClickListener(view -> imageChooser());

        decodeBtn.setOnClickListener(view -> {
            String secret_key = secretKeyInput.getText().toString().trim();
            if (secret_key.isEmpty()) {
                secretKeyInput.setError("Empty Field");
            } else if (encodedImageBitmap == null) {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            } else {
                ImageSteganography imageSteganography = new ImageSteganography(secret_key,
                        encodedImageBitmap);
                TextDecoding textDecoding = new TextDecoding(DecodeImgSteg1.this,
                        DecodeImgSteg1.this);
                textDecoding.execute(imageSteganography);
            }

        });
    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
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
                        Uri selectedImageUri = data.getData();
                        try {
                            encodedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageViewPreviewTop.setImageBitmap(encodedImageBitmap);
                    }


                }
            });

    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do at the start of text decoding
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        //After the completion of text decoding.
//        this.result = result;
        hiddenMessageTextView.setVisibility(View.VISIBLE);
        if (result != null) {
            if (!result.isDecoded()) {
                hiddenMessageTextView.setText("No hidden message found !!!");
            } else {
                /* If result.isSecretKeyWrong() is true, it means that secret key provided is wrong. */
                if (!result.isSecretKeyWrong()) {
                    //set the message to the UI component.
                    hiddenMessageTextView.setText("Hidden Message: ");
                    messageOutputTextView.setText("" + result.getMessage());
                    messageOutputTextView.setVisibility(View.VISIBLE);
                    successDecodingTextView.setVisibility(View.VISIBLE);
                } else {
                    hiddenMessageTextView.setText("Wrong secret key");
                }
            }
        } else {
            //If result is null it means that bitmap is null
            hiddenMessageTextView.setText("Select Image First");
        }
    }

}