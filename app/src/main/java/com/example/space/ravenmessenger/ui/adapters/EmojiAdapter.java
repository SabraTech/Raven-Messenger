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

public class EmojiAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> emojis;
    private Context context;


    public EmojiAdapter(Context context, List<String> emojis) {
        this.context = context;
        this.emojis = emojis;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rc_item_emoji, parent, false);
        return new ItemEmojiViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final String code = emojis.get(position);

        ((ItemEmojiViewHolder) holder).emojiCode.setText(code);

        ((ItemEmojiViewHolder) holder).emojiCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emojiChosen = new Intent(ChatActivity.ACTION_EMOJI_CHOSEN);
                emojiChosen.putExtra("code", code);
                context.sendBroadcast(emojiChosen);
            }
        });
    }

    @Override
    public int getItemCount() {
        return emojis.size();
    }

    class ItemEmojiViewHolder extends RecyclerView.ViewHolder {
        TextView emojiCode;
        private Context context;

        ItemEmojiViewHolder(Context context, View itemView) {
            super(itemView);
            emojiCode = itemView.findViewById(R.id.textContentEmoji);
            this.context = context;
        }
    }
}
