package com.example.fitup;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PendingRequestAdapter extends RecyclerView.Adapter<PendingRequestAdapter.ViewHolder> {

    public static final int TYPE_INCOMING = 1; // Others -> Me (Show Accept)
    public static final int TYPE_OUTGOING = 2; // Me -> Others (Show Awaiting)

    private Context context;
    private List<ConnectionRequest> requestList;
    private int type;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onAccept(ConnectionRequest request);
    }

    public PendingRequestAdapter(Context context, List<ConnectionRequest> requestList, int type, OnRequestActionListener listener) {
        this.context = context;
        this.requestList = requestList;
        this.type = type;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pending_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConnectionRequest request = requestList.get(position);

        holder.tvName.setText(request.getSenderName() != null ? request.getSenderName() : "Unknown");
        holder.tvRole.setText(request.getSenderRole() != null ? request.getSenderRole() : "Member");

        if (request.getSenderAvatar() != null) {
            Glide.with(context).load(request.getSenderAvatar()).placeholder(R.drawable.defaultavt).into(holder.imgAvatar);
        }

        if (type == TYPE_INCOMING) {
            holder.btnAccept.setText("Accept Request");
            holder.btnAccept.setEnabled(true);
            holder.btnAccept.setAlpha(1.0f);

            holder.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(request);
            });

        } else {
            holder.btnAccept.setText("Awaiting response");
            holder.btnAccept.setEnabled(false);
            holder.btnAccept.setAlpha(0.5f);
            holder.btnAccept.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRole;
        ImageView imgAvatar;
        AppCompatButton btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvRole = itemView.findViewById(R.id.tvRole);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            btnAccept = itemView.findViewById(R.id.btnAccept);
        }
    }
}