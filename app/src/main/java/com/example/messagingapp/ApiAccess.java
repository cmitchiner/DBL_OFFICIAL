package com.example.messagingapp;

import android.database.Observable;

import com.example.messagingapp.model.BiddingData;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;
import com.example.messagingapp.model.MessageModel;

import org.json.JSONObject;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface ApiAccess for retrofit method specification
 */
public interface ApiAccess {

    /**
     * Retrofit specification for getting the details of a bidding listing
     *
     * @param aucId
     * @param apiKey
     * @return
     */
    @GET("/auction")
    Call<BiddingData> getAuctionDetails(@Query("aucId") int aucId, @Query("apiKey") String apiKey);

    @GET("/msgs")
    Call<ArrayList<MessageModel>> getMessages(@Query("apiKey") String apiKey,
                                              @Query("firstUser") String userId1,
                                              @Query("secondUser") String userId2);
    @GET("/listings")
    Call<ArrayList<ListFacade>> getInfo(@Query("apiKey") String apiKey, @Query("numberRows") int numberRows);

    @POST("/updatelisting")
    Call<ResponseBody> addNewListing(@Body Listing list, @Query("apiKey") String apiKey);

    @GET("/listings/{id}")
    Call<ResponseBody> getDetailedListing(@Path("id") String listingId, @Query("apiKey") String apiKey
                                     );

    //@GET("/listingsFiltered")
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

    @PATCH("/updatelisting")
    Call<ResponseBody> updateListing(@Body JSONObject updates);

    @Multipart
    @POST("/addimg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part image, @Query("apiKey") String apiKey);
//    @FormUrlEncoded
//    @POST("/addimg")
//    Call<ResponseBody> uploadImg(@Field("image") File img, @Query("apiKey") String apiKey);

}
