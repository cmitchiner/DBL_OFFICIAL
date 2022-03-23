package com.example.messagingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecycleOfferAdapter extends RecyclerView.Adapter<RecycleOfferAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<ListFacade> listFacadeList;
    private ArrayList<ListFacade> allListFacadeList;
    private ArrayList<String> filterss = new ArrayList<>();
    private SelectListener listner;

    //constructor
    public RecycleOfferAdapter(Context context, ArrayList<ListFacade> listFacadeList, SelectListener listner){
        this.context = context;
        this.listner = listner;
        this.listFacadeList = listFacadeList;
        allListFacadeList = new ArrayList<>(listFacadeList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.listing_row, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListFacade rower = listFacadeList.get(position);
        holder.title.setText(rower.getTitle());
        holder.title.setTag(rower.getTitle());
        holder.price.setText(rower.getPrice()/100 + "€");
        holder.offerType.setText(rower.getType());
        holder.offerType.setTag(rower.getTitle());
        holder.courseId.setText(rower.getCourseCode());
        holder.university.setText(rower.getUniversity());
        holder.isBid = rower.getIsBid();
        holder.id = rower.getList_iD();
        holder.isbn = rower.getIsbn();
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onItemClicked(listFacadeList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listFacadeList.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }
    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<ListFacade> filterlist = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filterlist.addAll(allListFacadeList);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                if(filterss.contains(filterPattern)){
                    filterss.remove(filterPattern);
                    if(filterss.size() == 0){
                        filterlist.addAll(allListFacadeList);
                    }
                } else{
                    filterss.add(filterPattern);
                }
                for(int i = 0; i < filterss.size(); i++) {
                    for (ListFacade item : allListFacadeList) {
                        if (item.getTitle().toLowerCase().contains(filterss.get(i))
                                || item.getTitle().toLowerCase().contains(filterss.get(i))
                                || item.getType().toLowerCase().contains(filterss.get(i))
                                || item.getCourseCode().toLowerCase().contains(filterss.get(i))
                                || item.getUniversity().toLowerCase().contains(filterss.get(i))) {
                            filterlist.add(item);
                        }

                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterlist;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listFacadeList.clear();
            listFacadeList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our text views.
        private final TextView title;
        private final TextView price;
        private final TextView offerType;
        private final TextView courseId;
        private final TextView university;
        private boolean sold;
        private boolean isBid;
        private String author;
        private CardView cardView;
        private String id;
        private Long isbn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing our text views.
            title = itemView.findViewById(R.id.Title);
            price = itemView.findViewById(R.id.OfferPriceRow);
            offerType = itemView.findViewById(R.id.OfferTypeRow);
            courseId = itemView.findViewById(R.id.OfferCourseIdRow);
            university = itemView.findViewById(R.id.OfferUniversityIdRow);
            cardView = itemView.findViewById(R.id.row_card);
        }
    }

}

