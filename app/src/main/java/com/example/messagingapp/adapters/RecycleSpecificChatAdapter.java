package com.example.messagingapp.adapters;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.R;
import com.example.messagingapp.objects.Message;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecycleSpecificChatAdapter extends RecyclerView.Adapter {

    /** VARIABLE DECLARATIONS **/

    //Current activity context
    Context context;
    //Array list to hold all messages in a chat
    ArrayList<Message> messages;
    File localFile = null;

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
            View view = LayoutInflater.from(context).inflate(R.layout.specific_chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.specific_chat_reciever, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://justudy-ebc7b.appspot.com");
        StorageReference storageReference = storage.getReference().child(message.getUniqueID());
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder viewHolder = (SenderViewHolder) holder;
            if (message.getImage()) {
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.timeOfMessage.setVisibility(View.GONE);
                viewHolder.imageView.setVisibility(View.VISIBLE);


                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    Log.e("error", Log.getStackTraceString(e));
                }
                if (localFile != null) {
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            viewHolder.imageView.setImageURI(Uri.parse(localFile.toString()));
                        }});
                }

            } else {
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.timeOfMessage.setVisibility(View.VISIBLE);
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.messageText.setText(message.getMessage());
                viewHolder.timeOfMessage.setText(message.getCurrentTime());
            }
        } else {
            ReceiverViewHolder viewHolder = (ReceiverViewHolder) holder;
            if (message.getImage()) {
                viewHolder.messageText.setVisibility(View.GONE);
                viewHolder.timeOfMessage.setVisibility(View.GONE);
                viewHolder.imageView.setVisibility(View.VISIBLE);
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    Log.e("error", Log.getStackTraceString(e));
                }
                if (localFile != null) {
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            viewHolder.imageView.setImageURI(Uri.parse(localFile.toString()));
                        }});
                }
            } else {
                viewHolder.messageText.setVisibility(View.VISIBLE);
                viewHolder.timeOfMessage.setVisibility(View.VISIBLE);
                viewHolder.imageView.setVisibility(View.GONE);
                viewHolder.messageText.setText(message.getMessage());
                viewHolder.timeOfMessage.setText(message.getCurrentTime());
            }
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
        ImageView imageView;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.senderTextView);
            timeOfMessage = itemView.findViewById(R.id.timeOfSentMsgTV);
            imageView = itemView.findViewById(R.id.senderImageView);

        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder
    {
        TextView messageText, timeOfMessage;
        ImageView imageView;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.senderTextView);
            timeOfMessage = itemView.findViewById(R.id.timeOfSentMsgTV);
            imageView = itemView.findViewById(R.id.senderImageView);

        }
    }
}
