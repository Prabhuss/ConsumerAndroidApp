package com.getpy.express.ui.main

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.MainAndSubCatResponse
import com.getpy.express.data.model.MerAppSettingsDetailsResponse
import com.getpy.express.data.repositories.UserRepository
import retrofit2.http.Field

class MainViewModel (val repository: UserRepository):ViewModel()
{
    //we should pass "NULL" value as categoryid.(backend service is designed like this)
    //then it is main category
    //Instead of null if pass any id to categoryid the it is subcategories
    suspend fun MainAndSubGategory(
            @Field("access_key")access_key:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int,
            @Field("category_id")category_id:String
    ) : MainAndSubCatResponse = repository.subCategory(
            merchantId,category_id,phoneNumer,access_key
    )

    suspend fun getProducts(merchantId:Int,phnNum:String,
                            accesskey:String,catgoryname:String,pageSize:Int,pageNum:Int,lastSyncDate:String)=repository.getProductDetails(
        merchantId,phnNum,accesskey,catgoryname,pageSize,pageNum,lastSyncDate)

    suspend fun merchantAppSettingDetails(mid:Int,setting_name:String,pnum:String,ackey:String): MerAppSettingsDetailsResponse
    {
        return repository.merchantAppSettingDetails(mid,setting_name,pnum,ackey)
    }
}