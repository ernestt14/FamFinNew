package com.example.finnew;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class QRAdapter extends RecyclerView.Adapter<QRAdapter.QRViewHolder> {

    private List<QRData> qrDataList;

    public QRAdapter(List<QRData> qrDataList) {
        this.qrDataList = qrDataList;
    }

    @NonNull
    @Override
    public QRViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr, parent, false);
        return new QRViewHolder(view);
    }

    @Override
    public void onBindViewHolder(QRViewHolder holder, int position) {
        QRData qrData = qrDataList.get(position);
        holder.qrNameTextView.setText(qrData.getQrName());
        Picasso.get().load(Uri.parse(qrData.getImageUrl())).into(holder.qrImageView);
    }

    @Override
    public int getItemCount() {
        return qrDataList.size();
    }

    public static class QRViewHolder extends RecyclerView.ViewHolder {
        TextView qrNameTextView;
        ImageView qrImageView;

        public QRViewHolder(View itemView) {
            super(itemView);
            qrNameTextView = itemView.findViewById(R.id.qrNameTextView);
            qrImageView = itemView.findViewById(R.id.qrImageView);
        }
    }
}
