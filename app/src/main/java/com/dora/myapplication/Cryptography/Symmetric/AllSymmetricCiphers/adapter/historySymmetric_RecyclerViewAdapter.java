package com.dora.myapplication.Cryptography.Symmetric.AllSymmetricCiphers.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dora.myapplication.R;

import java.util.ArrayList;

public class historySymmetric_RecyclerViewAdapter extends RecyclerView.Adapter<historySymmetric_RecyclerViewAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<String> encodedValuesArray;
    private final ArrayList<String> timestampValuesArray;

    public historySymmetric_RecyclerViewAdapter(Context context, ArrayList<String> encodedValuesArray, ArrayList<String> timestampValuesArray) {
        this.context = context;
        this.encodedValuesArray = encodedValuesArray;
        this.timestampValuesArray = timestampValuesArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_symmetric_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (encodedValuesArray.isEmpty() || timestampValuesArray.isEmpty()) return;

        holder.copyToClipboard.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("historySymmetricCipher", encodedValuesArray.get(position));
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "Copied to Clipboard !!!", Toast.LENGTH_SHORT).show();
        });


        String encodedTitleString = encodedValuesArray.get(position);
        if (encodedTitleString.length() > 25)
            encodedTitleString = encodedTitleString.substring(0, 25) + ".........";
        holder.title.setText(encodedTitleString);
        String dateTimeStr = timestampValuesArray.get(position).substring(0, 11);
        int hrs = Integer.parseInt(timestampValuesArray.get(position).substring(11, 13));
        int mins = Integer.parseInt(timestampValuesArray.get(position).substring(14, 16));
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
        public TextView title, date;
        //        public ImageView imageView;
        public Button copyToClipboard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView1_row_hf);
            date = itemView.findViewById(R.id.textView3_row_hf);
            copyToClipboard = itemView.findViewById(R.id.read_more_hf);
//            description = itemView.findViewById(R.id.textView2_row_hf);
//            imageView = itemView.findViewById(R.id.image_row_hf);
        }
    }

}
