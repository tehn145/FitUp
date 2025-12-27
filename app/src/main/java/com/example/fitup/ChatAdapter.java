package com.example.fitup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Define View Types
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
        boolean isMe = message.getSenderId().equals(currentUserId);

        if ("image".equals(message.getType())) {
            return isMe ? VIEW_TYPE_SENT_IMAGE : VIEW_TYPE_RECEIVED_IMAGE;
        } else if ("session".equals(message.getType())) {
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
            } else {
                dateHeader.setVisibility(View.GONE);
            }
        }

        // --- Type Specific Binding ---
        if (holder instanceof SessionViewHolder) {
            bindSessionViewHolder((SessionViewHolder) holder, message, timeFormat);
        } else if (holder instanceof TextMessageViewHolder) {
            ((TextMessageViewHolder) holder).textMessage.setText(message.getText());
            ((TextMessageViewHolder) holder).textDateTime.setText(timeFormat.format(new Date(message.getTimestamp())));
        } else if (holder instanceof ImageMessageViewHolder) {
            ImageMessageViewHolder imgHolder = (ImageMessageViewHolder) holder;
            imgHolder.textDateTime.setText(timeFormat.format(new Date(message.getTimestamp())));

            Glide.with(context)
                    .load(message.getText())
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .into(imgHolder.imageMessage);
        }
    }

    private void bindSessionViewHolder(SessionViewHolder holder, Message message, SimpleDateFormat timeFormat) {
        // Set initial text from message, but we will overwrite this with fresh data from Firestore below
        holder.tvSessionName.setText(message.getText());
        holder.tvTime.setText(timeFormat.format(new Date(message.getTimestamp())));

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
                        // -------------------------------------------------------------------------

                        String status = doc.getString("status");
                        Double price = doc.getDouble("price");
                        Long scheduledTime = doc.getLong("scheduledTimestamp");

                        long sHour = (doc.getLong("startHour") != null) ? doc.getLong("startHour") : -1;
                        long sMin = (doc.getLong("startMinute") != null) ? doc.getLong("startMinute") : 0;
                        long eHour = (doc.getLong("endHour") != null) ? doc.getLong("endHour") : -1;
                        long eMin = (doc.getLong("endMinute") != null) ? doc.getLong("endMinute") : 0;

                        GeoPoint pos = doc.getGeoPoint("location");
                        Double lat = (pos != null) ? pos.getLatitude() : null;
                        Double lng = (pos != null) ? pos.getLongitude() : null;

                        String formattedStatus = (status != null) ? status.toUpperCase() : "UNKNOWN";

                        String detailsText = "";
                        if (price != null) detailsText += String.format(Locale.US, "%.0f VND", price);
                        if (scheduledTime != null) {
                            SimpleDateFormat sdfDate = new SimpleDateFormat(" • MMM dd", Locale.getDefault());
                            detailsText += sdfDate.format(new Date(scheduledTime));

                            String timeRangeStr;
                            if (sHour != -1 && eHour != -1) {
                                String startTime = formatTimeSimple((int) sHour, (int) sMin);
                                String endTime = formatTimeSimple((int) eHour, (int) eMin);

                                int startTotalMins = (int) (sHour * 60 + sMin);
                                int endTotalMins = (int) (eHour * 60 + eMin);
                                if (endTotalMins < startTotalMins) endTotalMins += 24 * 60; // Handle overnight wrap

                                int diffMins = endTotalMins - startTotalMins;
                                String durationStr = "";
                                if (diffMins > 0) {
                                    int h = diffMins / 60;
                                    int m = diffMins % 60;
                                    if (h > 0) durationStr += h + "h";
                                    if (m > 0) durationStr += " " + m + "m";
                                    durationStr = " (" + durationStr.trim() + ")";
                                }

                                timeRangeStr = startTime + " - " + endTime + durationStr;
                            } else {
                                SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a", Locale.getDefault());
                                timeRangeStr = sdfTime.format(new Date(scheduledTime)) + " - (1h)";
                            }

                            holder.tvDetailsTime.setText(timeRangeStr);
                        }
                        holder.tvDetails.setText(detailsText);

                        String clientId = doc.getString("clientId");
                        boolean isClient = currentUserId.equals(clientId);

                        holder.btnViewLocation.setOnClickListener(v -> {
                            if (lat != null && lng != null) {
                                openMapLocation(lat, lng);
                            } else {
                                Toast.makeText(context, "Location coordinates missing", Toast.LENGTH_SHORT).show();
                            }
                        });

                        if ("completed".equals(status) || "cancelled".equals(status) || "expired".equals(status)) {
                            hideAllButtons(holder);
                        }

                        else {
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

        // Logic for Finish/Cancel buttons...
        holder.btnFinish.setOnClickListener(v -> updateSessionStatus(sessionId, "finished"));
        holder.btnCancel.setOnClickListener(v -> updateSessionStatus(sessionId, "cancelled"));

        // Card Click can still open details
        holder.itemView.setOnClickListener(v -> openSessionDetails(sessionId));
    }

    private String formatTimeSimple(int hour, int minute) {
        String amPm = "AM";
        if (hour >= 12) {
            amPm = "PM";
            if (hour > 12) hour -= 12;
        }
        if (hour == 0) hour = 12;
        return String.format(Locale.getDefault(), "%d:%02d %s", hour, minute, amPm);
    }

    private void openMapLocation(double lat, double lng) {
        try {

            MapLocationFragment mapFragment = MapLocationFragment.newInstance(false, lat, lng);

            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                        .add(android.R.id.content, mapFragment) // Open fullscreen over content
                        .addToBackStack(null)
                        .commit();
            }
        } catch (NoClassDefFoundError | Exception e) {
            Toast.makeText(context, "Map feature not ready", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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

        // These are TEXTVIEWS based on your XML
        TextView btnViewLocation, btnFinish, btnCancel;

        SessionViewHolder(View itemView) {
            super(itemView);

            // Text Content
            tvSessionName = itemView.findViewById(R.id.tv_session_name);
            tvDetails = itemView.findViewById(R.id.tv_session_details);
            tvDetailsTime = itemView.findViewById(R.id.tv_session_details_time);
            tvTime = itemView.findViewById(R.id.time_text);
            dateHeader = itemView.findViewById(R.id.date_header);

            // Buttons (actually TextViews in XML)
            btnViewLocation = itemView.findViewById(R.id.btn_view_session);
            btnFinish = itemView.findViewById(R.id.btn_finish_session);
            btnCancel = itemView.findViewById(R.id.btn_cancel_session);
        }
    }
}
