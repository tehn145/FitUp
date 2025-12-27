package com.example.fitup;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_SENT_IMAGE = 3;
    private static final int VIEW_TYPE_RECEIVED_IMAGE = 4;
    private static final int VIEW_TYPE_SENT_SESSION = 5;
    private static final int VIEW_TYPE_RECEIVED_SESSION = 6;

    private Context context;
    private List<Message> messageList;
    private String currentUserId;

    public ChatAdapter(Context context, List<Message> messageList, String currentUserId) {
        this.context = context;
        this.messageList = messageList;
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<Message> messages) {
        this.messageList = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId() == null) return VIEW_TYPE_RECEIVED;

        boolean isMe = message.getSenderId().equals(currentUserId);
        String type = message.getType();

        if ("image".equals(type)) {
            return isMe ? VIEW_TYPE_SENT_IMAGE : VIEW_TYPE_RECEIVED_IMAGE;
        } else if ("session".equals(type)) {
            return isMe ? VIEW_TYPE_SENT_SESSION : VIEW_TYPE_RECEIVED_SESSION;
        } else {
            return isMe ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        if (viewType == VIEW_TYPE_SENT) {
            return new TextMessageViewHolder(inflater.inflate(R.layout.item_message_sent, parent, false));
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            return new TextMessageViewHolder(inflater.inflate(R.layout.item_message_received, parent, false));
        } else if (viewType == VIEW_TYPE_SENT_IMAGE) {
            return new ImageMessageViewHolder(inflater.inflate(R.layout.item_message_sent_image, parent, false));
        } else if (viewType == VIEW_TYPE_RECEIVED_IMAGE) {
            return new ImageMessageViewHolder(inflater.inflate(R.layout.item_message_received_image, parent, false));
        } else if (viewType == VIEW_TYPE_SENT_SESSION) {
            return new SessionViewHolder(inflater.inflate(R.layout.item_message_sent_session, parent, false));
        } else {
            return new SessionViewHolder(inflater.inflate(R.layout.item_message_received_session, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        TextView dateHeader = null;
        if (holder instanceof TextMessageViewHolder) dateHeader = ((TextMessageViewHolder) holder).dateHeader;
        else if (holder instanceof ImageMessageViewHolder) dateHeader = ((ImageMessageViewHolder) holder).dateHeader;
        else if (holder instanceof SessionViewHolder) dateHeader = ((SessionViewHolder) holder).dateHeader;

        if (dateHeader != null) {
            if (message.isShowDateHeader()) {
                dateHeader.setVisibility(View.VISIBLE);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                dateHeader.setText(dateFormat.format(new Date(message.getTimestamp())));
                dateHeader.setTextColor(Color.GRAY);
            } else {
                dateHeader.setVisibility(View.GONE);
            }
        }


        if (holder instanceof TextMessageViewHolder) {
            TextMessageViewHolder txtHolder = (TextMessageViewHolder) holder;

            String msgContent = message.getText();
            if (msgContent == null) msgContent = "";

            txtHolder.textMessage.setText(msgContent);
            txtHolder.textDateTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            txtHolder.textMessage.setTextColor(Color.WHITE);
            txtHolder.textDateTime.setTextColor(Color.LTGRAY);

            txtHolder.textMessage.setMaxLines(Integer.MAX_VALUE);
        }
        else if (holder instanceof ImageMessageViewHolder) {
            ImageMessageViewHolder imgHolder = (ImageMessageViewHolder) holder;
            imgHolder.textDateTime.setText(timeFormat.format(new Date(message.getTimestamp())));
            imgHolder.textDateTime.setTextColor(Color.LTGRAY);

            String imageUrl = message.getText();

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .error(R.drawable.defaultavt)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new CenterCrop(), new RoundedCorners(18))
                        .override(800, 800)
                        .into(imgHolder.imageMessage);
            } else {
                imgHolder.imageMessage.setImageResource(R.drawable.defaultavt);
            }
        }

        else if (holder instanceof SessionViewHolder) {
            bindSessionViewHolder((SessionViewHolder) holder, message, timeFormat);
        }
    }

    private void bindSessionViewHolder(SessionViewHolder holder, Message message, SimpleDateFormat timeFormat) {
        holder.tvSessionName.setText(message.getText());
        holder.tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));

        holder.tvSessionName.setTextColor(Color.WHITE);
        holder.tvDetails.setTextColor(Color.LTGRAY);
        holder.tvTime.setTextColor(Color.LTGRAY);

        String sessionId = message.getSessionId();

        if (sessionId != null) {
            FirebaseFirestore.getInstance().collection("sessions")
                    .document(sessionId)
                    .addSnapshotListener((doc, e) -> {
                        if (e != null || doc == null || !doc.exists()) {
                            holder.tvDetails.setText("Unavailable");
                            hideAllButtons(holder);
                            return;
                        }

                        String fetchedTitle = doc.getString("sessionName");
                        if (fetchedTitle != null && !fetchedTitle.isEmpty()) {
                            holder.tvSessionName.setText(fetchedTitle);
                        }

                        String status = doc.getString("status");
                        Double price = doc.getDouble("price");
                        Long scheduledTime = doc.getLong("scheduledTimestamp");
                        GeoPoint pos = doc.getGeoPoint("location");
                        Double lat = (pos != null) ? pos.getLatitude() : null;
                        Double lng = (pos != null) ? pos.getLongitude() : null;

                        String detailsText = "";
                        if (price != null) detailsText += String.format(Locale.US, "%.0f VND", price);
                        if (scheduledTime != null) {
                            SimpleDateFormat sdfDate = new SimpleDateFormat(" • MMM dd", Locale.getDefault());
                            detailsText += sdfDate.format(new Date(scheduledTime));
                            SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            holder.tvDetailsTime.setText(sdfTime.format(new Date(scheduledTime)) + " - (1h)");
                        }
                        holder.tvDetails.setText(detailsText);

                        String clientId = doc.getString("clientId");
                        boolean isClient = (currentUserId != null && currentUserId.equals(clientId));

                        holder.btnViewLocation.setOnClickListener(v -> {
                            if (lat != null && lng != null) openMapLocation(lat, lng);
                            else Toast.makeText(context, "Location unavailable", Toast.LENGTH_SHORT).show();
                        });

                        if ("completed".equals(status) || "cancelled".equals(status) || "expired".equals(status)) {
                            hideAllButtons(holder);
                        } else {
                            if (isClient) {
                                holder.btnViewLocation.setVisibility(View.VISIBLE);
                                holder.btnCancel.setVisibility(View.VISIBLE);
                                holder.btnFinish.setVisibility(View.GONE);
                            } else {
                                if ("pending".equals(status)) {
                                    holder.btnViewLocation.setVisibility(View.VISIBLE);
                                    holder.btnCancel.setVisibility(View.VISIBLE);
                                    holder.btnFinish.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
        }
        holder.btnFinish.setOnClickListener(v -> updateSessionStatus(sessionId, "finished"));
        holder.btnCancel.setOnClickListener(v -> updateSessionStatus(sessionId, "cancelled"));
        holder.itemView.setOnClickListener(v -> openSessionDetails(sessionId));
    }

    private void openMapLocation(double lat, double lng) {
        try {
            MapLocationFragment mapFragment = MapLocationFragment.newInstance(false, lat, lng);
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(android.R.id.content, mapFragment)
                        .addToBackStack(null)
                        .commit();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Map error", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideAllButtons(SessionViewHolder holder) {
        holder.btnViewLocation.setVisibility(View.GONE);
        holder.btnFinish.setVisibility(View.GONE);
        holder.btnCancel.setVisibility(View.GONE);
    }

    private void openSessionDetails(String sessionId) {
        SessionDetailsFragment bottomSheet = SessionDetailsFragment.newInstance(sessionId);
        if (context instanceof AppCompatActivity) {
            bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "SessionDetails");
        }
    }

    private void updateSessionStatus(String sessionId, String status) {
        FirebaseFirestore.getInstance().collection("sessions").document(sessionId)
                .update("status", status)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Session " + status, Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // --- ViewHolders ---

    static class TextMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textDateTime, dateHeader;
        TextMessageViewHolder(View itemView) {
            super(itemView);
            // Kiểm tra kỹ ID trong layout XML
            textMessage = itemView.findViewById(R.id.message_text);
            textDateTime = itemView.findViewById(R.id.time_text);
            dateHeader = itemView.findViewById(R.id.date_header);
        }
    }

    static class ImageMessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMessage;
        TextView textDateTime, dateHeader;
        ImageMessageViewHolder(View itemView) {
            super(itemView);
            imageMessage = itemView.findViewById(R.id.message_image);
            textDateTime = itemView.findViewById(R.id.time_text);
            dateHeader = itemView.findViewById(R.id.date_header);
        }
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        TextView tvSessionName, tvDetails, tvDetailsTime, tvTime, dateHeader;
        TextView btnViewLocation, btnFinish, btnCancel;

        SessionViewHolder(View itemView) {
            super(itemView);
            tvSessionName = itemView.findViewById(R.id.tv_session_name);
            tvDetails = itemView.findViewById(R.id.tv_session_details);
            tvDetailsTime = itemView.findViewById(R.id.tv_session_details_time);
            tvTime = itemView.findViewById(R.id.time_text);
            dateHeader = itemView.findViewById(R.id.date_header);
            btnViewLocation = itemView.findViewById(R.id.btn_view_session);
            btnFinish = itemView.findViewById(R.id.btn_finish_session);
            btnCancel = itemView.findViewById(R.id.btn_cancel_session);
        }
    }
}