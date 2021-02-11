package com.getpy.fresh.views.home

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.*
import com.getpy.express.data.repositories.UserRepository
import retrofit2.http.Field

class HomeViewModel(val repository: UserRepository) : ViewModel() {
    suspend fun MainAndSubGategory(
            @Field("access_key")access_key:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int,
            @Field("category_id")category_id:String
    ) : MainAndSubCatResponse = repository.subCategory(
            merchantId,category_id,phoneNumer,access_key
    )

    suspend fun callSlidingImages(mid:Int,pnum:String,ackey:String): SlidingimagesResponse
    {
        return repository.callSlidingImages(mid,pnum,ackey)
    }

    suspend fun getOfferDetails(mid:Int,pnum:String,ackey:String): GetOfferDetailsResponse
    {
        return repository.getOfferDetails(mid,pnum,ackey)
    }
    suspend fun getofferProduct(mid:Int,pnum:String,ackey:String,offercode:String): ProductsResponse
    {
        return repository.getofferProduct(mid,pnum,ackey,offercode)
    }
    suspend fun getOfferTails(mid:Int,pnum:String,ackey:String): GetOfferTailsResponse
    {
        return repository.getOfferTails(mid,pnum,ackey)
    }
    suspend fun getTopDetails(mid:Int,pnum:String,ackey:String): ProductsResponse
    {
        return repository.getTopDetails(mid,pnum,ackey)
    }
    suspend fun getDealsForyou(mid:Int,pnum:String,ackey:String): ProductsResponse
    {
        return repository.getDealsForyou(mid,pnum,ackey)
    }
}