package com.example.messagingapp;

import android.content.Context;
import android.util.Log;
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
import java.util.Locale;

public class RecycleOfferAdapter extends RecyclerView.Adapter<RecycleOfferAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<ListFacade> listFacadeList;
    private ArrayList<ListFacade> allListFacadeList;
    private ArrayList<ListFacade> currentRows;
    private ArrayList<String> filterss = new ArrayList<>();
    private SelectListener listner;

    //constructor
    public RecycleOfferAdapter(Context context, ArrayList<ListFacade> listFacadeList, SelectListener listner){
        this.context = context;
        this.listner = listner;
        this.listFacadeList = listFacadeList;
        allListFacadeList = new ArrayList<>(listFacadeList);
        currentRows = new ArrayList<>(listFacadeList);
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
            ArrayList<ListFacade> removedFiltRows = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                removedFiltRows.addAll(allListFacadeList);
            }else{
                String[] colWithFilt = charSequence.toString().split(":");
                String filtCol = colWithFilt[0].toLowerCase().trim();
                String filtContent = colWithFilt[1].toLowerCase().trim();
                //Log.d("filter", filtCol + " Content: " + filtContent);

                if(filterss.contains(filtContent)){
                    filterss.remove(filtContent);

                    if(filterss.size() == 0){
                        removedFiltRows.addAll(allListFacadeList);
                    }
                } else{
                    filterss.add(filtContent);
                }
                    for (ListFacade item : currentRows) {
                        //Log.d("filter", "result if contains: " + getFiltCol(item, filtCol).toLowerCase().contains(filtContent));
                        if (getFiltCol(item, filtCol).toLowerCase().contains(filtContent)) {
                            removedFiltRows.add(item);
                        }

                    }


            }
            FilterResults results = new FilterResults();
            results.values = removedFiltRows;
            currentRows.removeAll(removedFiltRows);
            Log.d("filter", "currentRows size: " + currentRows.size());
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

    String getFiltCol(ListFacade listFacade, String col){
        String result = "";
        switch (col){
            case "title": col = "title";
                result = listFacade.getTitle();
                break;
            //case "author": col = "author";
            //    result = listFacade.getAuthor();
            //    break;
            case "university": col = "university";
                result = listFacade.getUniversity();
                break;
            case "type": col = "type";
                result = listFacade.getType();
                break;
            case "coursecode": col = "coursecode";
                result = listFacade.getCourseCode();
                break;
            //case "isbn": col = "isbn";
            //    result = listFacade.getIsbn().toString();
            //    break;
        }
        //Log.d("filter", "result getFiltCol: " + result);
        return result;
    }

    void recoverFilters(CharSequence bubText){
        String[] colWithFilt = bubText.toString().split(":");
        String filtCol = colWithFilt[0].toLowerCase().trim();
        String filtContent = colWithFilt[1].toLowerCase().trim();
        //Log.d("filter", filtCol + " Content: " + filtContent);



    }

}

