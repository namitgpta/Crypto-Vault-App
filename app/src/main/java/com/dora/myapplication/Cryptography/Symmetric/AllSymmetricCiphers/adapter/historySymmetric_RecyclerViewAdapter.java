package com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.adapter;

import static com.dora.myapplication.AwsRdsData.TABLE_NAME_AES;
import static com.dora.myapplication.AwsRdsData.password;
import static com.dora.myapplication.AwsRdsData.url;
import static com.dora.myapplication.AwsRdsData.username;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.HistorySymmetricCiphers;
import com.dora.myapplication.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;

public class historySymmetric_RecyclerViewAdapter extends RecyclerView.Adapter<historySymmetric_RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> encodedValuesArray, timestampValuesArray, methodUsedArray;
    //    Connection connection;
    boolean deleteEntrySuccessful;

    public historySymmetric_RecyclerViewAdapter(Context context, ArrayList<String> encodedValuesArray, ArrayList<String> timestampValuesArray, ArrayList<String> methodUsedArray) {
        this.context = context;
        this.encodedValuesArray = encodedValuesArray;
        this.timestampValuesArray = timestampValuesArray;
        this.methodUsedArray = methodUsedArray;
//        AwsConnectionClose();
//        this.connection = null;
        this.deleteEntrySuccessful = true;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_symmetric_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (encodedValuesArray.isEmpty()) return;

        String encodedTitleStringFull = encodedValuesArray.get(position);
        String dateTimeFullString = timestampValuesArray.get(position);
        String methodUsed = methodUsedArray.get(position);

        holder.copyToClipboard.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("historySymmetricCipher", encodedTitleStringFull);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
        });

        holder.deleteBtn.setOnClickListener(view -> {
            holder.deleteBtn.setVisibility(View.INVISIBLE);
            holder.progressBarDelete.setVisibility(View.VISIBLE);
            deleteEntryAwsRds(encodedTitleStringFull, holder);
        });

        String encodedTitleString = encodedTitleStringFull;
        if (encodedTitleString.length() > 25)
            encodedTitleString = encodedTitleString.substring(0, 25) + ".........";
        holder.title.setText(encodedTitleString);
        holder.methodUsedTextView.setText(methodUsed);
        String dateTimeStr = dateTimeFullString.substring(0, 11);
        int hrs = Integer.parseInt(dateTimeFullString.substring(11, 13));
        int mins = Integer.parseInt(dateTimeFullString.substring(14, 16));
        // to add +5:30 to the UTC timings stored in the table to convert to IST timings:
        hrs += 5;
        mins += 30;
        if (mins >= 60) {
            mins -= 60;
            hrs++;
        }
        if (hrs >= 24) {
            hrs -= 24;
        }
        if (hrs / 10 == 0) dateTimeStr += "0";
        dateTimeStr += String.valueOf(hrs);
        dateTimeStr += ":";
        if (mins / 10 == 0) dateTimeStr += "0";
        dateTimeStr += String.valueOf(mins);
        holder.date.setText(dateTimeStr);

//            Glide.with(context)
//                    .load(obj.getJSONArray("multimedia")
//                            .getJSONObject(2)
//                            .getString("url")).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return encodedValuesArray.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, date, methodUsedTextView;
        //        public ImageView imageView;
        public Button copyToClipboard, deleteBtn;
        public ProgressBar progressBarDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView1_row_hf);
            date = itemView.findViewById(R.id.textView3_row_hf);
            copyToClipboard = itemView.findViewById(R.id.read_more_hf);
            deleteBtn = itemView.findViewById(R.id.deleteBtnHistorySymmetric);
            methodUsedTextView = itemView.findViewById(R.id.textView2_row_hf);
            progressBarDelete = itemView.findViewById(R.id.progressBarHistorySymmetricRow);
//            imageView = itemView.findViewById(R.id.image_row_hf);

            progressBarDelete.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteEntryAwsRds(String encodedString, ViewHolder holder) {
        deleteEntrySuccessful = true;
        new Thread(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);
//                if (connection == null) {
//                    Log.d("ConnectionAWS", "Connected new created");
//                    connection = DriverManager.getConnection(url, username, password);
//                }
//                Statement statement = connection.createStatement();
                // add to RDS DB:
                // Prepared Statement ? can be used only to set the VALUES.
                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM " + TABLE_NAME_AES + " WHERE encodedString=?");
                preparedStatement.setString(1, encodedString);
                preparedStatement.executeUpdate();
                connection.close();

            } catch (Exception e) {
                deleteEntrySuccessful = false;
                e.printStackTrace();
            }

            ((HistorySymmetricCiphers) context).runOnUiThread(() -> {
                // after the job is finished:
                if (!deleteEntrySuccessful) {
                    Toast.makeText(context, "Error deleting !!!", Toast.LENGTH_LONG).show();
                } else {
                    holder.deleteBtn.setText("Deleted");
                    holder.deleteBtn.setTextColor(context.getColor(R.color.grey));
                    holder.deleteBtn.setClickable(false);
                }
                holder.progressBarDelete.setVisibility(View.INVISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);
            });
        }).start();

    }

//    private void AwsConnectionClose() {
//        if (connection != null) {
//            try {
//                connection.close();
//                Log.d("ConnectionAWS", "Connected AWS closed");
//            } catch (SQLException throwables) {
//                throwables.printStackTrace();
//            }
//        }
//    }

}
