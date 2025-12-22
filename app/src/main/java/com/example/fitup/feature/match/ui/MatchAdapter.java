package com.example.fitup.feature.match.ui;

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

import com.example.fitup.R;
import com.example.fitup.feature.match.model.MatchResult;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.VH> {

    private final Context context;
    private final List<MatchResult> data;

    public MatchAdapter(Context context, List<MatchResult> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_match, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        MatchResult r = data.get(position);

        h.tvName.setText((position + 1) + ". " + r.profile.name);
        h.tvScore.setText(r.score + "%");
        h.pbScore.setProgress(r.score);
        h.tvReason.setText(r.reason);

        h.btnConnect.setOnClickListener(v ->
                Toast.makeText(context, "Sent connection to " + r.profile.name, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvScore, tvReason;
        ProgressBar pbScore;
        Button btnConnect;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvScore = itemView.findViewById(R.id.tvScore);
            tvReason = itemView.findViewById(R.id.tvReason);
            pbScore = itemView.findViewById(R.id.pbScore);
            btnConnect = itemView.findViewById(R.id.btnConnect);
        }
    }
}
