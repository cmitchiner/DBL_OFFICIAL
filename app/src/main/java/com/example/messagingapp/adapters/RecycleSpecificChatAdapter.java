package com.example.messagingapp.adapters;

import android.content.Context;
import android.net.Uri;
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

    /**
     * VARIABLE DECLARATIONS
     **/

    //Current activity context
    Context context;

    //Array list to hold all messages in a chat
    ArrayList<Message> messages;
    File localFile = null;

    //Constant integers to represent a sent or received message
    final int ITEM_SEND = 1;
    final int ITEM_RECEIVE = 2;


    /**
     * Constructor to set activity context and feed in messages array
     *
     * @param context  context of the current activity
     * @param messages all messages in this specific chat
     */
    public RecycleSpecificChatAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    /**
     * Method that runs once on the creation of the view holder
     *
     * @param parent
     * @param viewType
     * @return the proper view holder whether a message is from a receiver or sent by a user
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //In order to put the chat bubbles on the correct side of the screen, we assign the proper view holder
        // based on whether a message is sent or received
        if (viewType == ITEM_SEND) { //Sent item
            View view = LayoutInflater.from(context).inflate(R.layout.specific_chat_sender, parent, false);
            return new SenderViewHolder(view);
        } else { //Received item
            View view = LayoutInflater.from(context).inflate(R.layout.specific_chat_reciever, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    /**
     * Sets the data for the view holders
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //get current position in messages array list
        Message message = messages.get(position);
        //Create a reference to the firebase storaging containg message data
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://justudy-ebc7b.appspot.com");
        StorageReference storageReference = storage.getReference().child(message.getUniqueID());
        //Check whether the view holder is for a sender or receiver
        if (holder.getClass() == SenderViewHolder.class) {
            //Sender view holder, set data accordingly
            SenderViewHolder senderHolder = (SenderViewHolder) holder;

            if (message.getImage()) {    //Message is an image, set data accordingly
                senderHolder.messageText.setVisibility(View.GONE);
                senderHolder.timeOfMessage.setVisibility(View.GONE);
                senderHolder.imageView.setVisibility(View.VISIBLE);

                //Store image locally
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    Log.e("error", Log.getStackTraceString(e));
                }
                if (localFile != null) {
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            senderHolder.imageView.setImageURI(Uri.parse(localFile.toString()));
                        }
                    });
                }

            } else { //not an image, set data accordingly
                senderHolder.messageText.setVisibility(View.VISIBLE);
                senderHolder.timeOfMessage.setVisibility(View.VISIBLE);
                senderHolder.imageView.setVisibility(View.GONE);
                senderHolder.messageText.setText(message.getMessage());
                senderHolder.timeOfMessage.setText(message.getCurrentTime());
            }
        } else { //Receiver view holder
            ReceiverViewHolder receiverHolder = (ReceiverViewHolder) holder;
            if (message.getImage()) { //message is an image
                receiverHolder.messageText.setVisibility(View.GONE);
                receiverHolder.timeOfMessage.setVisibility(View.GONE);
                receiverHolder.imageView.setVisibility(View.VISIBLE);

                //Store image locally
                try {
                    localFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    Log.e("error", Log.getStackTraceString(e));
                }
                if (localFile != null) {
                    storageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            receiverHolder.imageView.setImageURI(Uri.parse(localFile.toString()));
                        }
                    });
                }
            } else { //message is not an image
                receiverHolder.messageText.setVisibility(View.VISIBLE);
                receiverHolder.timeOfMessage.setVisibility(View.VISIBLE);
                receiverHolder.imageView.setVisibility(View.GONE);
                receiverHolder.messageText.setText(message.getMessage());
                receiverHolder.timeOfMessage.setText(message.getCurrentTime());
            }
        }
    }

    /**
     * Getter that returns which type a message is, sent or received
     *
     * @param position position of the array list
     * @return ITEM_SEND if message was sent, ITEM_RECEIVED if message was received
     */
    @Override
    public int getItemViewType(int position) {
        //Grab current message
        Message message = messages.get(position);

        //Check whether the UID matches or not, and return accordingly
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderID())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }

    /**
     * Getter for how big the array list is
     *
     * @return an integer containg the size of array list
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Class for the view holder for a sent message
     */
    class SenderViewHolder extends RecyclerView.ViewHolder {
        /**
         * VARIABLES
         **/
        TextView messageText, timeOfMessage;
        ImageView imageView;

        /**
         * Default constructor
         *
         * @param itemView
         */
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.senderTextView);
            timeOfMessage = itemView.findViewById(R.id.timeOfSentMsgTV);
            imageView = itemView.findViewById(R.id.senderImageView);

        }
    }


    /**
     * Class for the view holder for a recieved message
     */
    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        /**
         * VARIABLES
         **/
        TextView messageText, timeOfMessage;
        ImageView imageView;

        /**
         * Default constructor
         *
         * @param itemView
         */
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.senderTextView);
            timeOfMessage = itemView.findViewById(R.id.timeOfSentMsgTV);
            imageView = itemView.findViewById(R.id.senderImageView);

        }
    }
}
