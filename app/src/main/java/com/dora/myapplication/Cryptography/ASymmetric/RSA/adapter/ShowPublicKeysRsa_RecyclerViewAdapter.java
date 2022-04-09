package com.dora.myapplication.Cryptography.ASymmetric.RSA.adapter;

import static com.dora.myapplication.AwsRdsData.password;
import static com.dora.myapplication.AwsRdsData.url;
import static com.dora.myapplication.AwsRdsData.username;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dora.myapplication.Cryptography.ASymmetric.RSA.RsaShowPublicKeys;
import com.dora.myapplication.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Objects;

public class ShowPublicKeysRsa_RecyclerViewAdapter extends RecyclerView.Adapter<ShowPublicKeysRsa_RecyclerViewAdapter.ViewHolder2> {
    private final Context context;
    private final ArrayList<String> keyBitsArray, timestampsArray, namesArray;
    private final ArrayList<Integer> idArray;
    private final ArrayList<byte[]> publicKeysBytesArray;
    //    Connection connection;
    boolean deleteEntrySuccessful, keysSavedOrNot;
    AlertDialog loadingDialog;

    public ShowPublicKeysRsa_RecyclerViewAdapter(Context context, ArrayList<String> keyBitsArray, ArrayList<String> timestampsArray, ArrayList<Integer> idArray, ArrayList<byte[]> publicKeysBytesArray, ArrayList<String> namesArray) {
        this.context = context;
        this.keyBitsArray = keyBitsArray;
        this.timestampsArray = timestampsArray;
        this.idArray = idArray;
        this.publicKeysBytesArray = publicKeysBytesArray;
        this.namesArray = namesArray;
//        AwsConnectionClose();
//        this.connection = null;
        this.deleteEntrySuccessful = true;
        this.keysSavedOrNot = true;

        // new Alert Loading Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_layout_dialog);
        loadingDialog = builder.create();
    }

    @NonNull
    @Override
    public ShowPublicKeysRsa_RecyclerViewAdapter.ViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.show_public_keys_rsa_row, viewGroup, false);
        return new ShowPublicKeysRsa_RecyclerViewAdapter.ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShowPublicKeysRsa_RecyclerViewAdapter.ViewHolder2 holder, int position) {
        if (keyBitsArray.isEmpty()) return;

        String dateTimeFullString = timestampsArray.get(position);
        String keyBits = keyBitsArray.get(position);
        String name = namesArray.get(position);
        int id = idArray.get(position);

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

        String finalDateTimeStr = dateTimeStr;
        holder.saveBtn.setOnClickListener(v -> {
            try {
                saveKeysScopedStorage(keyBits, finalDateTimeStr, publicKeysBytesArray.get(position), name);
            } catch (Exception e) {
                e.printStackTrace();
            }
            loadingDialog.show();
//            Toast.makeText(context, " !!!", Toast.LENGTH_SHORT).show();
        });

        holder.deleteBtn.setOnClickListener(view -> {
            holder.deleteBtn.setVisibility(View.INVISIBLE);
            holder.progressBarDelete.setVisibility(View.VISIBLE);
            deleteEntryAwsRds(id, holder);
        });

        String keyBitsString = name + " - " + keyBits + " bits";
        holder.keyBitsTextView.setText(keyBitsString);

//            Glide.with(context)
//                    .load(obj.getJSONArray("multimedia")
//                            .getJSONObject(2)
//                            .getString("url")).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return keyBitsArray.size();
    }

    public static class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView date, keyBitsTextView;
        //        public ImageView imageView;
        public Button saveBtn, deleteBtn;
        public ProgressBar progressBarDelete;

        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
//            title = itemView.findViewById(R.id.textView1_rsa_row_hf);
            date = itemView.findViewById(R.id.textView_dateTime_showRsaPublicKeys_row);
            saveBtn = itemView.findViewById(R.id.saveBtn_ShowPublicKeysRsa_row);
            deleteBtn = itemView.findViewById(R.id.deleteBtn_ShowPublicKeysRsa_row);
            keyBitsTextView = itemView.findViewById(R.id.textView_keyBits_showRsaPublicKeys_row);
            progressBarDelete = itemView.findViewById(R.id.progressBarShowPublicKeysRsa_row);
//            imageView = itemView.findViewById(R.id.image_row_hf);

            progressBarDelete.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteEntryAwsRds(int id, ViewHolder2 holder) {
        deleteEntrySuccessful = true;
        new Thread(() -> {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(url, username, password);

                PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM RSA" + " WHERE _id=?");
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                connection.close();

            } catch (Exception e) {
                deleteEntrySuccessful = false;
                e.printStackTrace();
            }

            ((RsaShowPublicKeys) context).runOnUiThread(() -> {
                // after the job is finished:
                if (!deleteEntrySuccessful) {
                    Toast.makeText(context, "Error deleting the key!!!", Toast.LENGTH_LONG).show();
                } else {
                    String deletedStr = "deleted";
                    holder.deleteBtn.setText(deletedStr);
                    holder.deleteBtn.setTextColor(context.getColor(R.color.grey));
                    holder.deleteBtn.setClickable(false);
                }
                holder.progressBarDelete.setVisibility(View.INVISIBLE);
                holder.deleteBtn.setVisibility(View.VISIBLE);
            });
        }).start();

    }

    private void saveKeysScopedStorage(String keyLength, String dateTime, byte[] publicKeyBytes, String name) {
        keysSavedOrNot = true;

        new Thread(() -> {
            OutputStream os = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ContentResolver contentResolver = context.getContentResolver();
                    ContentValues contentValues = new ContentValues();

                    // for saving public key:
//                    LocalDateTime currDateTime = java.time.LocalDateTime.now();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Online_Public_Key_" + name + "_" + keyLength + "_" + dateTime + ".TXT");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                    contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + File.separator + "CryptoVault Keys" + File.separator + "Online Public Keys");
                    Uri keyUri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                    os = contentResolver.openOutputStream(Objects.requireNonNull(keyUri));
                    os.write(publicKeyBytes);
//                    try (FileOutputStream fos = new FileOutputStream("public.key")) {
//                        fos.write(publicKey.getEncoded());
//                    }
                    Objects.requireNonNull(os);

                    // Toasts not allowed in threads
                }
            } catch (Exception e) {
                keysSavedOrNot = false;
                Log.e("Online Public Key Save Error: ", e.getMessage());
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ((RsaShowPublicKeys) context).runOnUiThread(() -> {
                // after the thread job is finished:
                if (keysSavedOrNot) {
                    Toast.makeText(context, "Public Key Saved to Downloads!!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Unable to Save the Key. Check logs for error", Toast.LENGTH_LONG).show();
                }
                loadingDialog.dismiss();
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
