package com.example.messagingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.R;
import com.example.messagingapp.model.firebaseChatModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A {@link Fragment} class for displaying all users that have an open chat with the logged in user
 */
public class ChatListFragment extends Fragment {

    //Autogenerated code
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //Adapter for recycler
    FirestoreRecyclerAdapter<firebaseChatModel, NoteViewHolder> chatAdapter;
    //Variables for references to XML
    private RecyclerView recyclerView;
    private ImageView mImageViewOfUser;
    //Firebase variables
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    //Autogenerated code
    private String mParam1;
    private String mParam2;

    public ChatListFragment() {
        // Required empty public constructor
    }

    //Auto generated code
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Set view to hold each individual conversation in the list
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //Setup firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        //Init references to XML
        recyclerView = view.findViewById(R.id.chatContainer);

        //Query database for any existing conversations
        Query query = firebaseFirestore.collection("Users").document(firebaseAuth.getCurrentUser().getUid()).collection("ReceivingUsers");

        //Set the query for our recycler
        FirestoreRecyclerOptions<firebaseChatModel> allUsernames = new FirestoreRecyclerOptions.Builder<firebaseChatModel>().setQuery(query,
                firebaseChatModel.class).build();

        //Implement adapter, this sets the text for every individual conversation and stores
        //additional info we will need when moving to specific chat
        chatAdapter = new FirestoreRecyclerAdapter<firebaseChatModel, NoteViewHolder>(allUsernames) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebaseChatModel model) {
                //Set name of user
                String usernameToChatWth = model.getName();
                holder.usernameToChat.setText(model.getName());
                //Set on click listener to redirect to specific chat when clicked
                holder.currentCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), SpecificChatActivity.class);
                        intent.putExtra("name", usernameToChatWth);
                        intent.putExtra("receiverUID", model.getUid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, parent, false);
                return new NoteViewHolder(view);
            }
        };

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        chatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (chatAdapter != null) {
            chatAdapter.stopListening();
        }
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView usernameToChat;
        private CardView currentCard;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameToChat = itemView.findViewById(R.id.chat_row_name);
            currentCard = itemView.findViewById(R.id.chat_list_card_view);
        }
    }


}