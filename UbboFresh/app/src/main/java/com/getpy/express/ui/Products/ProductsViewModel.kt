package com.getpy.express.ui.Products

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.MainAndSubCatResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.data.repositories.UserRepository
import retrofit2.http.Field

class ProductsViewModel(val preference: PreferenceProvider,val respository:UserRepository) : ViewModel()
{

    suspend fun MainAndSubGategory(
            @Field("access_key")access_key:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int,
            @Field("category_id")category_id:String
    ) : MainAndSubCatResponse = respository.subCategory(
            merchantId,category_id,phoneNumer,access_key
    )

    suspend fun getProducts(merchantId:Int,phnNum:String,
    accesskey:String,catgoryname:String,pageSize:Int,pageNum:Int,lastSyncDate:String)=respository.getProductDetails(
            merchantId,phnNum,accesskey,catgoryname,pageSize,pageNum,lastSyncDate)
}