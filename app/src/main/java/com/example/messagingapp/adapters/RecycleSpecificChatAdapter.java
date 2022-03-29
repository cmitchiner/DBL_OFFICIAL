package com.example.messagingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.R;
import com.example.messagingapp.objects.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class RecycleSpecificChatAdapter extends RecyclerView.Adapter {

    /** VARIABLE DECLARATIONS **/

    //Current activity context
    Context context;
    //Array list to hold all messages in a chat
    ArrayList<Message> messages;

    //Constant integers to represent a sent or received message
    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;

    //Constructor to set activity context and feed in messages array
    public RecycleSpecificChatAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_me, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_other, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            viewHolder.messageText.setText(message.getMessage());
            viewHolder.timeOfMessage.setText(message.getCurrentTime());
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            viewHolder.messageText.setText(message.getMessage());
            viewHolder.timeOfMessage.setText(message.getCurrentTime());
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderID())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class SenderViewHolder extends RecyclerView.ViewHolder
    {
        TextView messageText, timeOfMessage;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_gchat_message_me);
            timeOfMessage = itemView.findViewById(R.id.text_gchat_timestamp_me);

        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder
    {
        TextView messageText, timeOfMessage;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_gchat_message_other);
            timeOfMessage = itemView.findViewById(R.id.text_gchat_timestamp_other);

        }
    }
}
