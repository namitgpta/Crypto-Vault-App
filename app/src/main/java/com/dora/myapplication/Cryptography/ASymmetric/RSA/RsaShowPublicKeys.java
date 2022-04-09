package com.dora.myapplication.Cryptography.ASymmetric.RSA;

import static com.dora.myapplication.AwsRdsData.password;
import static com.dora.myapplication.AwsRdsData.url;
import static com.dora.myapplication.AwsRdsData.username;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dora.myapplication.Cryptography.ASymmetric.RSA.adapter.ShowPublicKeysRsa_RecyclerViewAdapter;
import com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.adapter.SampleRecycler;
import com.dora.myapplication.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class RsaShowPublicKeys extends AppCompatActivity {

    private RecyclerView recyclerViewShowPublicKeys;
    private ShowPublicKeysRsa_RecyclerViewAdapter showPublicKeysRsa_recyclerViewAdapter;
    private ProgressBar progressBar;

    ArrayList<String> timestampsArray, keyBitsArray, namesArray;
    ArrayList<Integer> idArray;
    ArrayList<byte[]> publicKeysBytesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rsa_show_public_keys);

        timestampsArray = new ArrayList<>();
        keyBitsArray = new ArrayList<>();
        idArray = new ArrayList<>();
        publicKeysBytesArray= new ArrayList<>();
        namesArray= new ArrayList<>();

        progressBar = findViewById(R.id.progressBarShowPublicKeysRsa);
        progressBar.setVisibility(View.VISIBLE);

        recyclerViewShowPublicKeys = findViewById(R.id.recyclerView_showPublicKeysRsa);
        recyclerViewShowPublicKeys.setHasFixedSize(true);
        recyclerViewShowPublicKeys.setLayoutManager(new LinearLayoutManager(this));

        // Setting adapter to sample Recycler for avoiding unnecessary errors in log
        recyclerViewShowPublicKeys.setAdapter(new SampleRecycler());
        try {
            utilFun();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void utilFun() throws SQLException {
        new Thread(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * FROM RSA" + " ORDER BY _id DESC");
                while (rs.next()) {
                    idArray.add(rs.getInt("_id"));
                    publicKeysBytesArray.add(rs.getBytes("publicKey"));
                    keyBitsArray.add(rs.getString("keyBits"));
                    timestampsArray.add(rs.getString("dateTimeStamp"));
                    namesArray.add(rs.getString("name"));
                }
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // after the job is finished:

                if (keyBitsArray.isEmpty()) {
                    Toast.makeText(this, "Failed to fetch data from AWS RDS !!!", Toast.LENGTH_LONG).show();
                } else {
                    // setup adapter
                    showPublicKeysRsa_recyclerViewAdapter = new ShowPublicKeysRsa_RecyclerViewAdapter(RsaShowPublicKeys.this, keyBitsArray, timestampsArray, idArray, publicKeysBytesArray, namesArray);
                    recyclerViewShowPublicKeys.setAdapter(showPublicKeysRsa_recyclerViewAdapter);
                }
                progressBar.setVisibility(View.INVISIBLE);
            });
        }).start();

    }
}