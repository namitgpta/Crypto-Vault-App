package com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers;

import static com.dora.myapplication.AwsRdsData.TABLE_NAME_AES;
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

import com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.adapter.SampleRecycler;
import com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.adapter.historySymmetric_RecyclerViewAdapter;
import com.dora.myapplication.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class HistorySymmetricCiphers extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private historySymmetric_RecyclerViewAdapter historySymmetric_recyclerViewAdapter;
    private ProgressBar progressBar;

    ArrayList<String> encodedValuesArray, timestampValuesArray, methodUsedArray;
//    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_symmetric_ciphers);

        encodedValuesArray = new ArrayList<>();
        timestampValuesArray = new ArrayList<>();
        methodUsedArray = new ArrayList<>();

        progressBar = findViewById(R.id.progressBarHistorySymmetricCiphers);
        progressBar.setVisibility(View.VISIBLE);

        recyclerViewHistory = findViewById(R.id.recyclerView_historySymmetricCiphers);
        recyclerViewHistory.setHasFixedSize(true);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));

        // Setting adapter to sample Recycler for avoiding unnecessary errors in log
        recyclerViewHistory.setAdapter(new SampleRecycler());

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

                ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME_AES + " ORDER BY _id DESC");
                while (rs.next()) {
                    encodedValuesArray.add(rs.getString("encodedString"));
                    methodUsedArray.add(rs.getString("method"));
                    timestampValuesArray.add(rs.getString("encodedDateTime"));
                }
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // after the job is finished:

                if (encodedValuesArray.isEmpty()) {
                    Toast.makeText(this, "Failed to fetch data from AWS RDS !!!", Toast.LENGTH_LONG).show();
                } else {
                    // setup adapter
                    historySymmetric_recyclerViewAdapter = new historySymmetric_RecyclerViewAdapter(HistorySymmetricCiphers.this, encodedValuesArray, timestampValuesArray, methodUsedArray);
                    recyclerViewHistory.setAdapter(historySymmetric_recyclerViewAdapter);
                }
                progressBar.setVisibility(View.INVISIBLE);
            });
        }).start();

    }

}