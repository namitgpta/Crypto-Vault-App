package com.dora.myapplication.ImageSteganography;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextEncodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextEncoding;
import com.dora.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class EncodeImgSteg1 extends AppCompatActivity implements TextEncodingCallback {

    ImageView imageViewPreviewTop;
    EditText messageInput, secretKeyInput;
    Button chooseImgBtn, encodeProceedBtn, saveEncodedImgBtn;
    TextView successEncodingTextView;

    private Bitmap selectedImageBitmap;
    private Bitmap encodedImageBitmap;
    boolean imageSavedOrNot = true;
//    ImageSteganography result;

    AlertDialog saveLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode_img_steg1);

        messageInput = findViewById(R.id.messageEncodeImgSteg);
        secretKeyInput = findViewById(R.id.keyEncodeImgSteg);
        chooseImgBtn = findViewById(R.id.chooseImgEncodeImgSteg);
        encodeProceedBtn = findViewById(R.id.encodeProceedImgSteg);
        imageViewPreviewTop = findViewById(R.id.imagePreviewEncodeImgSteg);
        successEncodingTextView = findViewById(R.id.successTextViewEncodeImgSteg);
        saveEncodedImgBtn = findViewById(R.id.saveEncodedImgSteg);

        // Request the old deprecated Read and Write permissions, if not granted already:
//        checkAndRequestPermissions();

        // Set no_image_selected as ImageView
        imageViewPreviewTop.setImageResource(R.drawable.no_img_selected);

        chooseImgBtn.setOnClickListener(view -> imageChooser());

        // new Alert Loading Dialog for saving Encoded image;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        saveLoadingDialog = builder.create();

        encodeProceedBtn.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            String secret_key = secretKeyInput.getText().toString().trim();
            if (message.isEmpty()) {
                messageInput.setError("Empty Field");
            } else if (secret_key.isEmpty()) {
                secretKeyInput.setError("Empty Field");
            } else if (selectedImageBitmap == null) {
                Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            } else {
                ImageSteganography imageSteganography = new ImageSteganography(message,
                        secret_key,
                        selectedImageBitmap);
                TextEncoding textEncoding = new TextEncoding(EncodeImgSteg1.this,
                        EncodeImgSteg1.this);
                textEncoding.execute(imageSteganography);
            }

        });

        saveEncodedImgBtn.setOnClickListener(view -> {
            saveImageScopedStorage();
            saveLoadingDialog.show();
        });

        // Save image button
        // This saving the encoded image part is most crucial for the Image Steganography to work properly:
//        saveEncodedImgBtn.setOnClickListener(view -> {
//            final Bitmap imgToSave = encodedImageBitmap;
//            Thread PerformEncoding = new Thread(() -> saveToInternalStorage(imgToSave));
//            saveLoadingDialog.show();
//
//            PerformEncoding.start();
//        });
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
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageViewPreviewTop.setImageBitmap(selectedImageBitmap);
                        successEncodingTextView.setVisibility(View.INVISIBLE);
                    }


                }
            });

    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do at the start of text encoding

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {
        //After the completion of text encoding.
//        this.result = result;
        if (result != null && result.isEncoded()) {

            //encrypted image bitmap is extracted from result object
            encodedImageBitmap = result.getEncoded_image();

            //set text and image to the UI component.
            successEncodingTextView.setVisibility(View.VISIBLE);
            saveEncodedImgBtn.setVisibility(View.VISIBLE);
            imageViewPreviewTop.setImageBitmap(encodedImageBitmap);
        }
    }

    private void saveImageScopedStorage() {
        imageSavedOrNot = true;

        new Thread(() -> {
            OutputStream fos = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();

                    // Saving encoded image as PNG results in successful encoding and decoding.
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Encoded_Stego_Img_" + java.time.LocalDateTime.now() + ".PNG");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/PNG");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + "Crypto Vault App");
                    Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    fos = contentResolver.openOutputStream(Objects.requireNonNull(imageUri));
                    encodedImageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    Objects.requireNonNull(fos);

                    // Toasts not allowed in threads
                }
            } catch (Exception e) {
                imageSavedOrNot = false;
                Log.e("Stego Image Save Error: ", e.getMessage());
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                // after the thread job is finished:
                imageSavedToastFunction();
                saveLoadingDialog.dismiss();
            });
        }).start();

    }

    public void imageSavedToastFunction() {
        if (imageSavedOrNot) {
            Toast.makeText(this, "Image Saved to Pictures!!!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Image not saved. Check logs for error", Toast.LENGTH_LONG).show();
        }
    }


//    private void saveToInternalStorage(Bitmap bitmapImage) {
//        OutputStream fOut;
//        //Integer counter = 0;
//        File file = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS), "Encoded" + ".PNG"); // the File to save ,
//        try {
//            fOut = new FileOutputStream(file);
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
//            fOut.flush(); // Not really required
//            fOut.close(); // do not forget to close the stream
//            successEncodingTextView.post(() -> {
////                    save.dismiss();
//                saveLoadingDialog.dismiss();
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void checkAndRequestPermissions() {
//        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
//        }
//        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
//            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if (!listPermissionsNeeded.isEmpty()) {
//            String[] listPermissionsNeededStringArr = new String[listPermissionsNeeded.size()];
//            listPermissionsNeededStringArr = listPermissionsNeeded.toArray(listPermissionsNeededStringArr);
//            ActivityCompat.requestPermissions(this, listPermissionsNeededStringArr, 1);
//        }
//    }
}