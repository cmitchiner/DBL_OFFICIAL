package com.example.messagingapp.network;

import com.example.messagingapp.model.ListFacade;
import com.example.messagingapp.model.Listing;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
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
     * Retrofit specification to get details of for listing list
     *
     * @param apiKey     apikey
     * @param numberRows number of rows of the database to return
     * @return arraylist of listfacades
     */
    @GET("/listings")
    Call<ArrayList<ListFacade>> getInfo(@Query("apiKey") String apiKey, @Query("numberRows") int numberRows);

    /**
     * Retrofit specification  to get the listings from the database with filters
     *
     * @param apikey apikey
     * @param json   dictionary of filters
     * @return arraylist of listfacades
     */
    @POST("/listings")
    Call<ArrayList<ListFacade>> getFilteredInfo(@Query("apiKey") String apikey, @Body Map<String, ArrayList<String>> json);

    /**
     * Retrofit specification for posting a new listing
     *
     * @param list   listing to upload to the database
     * @param apiKey apikey
     * @return status: ok if the listing got added to the database correctly else status: ERROR
     */
    @POST("/updatelisting")
    Call<ResponseBody> addNewListing(@Body Listing list, @Query("apiKey") String apiKey);

    /**
     * Retrofit specification for getting the full data of a single listing
     *
     * @param listingId
     * @param apiKey
     * @return
     */
    @GET("/listings/{id}")
    Call<ResponseBody> getDetailedListing(@Path("id") String listingId, @Query("apiKey") String apiKey);

    /**
     * Retrofit specification to update a listing
     *
     * @param updates list of values to update
     * @param apiKey  apikey
     * @return status: OK iff update worked, else status: ERROR
     */
    @PATCH("/updatelisting")
    Call<ResponseBody> updateListing(@Body JSONObject updates, @Query("apiKey") String apiKey);

    /**
     * Retrofit specification to upload an image
     *
     * @param image  the image to upload, encoded as a multipartbody.part
     * @param apiKey apikey
     * @return status: OK iff update worked, else status: ERROR
     */
    @Multipart
    @POST("/addimg")
    Call<ResponseBody> uploadImg(@Part MultipartBody.Part image, @Query("apiKey") String apiKey);

}
