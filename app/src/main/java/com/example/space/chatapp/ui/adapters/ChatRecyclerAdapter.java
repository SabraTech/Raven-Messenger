//package com.example.space.chatapp.ui.adapters;
//
//
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.example.space.chatapp.R;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.List;
//
//public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int VIEW_TYPE_ME = 1;
//    private static final int VIEW_TYPE_OTHER = 2;
//    private List<Chat> chats;
//
//    public ChatRecyclerAdapter(List<Chat> chats) {
//        this.chats = chats;
//    }
//
//    public void add(Chat chat) {
//        chats.add(chat);
//        notifyItemInserted(chats.size() - 1);
//    }
//
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        RecyclerView.ViewHolder viewHolder = null;
//        switch (viewType) {
//            case VIEW_TYPE_ME:
//                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_source, parent, false);
//                viewHolder = new MyChatViewHolder(viewChatMine);
//                break;
//            case VIEW_TYPE_OTHER:
//                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_destination, parent, false);
//                viewHolder = new OtherChatViewHolder(viewChatOther);
//                break;
//        }
//        return viewHolder;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (TextUtils.equals(chats.get(position).getSenderUid()
//                , FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//            configureMyChatViewHolder((MyChatViewHolder) holder, position);
//        } else {
//            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
//        }
//    }
//
//    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
//        Chat chat = chats.get(position);
//        String alphabet = chat.getSender().substring(0, 1);
//        myChatViewHolder.txtChatMessage.setText(chat.getMessage());
//        myChatViewHolder.txtUserAlphabet.setText(alphabet);
//    }
//
//    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
//        Chat chat = chats.get(position);
//        String alphabet = chat.getSender().substring(0, 1);
//        otherChatViewHolder.txtChatMessage.setText(chat.getMessage());
//        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
//    }
//
//    @Override
//    public int getItemCount() {
//        if (chats != null) {
//            return chats.size();
//        }
//        return 0;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (TextUtils.equals(chats.get(position).getSenderUid()
//                , FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//            return VIEW_TYPE_ME;
//        } else {
//            return VIEW_TYPE_OTHER;
//        }
//    }
//
//    private static class MyChatViewHolder extends RecyclerView.ViewHolder {
//        private TextView txtChatMessage, txtUserAlphabet;
//
//        public MyChatViewHolder(View itemView) {
//            super(itemView);
//            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
//            txtUserAlphabet = itemView.findViewById(R.id.text_view_user_alphabet);
//        }
//    }
//
//    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
//        private TextView txtChatMessage, txtUserAlphabet;
//
//        public OtherChatViewHolder(View itemView) {
//            super(itemView);
//            txtChatMessage = itemView.findViewById(R.id.text_view_chat_message);
//            txtUserAlphabet = itemView.findViewById(R.id.text_view_user_alphabet);
//        }
//    }
//}
