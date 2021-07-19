package com.peerbits.base.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PartMap
import retrofit2.http.Url
import java.util.HashMap

interface ApiInterface {

    @POST
    fun APICall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @Body requestClass: Any): Call<ResponseBody>

    @PUT
    fun APIPutCall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @Body requestClass: Any): Call<ResponseBody>

    @FormUrlEncoded
    @Headers("Content-Type: application/json"/*,"Content-Type: application/x-www-form-urlencoded"*/)
    @POST
    fun APICall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @FieldMap fields: HashMap<String, String>): Call<ResponseBody>

    @Multipart
    @POST
    fun APIMultipartCall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>, @PartMap fields: HashMap<String, RequestBody>): Call<ResponseBody>

    @POST
    fun APIBinaryCall(@Url endPoint: String, @Body photo : RequestBody): Call<ResponseBody>

    @GET
    fun APICall(@Url endPoint: String, @HeaderMap hashMap: HashMap<String, String>): Call<ResponseBody>

    @GET
    fun getMethod(@Url endpoint: String): Call<ResponseBody>

    /*@GET(GET_VEHICLE_YEAR)
    fun APIVehicleYear(@Path(value = "VehicleModelId") path : String, @HeaderMap hashMap: HashMap<String, String>): Call<ResponseBody>
*/
}
