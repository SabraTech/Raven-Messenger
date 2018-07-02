package com.example.space.ravenmessenger.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.space.ravenmessenger.R;
import com.example.space.ravenmessenger.ui.activities.ChatActivity;

import java.util.List;

public class SmartReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> replies;
    private Context context;


    public SmartReplyAdapter(Context context, List<String> replies) {
        this.context = context;
        this.replies = replies;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_reply, parent, false);
        return new ItemSmartReplyViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String text = replies.get(position);

        ((ItemSmartReplyViewHolder) holder).replyText.setText(text);

        ((ItemSmartReplyViewHolder) holder).replyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent replyChosen = new Intent(ChatActivity.ACTION_SMART_REPLY_CHOSEN);
                replyChosen.putExtra("text", text);
                context.sendBroadcast(replyChosen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return replies.size();
    }

    class ItemSmartReplyViewHolder extends RecyclerView.ViewHolder {
        TextView replyText;
        private Context context;

        ItemSmartReplyViewHolder(Context context, View itemView) {
            super(itemView);
            replyText = itemView.findViewById(R.id.textContentReply);
            this.context = context;
        }
    }
}