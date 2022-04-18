package com.example.messagingapp;

import com.example.messagingapp.model.ListFacade;

/**
 * Interface for observing when a listing facade is clicked
 */
public interface SelectListener {
    /**
     * When event observed do something
     * @param listFacade ListFacade that got clicked
     */
    void onItemClicked(ListFacade listFacade);
}

