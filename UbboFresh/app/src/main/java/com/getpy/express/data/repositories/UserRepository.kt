package com.getpy.express.data.repositories

import com.google.gson.JsonObject
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.model.CustomerAddressResponse
import com.getpy.express.data.model.*
import com.getpy.express.data.network.MyApi
import com.getpy.express.data.network.SafeApiRequest
import okhttp3.ResponseBody

class UserRepository(val api:MyApi, val db:AppDataBase):SafeApiRequest(){

    suspend fun generateOtp(phoneNumber:String, merchantId:Int,hashkey:String,regtoken:String) : AuthResponse
    {
        return  apiRequest{api.generateOtp(phoneNumber,merchantId,hashkey,regtoken)}
    }

    suspend fun verifyOtp(otp:String,phoneNumber:String, merchantId:Int) : OtpValidateResponse
    {
        return  apiRequest {api.otpValidate(otp,phoneNumber,merchantId)}
    }

    suspend fun generateBaseUrl(params: Map<String, String>) : BaseUrlResponse
    {

        return  apiRequest { api.getBaseURL(params)}
    }

    suspend fun getStoreDetailsforMultiStore
            (access_key:String, phoneNumer:String, merchantId:Int) : MultiStoreResponse
    {
        return apiRequest {api.getStoreDetailsforMultiStore(access_key,phoneNumer,merchantId)}
    }

    suspend fun subCategory
            (merchant_id:Int, category_id:String, phone_number:String, access_key:String) :MainAndSubCatResponse
    {
        return apiRequest { api.subCategory(merchant_id,category_id,phone_number,access_key)}
    }

    suspend fun getProductDetails(
            merchant_id:Int,
            phone_number:String,
            access_key:String,categoryname:String,page_size:Int, page_number:Int,lastSyncDate:String):ProductsResponse
    {
        return apiRequest {api.getProductDetails(merchant_id,phone_number,access_key,categoryname,page_size,page_number,lastSyncDate)}
    }
    suspend fun getProductBySearch(
        phone_number:String,
        access_key:String,merchant_id:Int,categoryname:String,page_size:Int, page_number:Int):ProductsResponse
    {
        return apiRequest {api.getProductBySearch(phone_number,access_key,merchant_id,categoryname,page_size,page_number)}
    }

    suspend fun getContactDetails(merchantbranchid:Int, phone_number:String, access_key:String): GetContactDetailsResponse
    {
        return apiRequest { api.getContactDetails(merchantbranchid, phone_number, access_key) }
    }

    suspend fun getOrders(access_key:String,phone_number: String, merchant_id:Int,start_date:String,
            page_size:Int,page_number:Int, end_date:String): GetOrderResponse
    {
        return apiRequest { api.getOrders(access_key,phone_number, merchant_id,start_date,
                page_size,page_number,end_date) }
    }

    suspend fun getCustInfo (phone_number:Long,
                             access_key:String,
                             merchant_id:Int) : GetCustInfoResponse
    {
        return apiRequest { api.getCustInfo(phone_number, access_key, merchant_id) }
    }

    suspend fun setcustInfo(phone_number:String?, secondphone_number:String?, merchant_id:Int?,
        first_name:String?, last_name:String?, device:String?, latitude:String?, longitude:String?,
        email_id:String?, address1:String?, address2:String?, city:String?, state:String?,
        country:String?, gst_number:String?, access_key:String?):SetCustInfoResponse
    {
        return apiRequest { api.setcustInfo(phone_number, secondphone_number, merchant_id, first_name,
                last_name, device, latitude, longitude, email_id, address1, address2, city, state,
                country, gst_number, access_key) }
    }
    suspend fun callSlidingImages(mid:Int,pnum:String,ackey:String):SlidingimagesResponse
    {
        return apiRequest { api.getSlidingImages(mid,pnum,ackey) }
    }

    suspend fun getOfferDetails(mid:Int,pnum:String,ackey:String):GetOfferDetailsResponse
    {
        return apiRequest { api.getOfferDetails(mid,pnum,ackey) }
    }
    suspend fun getofferProduct(mid:Int,pnum:String,ackey:String,offercode:String):ProductsResponse
    {
        return apiRequest { api.getOfferProducts(mid,pnum,ackey,offercode) }
    }
    suspend fun getOfferTails(mid:Int,pnum:String,ackey:String):GetOfferTailsResponse
    {
        return apiRequest { api.getoOfferTiles(mid,pnum,ackey) }
    }
    suspend fun getTopDetails(mid:Int,pnum:String,ackey:String):ProductsResponse
    {
        return apiRequest { api.getopproducts(mid,pnum,ackey) }
    }
    suspend fun getDealsForyou(mid:Int,pnum:String,ackey:String):ProductsResponse
    {
        return apiRequest { api.GetActiveList2(mid,pnum,ackey) }
    }

    suspend fun merchantAppSettingDetails(mid:Int,setting_name:String,pnum:String,ackey:String):MerAppSettingsDetailsResponse
    {
        return apiRequest { api.merchantAppSettingDetails(mid,setting_name,pnum,ackey) }
    }
    suspend fun getGSTSettingDetails(merchant_id:Int,phone_number: String,access_key:String):GetGSTSettingDetails
    {
        return apiRequest { api.getGSTSettingDetails(merchant_id,phone_number,access_key) }
    }
    suspend fun merchantAppSettingDetails1(mid:Int,setting_name:String,pnum:String,ackey:String):MerAppSettingsDetailsResponse
    {
        return apiRequest { api.merchantAppSettingDetails1(mid,setting_name,pnum,ackey) }
    }

    suspend fun setCustomerAddress(access_key:String, merchant_id:Int?, phone_number:String?, secondphone_number:String?,
        address1:String?, address2:String?, longitude:String?, latitude:String?, tag_name:String?, first_name:String?,
        society_buildingNo:String?, flatno_doorno:String?, city:String?, state:String?, country:String?,
        area:String?, postalcode_zipcode:String?):ResponseBody
    {
         return apiRequest { api.setcustomerAddress(access_key, merchant_id, phone_number, secondphone_number,
             address1, address2, longitude, latitude, tag_name, first_name,
             society_buildingNo, flatno_doorno, city, state, country,
             area, postalcode_zipcode) }
    }

    suspend fun getCustomerAddress(merchantbranchid:Int, phone_number:String, access_key:String): CustomerAddressResponse
    {
        return apiRequest { api.getCustomerAddress(merchantbranchid, phone_number, access_key) }
    }

    suspend fun getDistance(merchantbranchid:Int, latitude:String?,
                            longitude:String?, access_key:String, phone_number:String):GetDisanceResponse
    {
        return apiRequest { api.getDistance(merchantbranchid,latitude,longitude,access_key,phone_number) }
    }
    suspend fun createOrder(gson: JsonObject):OrderResponse
    {
        return apiRequest { api.CreateOrder(gson) }
    }

    suspend fun reorderInvoiceItems(
       merchantbranchid:Int,
       phone_number:String,
       access_key:String,
       invoice_id:String):ReOrderInvoiceItems
    {
        return apiRequest { api.reorderInvoiceItems(merchantbranchid, phone_number, access_key, invoice_id) }
    }


    suspend fun campaignCustomerNotificationDetails(
        merapchantbranchid:Int,
        phone_number:String,
        access_key:String,
        page_size:Int,
        page_number:Int
    ):CampaignCustomerNotificationDetails
    {
        return apiRequest {api.campaignCustomerNotificationDetails(
            merapchantbranchid, phone_number, access_key, page_size, page_number)  }
    }
}