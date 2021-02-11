package com.getpy.express.ui.cart

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.getpy.express.data.model.CustomerAddressResponse
import com.getpy.express.data.model.GetDisanceResponse
import com.getpy.express.data.model.MerAppSettingsDetailsResponse
import com.getpy.express.data.model.OrderResponse
import com.getpy.express.data.repositories.UserRepository
import okhttp3.ResponseBody

class CartViewModel(val repository: UserRepository) : ViewModel()
{

   suspend fun setCustomerAddress(
       access_key:String,
       merchant_id:Int?,
       phone_number:String?,
       secondphone_number:String?,
       address1:String?,
       address2:String?,
       longitude:String?,
       latitude:String?,
       tag_name:String?,
       first_name:String?,
       society_buildingNo:String?,
       flatno_doorno:String?,
       city:String?,
       state:String?,
       country:String?,
       area:String?,
       postalcode_zipcode:String?):ResponseBody
   {
        return repository.setCustomerAddress(access_key, merchant_id, phone_number, secondphone_number,
            address1, address2, longitude, latitude, tag_name, first_name,
            society_buildingNo, flatno_doorno, city, state, country,
            area, postalcode_zipcode)
   }


   suspend fun getCustomerAddress(merchantbranchid:Int, phone_number:String, access_key:String): CustomerAddressResponse
   {
       return repository.getCustomerAddress(merchantbranchid, phone_number, access_key)
   }

   suspend fun getDistance(merchantbranchid:Int,
                           latitude:String?,
                           longitude:String?,
                           access_key:String,
                           phone_number:String):GetDisanceResponse
   {
       return repository.getDistance(merchantbranchid,latitude,longitude,access_key,phone_number)
   }
    suspend fun merchantAppSettingDetails(mid:Int,setting_name:String,pnum:String,ackey:String): MerAppSettingsDetailsResponse
    {
        return repository.merchantAppSettingDetails1(mid,setting_name,pnum,ackey)
    }
   suspend fun createOrder(gson: JsonObject):OrderResponse
   {
       return repository.createOrder(gson)
   }
}