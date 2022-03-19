package com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers;

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

    public static final String DATABASE_NAME = "crypto_vault";
    public static final String url = "jdbc:mysql://crypto-vault-rds-1.ce3udfzrdpxd.ap-south-1.rds.amazonaws.com:3306/" +
            DATABASE_NAME + "?autoReconnect=true&useSSL=false";
    public static final String username = "admin", password = "namitVit$83";
    public static final String TABLE_NAME = "AES";

    ArrayList<String> encodedValuesArray, timestampValuesArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_symmetric_ciphers);

        encodedValuesArray = new ArrayList<>();
        timestampValuesArray = new ArrayList<>();

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
            //do your work

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
                Statement statement = connection.createStatement();

                ResultSet rs = statement.executeQuery("SELECT * FROM " + TABLE_NAME);
                while (rs.next()) {
                    encodedValuesArray.add(rs.getString("encodedString"));
                    timestampValuesArray.add(rs.getString("encodedDateTime"));
                }

                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> {
                // after the job is finished:

                if (encodedValuesArray.isEmpty() || timestampValuesArray.isEmpty()) {
                    Toast.makeText(this, "Failed to fetch data from AWS RDS !!!", Toast.LENGTH_LONG).show();
                } else {
                    // setup adapter
                    historySymmetric_recyclerViewAdapter = new historySymmetric_RecyclerViewAdapter(HistorySymmetricCiphers.this, encodedValuesArray, timestampValuesArray);
                    recyclerViewHistory.setAdapter(historySymmetric_recyclerViewAdapter);
                }
                progressBar.setVisibility(View.INVISIBLE);
            });
        }).start();

    }

//    public static void addTemp(String name_str, String place_str) {
//        new Thread(() -> {
//            try {
//                Class.forName("com.mysql.jdbc.Driver");
//                Connection connection = DriverManager.getConnection(url, username, password);
//                Statement statement = connection.createStatement();
//                // add to RDS DB:
//
//                statement.execute("INSERT INTO " + TABLE_NAME + "(name, place) VALUES('" + name_str + "', '" + place_str + "')");
//
//                connection.close();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
}