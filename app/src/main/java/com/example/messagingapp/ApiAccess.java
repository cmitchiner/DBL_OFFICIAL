package com.example.messagingapp;

import android.database.Observable;

import com.example.messagingapp.model.BiddingData;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.example.messagingapp.model.MessageModel;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiAccess {
    @GET("/auction")
    Call<BiddingData> getAuctionDetails(@Query("aucId") int aucId, @Query("apiKey") String apiKey);

    @GET("/msgs")
    Call<ArrayList<MessageModel>> getMessages(@Query("apiKey") String apiKey,
                                              @Query("firstUser") String userId1,
                                              @Query("secondUser") String userId2);
    @GET("/listings")
    Call<ArrayList<ListFacade>> getInfo(@Query("apiKey") String apiKey, @Query("numberRows") int numberRows);

    @POST("/addlisting")
    Call<Listing> addNewListing(@Body Listing list);

    @GET("/listings/{id}")
    Call<ResponseBody> getDetailedListing(@Path("id") String listingId, @Query("apiKey") String apiKey
                                     );

    //@POST("/pushFiltDict")
    //Call<>

    @FormUrlEncoded
    @PATCH("/auction/{id}")
    Call<BiddingData> placeNewBid(
            @Path("id") int aucId,
            @Field("bidValue") int bidVal,
            @Field("bidderId") String bidId,
            @Field("apiKey") String apiKey
    );

    @FormUrlEncoded
    @POST("/msgs")
    Call<ResponseBody> sendMessage(
            @Field("sender") String SenderID,
            @Field("reciever") String RecieverID,
            @Field("text") String msg,
            @Field("photo") String photoURL,
            @Field("date") ZonedDateTime date,
            @Field("read") boolean read

    );


    @Multipart
    @POST("/img/test.png")
    Observable<ResponseBody> uploadImg(@Part MultipartBody.Part image);
}
