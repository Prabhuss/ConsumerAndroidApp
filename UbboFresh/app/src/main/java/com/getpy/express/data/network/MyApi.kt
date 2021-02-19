package com.getpy.express.data.network

import com.google.gson.JsonObject
import com.getpy.express.Utils.Constants
import com.getpy.express.data.model.CustomerAddressResponse
import com.getpy.express.data.model.*
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface MyApi {
    @FormUrlEncoded
    @POST("v2/auth/generateOtpMultiStore")
    suspend fun generateOtp(
        @Field("phone_number")phoneNumer:String,
        @Field("merchant_id")merchantId:Int,
        @Field("hash_key")hash_key:String,
        @Field("reg_token")reg_token:String
    ) : Response<AuthResponse>

    @FormUrlEncoded
    @POST("v2/auth/otpValidateforMultiStore")
    suspend fun otpValidate (
            @Field("otp")otp:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int
    ) : Response<OtpValidateResponse>

    @FormUrlEncoded
    @POST("v2/auth/getStoreDetailsforMultiStore")
    suspend fun getStoreDetailsforMultiStore (
            @Field("access_key")access_key:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int
    ) : Response<MultiStoreResponse>


    @GET("v2/merchant/getBaseURL")
    suspend fun getBaseURL (@QueryMap options: Map<String, String>) : Response<BaseUrlResponse>

    @GET("v2/auth/getcustInfo")
    suspend fun getCustInfo (@Query("phone_number")phone_number:Long,
                             @Query("access_key")access_key:String,
                             @Query("merchant_id")merchant_id:Int
    ) : Response<GetCustInfoResponse>

    @FormUrlEncoded
    @POST("v2/auth/SubCategory")
    suspend fun subCategory (
            @Field("merchant_id")merchant_id:Int,
            @Field("category_id")category_id:String,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ) : Response<MainAndSubCatResponse>


    @FormUrlEncoded
    @POST("v2/auth/getGSTSettingDetails")
    suspend fun getGSTSettingDetails (
            @Field("merchantbranchid")merchant_id:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ) : Response<GetGSTSettingDetails>

    @FormUrlEncoded
    @POST("v2/prod/getProductDetails")
    suspend fun getProductDetails(
            @Field("merchant_id")merchant_id:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String,
            @Field("category_name")category_id:String,
            @Field("page_size")page_size:Int,
            @Field("page_number")page_number:Int,
            @Field("lastSyncDate")lastSyncDate:String
    ) : Response<ProductsResponse>

    @FormUrlEncoded
    @POST("v2/prod/getProductBySearch")
    suspend fun getProductBySearch(
        @Field("phone_number")phone_number:String,
        @Field("access_key")access_key:String,
        @Field("merchant_id")merchant_id:Int,
        @Field("product_name")category_id:String,
        @Field("page_size")page_size:Int,
        @Field("page_number")page_number:Int
    ) : Response<ProductsResponse>

    @FormUrlEncoded
    @POST("v2/order/GetOrder")
    suspend fun getOrders(
        @Field("access_key")access_key:String,
        @Field("phone_number") phone_number: String,
        @Field("merchant_id") merchant_id:Int,
        @Field("start_date")start_date:String,
        @Field("page_size") page_size:Int,
        @Field("page_number")page_number:Int,
        @Field("end_date")end_date:String
    ):Response<GetOrderResponse>

    @FormUrlEncoded
    @POST("v2/auth/getContactDetails")
    suspend fun getContactDetails(
         @Field("merchantbranchid")merchantbranchid:Int,
         @Field("phone_number")phone_number:String,
         @Field("access_key")access_key:String
    ):Response<GetContactDetailsResponse>

    @FormUrlEncoded
    @POST("v2/auth/setcustInfo")
    suspend fun setcustInfo(
    @Field("phone_number")phone_number:String?,
    @Field("secondphone_number")secondphone_number:String?,
    @Field("merchant_id")merchant_id:Int?,
    @Field("first_name")first_name:String?,
    @Field("last_name")last_name:String?,
    @Field("device")device:String?,
    @Field("latitude")latitude:String?,
    @Field("longitude")longitude:String?,
    @Field("email_id")email_id:String?,
    @Field("address1")address1:String?,
    @Field("address2")address2:String?,
    @Field("city")city:String?,
    @Field("state")state:String?,
    @Field("country")country:String?,
    @Field("gst_number")gst_number:String?,
    @Field("access_key")access_key:String?):Response<SetCustInfoResponse>


    @FormUrlEncoded
    @POST("v2/auth/getSlidingImages")
    suspend fun getSlidingImages(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<SlidingimagesResponse>

    @FormUrlEncoded
    @POST("v2/auth/getOfferDetails")
    suspend fun getOfferDetails(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<GetOfferDetailsResponse>

    @FormUrlEncoded
    @POST("v2/auth/getOfferProducts")
    suspend fun getOfferProducts(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String,
            @Field("offer_code")offer_code:String
    ):Response<ProductsResponse>

    @FormUrlEncoded
    @POST("v2/auth/getoOfferTiles")
    suspend fun getoOfferTiles(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<GetOfferTailsResponse>


    @FormUrlEncoded
    @POST("v2/auth/getopproducts")
    suspend fun getopproducts(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<ProductsResponse>

    @FormUrlEncoded
    @POST("v2/auth/GetActiveList2")
    suspend fun GetActiveList2(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<ProductsResponse>


    @FormUrlEncoded
    @POST("v2/merchant/MerchantAppSettingDetails")
    suspend fun merchantAppSettingDetails(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("setting_name")setting_name:String,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<MerAppSettingsDetailsResponse>

    @FormUrlEncoded
    @POST("v2/merchant/MerchantAppSettingDetails")
    suspend fun merchantAppSettingDetails1(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("setting_name")setting_name:String,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String
    ):Response<MerAppSettingsDetailsResponse>



    @FormUrlEncoded
    @POST("v2/auth/SetCustomerAddress")
    suspend fun setcustomerAddress(
        @Field("access_key")access_key:String?,
        @Field("merchant_id")merchant_id:Int?,
        @Field("phone_number")phone_number:String?,
        @Field("secondphone_number")secondphone_number:String?,
        @Field("address1")address1:String?,
        @Field("address2")address2:String?,
        @Field("longitude")longitude:String?,
        @Field("latitude")latitude:String?,
        @Field("tag_name")tag_name:String?,
        @Field("first_name")first_name:String?,
        @Field("society_buildingNo")society_buildingNo:String?,
        @Field("flatno_doorno")flatno_doorno:String?,
        @Field("city")city:String?,
        @Field("state")state:String?,
        @Field("country")country:String?,
        @Field("area")area:String?,
        @Field("postalcode/zipcode")postalcode_zipcode:String?):Response<ResponseBody>

    @FormUrlEncoded
    @POST("v2/auth/getCustomerAddress ")
    suspend fun getCustomerAddress(
        @Field("merchant_id")merchantbranchid:Int,
        @Field("phone_number")phone_number:String,
        @Field("access_key")access_key:String
    ):Response<CustomerAddressResponse>


    @FormUrlEncoded
    @POST("v2/auth/getDistance")
    suspend fun getDistance(
        @Field("merchant_id")merchantbranchid:Int,
        @Field("latitude")latitude:String?,
        @Field("longitude")longitude:String?,
        @Field("access_key")access_key:String,
        @Field("phone_number")phone_number:String):Response<GetDisanceResponse>

    @FormUrlEncoded
    @POST("v2/auth/reorderInvoiceItems")
    suspend fun reorderInvoiceItems(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String,
            @Field("invoice_id")invoice_id:String
    ):Response<ReOrderInvoiceItems>

    @FormUrlEncoded
    @POST("v2/auth/campaignCustomerNotificationDetails")
    suspend fun campaignCustomerNotificationDetails(
            @Field("merchant_id")merchantbranchid:Int,
            @Field("phone_number")phone_number:String,
            @Field("access_key")access_key:String,
            @Field("page_size")page_size:Int,
            @Field("page_number")page_number:Int
    ):Response<CampaignCustomerNotificationDetails>

    @POST("v2/order/CreateOrderNew")
    suspend fun CreateOrder (@Body gson: JsonObject):Response<OrderResponse>

    companion object{
        operator fun invoke(networkConnectionInterceptor: NetworkConnectionInterceptor): MyApi
        {
            val okHttpClient=OkHttpClient.Builder()
                    .connectTimeout(30,TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)

                .addInterceptor(networkConnectionInterceptor).build()
            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(Constants.baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}