package com.dora.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dora.myapplication.ImageSteganography.ImgSteg1;

public class MainActivity extends AppCompatActivity {

    Button imgStegBtn, gitHubRepoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgStegBtn = findViewById(R.id.imgStegBtn);
        gitHubRepoBtn = findViewById(R.id.githubRepoBtn);

        imgStegBtn.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ImgSteg1.class)));
        gitHubRepoBtn.setOnClickListener(view -> gitHubRepoExternalRedirect());

    }

    private void gitHubRepoExternalRedirect() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/namitgpta/Crypto-Vault-App")));
    }


}