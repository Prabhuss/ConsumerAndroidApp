package com.getpy.express.ui.account

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.*
import com.getpy.express.data.repositories.UserRepository

class AccountVewModel(val repository: UserRepository) :ViewModel()
{
    suspend fun getCustInfo(phone_number:Long,
                            access_key:String,
                            merchant_id:Int) =
        repository.getCustInfo(phone_number,access_key,merchant_id)
    suspend fun setCustInfo(
            phone_number:String?, secondphone_number:String?, merchant_id:Int?,
            first_name:String?, last_name:String?, device:String?, latitude:String?, longitude:String?,
            email_id:String?, address1:String?, address2:String?, city:String?, state:String?,
            country:String?, gst_number:String?, access_key:String?)=repository.setcustInfo(
            phone_number, secondphone_number, merchant_id, first_name,
            last_name, device, latitude, longitude, email_id, address1, address2, city, state,
            country, gst_number, access_key)
    suspend fun getContactDetails(merchantbranchid:String, phone_number:String, access_key:String):GetContactDetailsResponse
    {
        return getContactDetails(merchantbranchid, phone_number, access_key)
    }
    suspend fun getOrders(access_key:String,phone_number: String, merchant_id:Int,start_date:String,
                          page_size:Int,page_number:Int, end_date:String): GetCustInfoResponse
    {
        return getOrders(access_key,phone_number, merchant_id,start_date,page_size,page_number,end_date)
    }

    suspend fun merchantAppSettingDetails(mid:Int,setting_name:String,pnum:String,ackey:String):MerAppSettingsDetailsResponse
    {
        return repository.merchantAppSettingDetails(mid,setting_name,pnum,ackey)
    }

    suspend fun getGSTSettingDetails(merchant_id:Int,phone_number: String,access_key:String):GetGSTSettingDetails
    {
        return repository.getGSTSettingDetails(merchant_id,phone_number,access_key)
    }



}