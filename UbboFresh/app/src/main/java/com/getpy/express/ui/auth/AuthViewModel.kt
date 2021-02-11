package com.getpy.express.ui.auth

import android.app.Application
import android.text.Html
import android.text.Spanned
import androidx.lifecycle.AndroidViewModel
import com.getpy.express.data.model.AuthResponse
import com.getpy.express.data.model.BaseUrlResponse
import com.getpy.express.data.model.MultiStoreResponse
import com.getpy.express.data.model.OtpValidateResponse
import com.getpy.express.data.repositories.UserRepository
import retrofit2.http.Field

class AuthViewModel(application: Application,private val repository: UserRepository) :AndroidViewModel(application)
{
    private val context = getApplication<Application>().applicationContext

    suspend fun generateOtp(phnNum:String,merchantId:Int,hashkey:String,regtoken:String):AuthResponse=
        repository.generateOtp(phnNum,merchantId,hashkey,regtoken)

    suspend fun generateBaseUrl(params: Map<String, String>):BaseUrlResponse=
            repository.generateBaseUrl(params)

    suspend fun verifyOtp(otp:String,phnNum: String,merchantId: Int):OtpValidateResponse=repository.verifyOtp(
            otp,phnNum,merchantId
    )

    fun getHtmlText(str: String,replacestr:String):Spanned
    {
        val hext = str.replace(replacestr,"<font color='#B02B18'>"+replacestr +"</font>");
        return Html.fromHtml(hext)
    }

    suspend fun getStoreDetailsforMultiStore (
            @Field("access_key")access_key:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int
    ) : MultiStoreResponse = repository.getStoreDetailsforMultiStore(
            access_key,phoneNumer,merchantId
    )

}