package com.example.messagingapp;

import com.example.messagingapp.model.BiddingData;
import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;

import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Map;

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

/**
 * Interface ApiAccess for retrofit method specification
 */
public interface ApiAccess {

    /**
     * Retrofit specification for getting the details of a bidding listing
     *
     * @param aucId id of auction to look up in database
     * @param apiKey apikey
     * @return BiddingData object filled with data from database
     */
    @GET("/auction")
    Call<BiddingData> getAuctionDetails(@Query("aucId") int aucId, @Query("apiKey") String apiKey);

    /**
     * Retrofit specification to get details of for listing list
     *
     * @param apiKey apikey
     * @param numberRows number of rows of the database to return
     * @return arraylist of listfacades
     */
    @GET("/listings")
    Call<ArrayList<ListFacade>> getInfo(@Query("apiKey") String apiKey, @Query("numberRows") int numberRows);

    /**
     * Retrofit specification  to get the listings from the database with filters
     *
     * @param apikey apikey
     * @param json dictionary of filters
     * @return arraylist of listfacades
     */
    @POST("/listings")
    Call<ArrayList<ListFacade>> getFilteredInfo(@Query("apiKey") String apikey, @Body Map<String, ArrayList<String>> json);

    /**
     * Retrofit specification for posting a new listing
     *
     * @param list listing to upload to the database
     * @param apiKey apikey
     * @return status: ok if the listing got added to the database correctly else status: ERROR
     */
    @POST("/updatelisting")
    Call<ResponseBody> addNewListing(@Body Listing list, @Query("apiKey") String apiKey);

    /**
     * Retrofit specification for getting the full data of a single listing
     *
     *
     * @param listingId
     * @param apiKey
     * @return
     */
    @GET("/listings/{id}")
    Call<ResponseBody> getDetailedListing(@Path("id") String listingId, @Query("apiKey") String apiKey
                                     );

    /**
     * Retrofit specification to place a new bid
     *
     * @param aucId auction to bid on
     * @param bidVal amount to bid in cents
     * @param bidId id of user bidding
     * @param apiKey apikey
     * @return updated biddingdata object
     */
    @FormUrlEncoded
    @PATCH("/auction/{id}")
    Call<BiddingData> placeNewBid(
            @Path("id") int aucId,
            @Field("bidValue") int bidVal,
            @Field("bidderId") String bidId,
            @Field("apiKey") String apiKey
    );

    /**
     *  Retrofit specification to update a listing
     *
     * @param updates list of values to update
     * @param apiKey apikey
     * @return status: OK iff update worked, else status: ERROR
     */
    @PATCH("/updatelisting")
    Call<ResponseBody> updateListing(@Body JSONObject updates, @Query("apiKey") String apiKey);

    /**
     * Retrofit specification to upload an image
     *
     * @param image the image to upload, encoded as a multipartbody.part
     * @param apiKey apikey
     * @return status: OK iff update worked, else status: ERROR
     */
    @Multipart
    @POST("/addimg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part image, @Query("apiKey") String apiKey);

}
