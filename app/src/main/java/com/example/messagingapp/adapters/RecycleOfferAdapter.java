package com.example.messagingapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messagingapp.R;
import com.example.messagingapp.SelectListener;
import com.example.messagingapp.model.ListFacade;

import java.util.ArrayList;

public class RecycleOfferAdapter extends RecyclerView.Adapter<RecycleOfferAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ListFacade> listFacadeList;
    private SelectListener listner;

    //Adapter constructor
    public RecycleOfferAdapter(Context context, ArrayList<ListFacade> listFacadeList, SelectListener listner){
        this.context = context;
        this.listner = listner;
        this.listFacadeList = listFacadeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.listing_row_2, parent, false));

    }

    //Setting values to the the different components of the row (viewHolder)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListFacade rower = listFacadeList.get(holder.getAdapterPosition());
        holder.isBid = rower.getIsBid();
        holder.id = rower.getList_iD();
        holder.isbn = rower.getIsbn();
        holder.location = rower.getLocation();
        holder.priceEuro = rower.getPrice();

        holder.title.setText(rower.getTitle());
        holder.title.setTag(rower.getTitle());
        holder.price.setText(String.valueOf(holder.priceEuro/100 + " €"));

        //Adding the icon representing the type of listing
        switch(rower.getType().toLowerCase()) {
            case "book":
                holder.offerType.setImageResource(R.drawable.ic_book);
                break;
            case "notes":
                holder.offerType.setImageResource(R.drawable.ic_notes);
                break;
            case "summary":
                holder.offerType.setImageResource(R.drawable.ic_baseline_fact_check_24);
                break;
        }
        holder.offerType.setTag(rower.getTitle());
        holder.courseId.setText(rower.getCourseCode());
        holder.university.setText(rower.getUniversity());

        //OnClick listener for the item
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onItemClicked(listFacadeList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFacadeList.size();
    }

    // creating variables for the text views.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView price;
        private final ImageView offerType;
        private final TextView courseId;
        private final TextView university;
        private boolean sold;
        private boolean isBid;
        private String author;
        private CardView cardView;
        private String id;
        private Long isbn;
        private String location;
        private double priceEuro;

        // initializing text views.
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.Title);
            price = itemView.findViewById(R.id.OfferPriceRow);
            offerType = itemView.findViewById(R.id.OfferTypeRow);
            courseId = itemView.findViewById(R.id.OfferCourseIdRow);
            university = itemView.findViewById(R.id.OfferUniversityIdRow);
            cardView = itemView.findViewById(R.id.row_card);
        }
    }


}

