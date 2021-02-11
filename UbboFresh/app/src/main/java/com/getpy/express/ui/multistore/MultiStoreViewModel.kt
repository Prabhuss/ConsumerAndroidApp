package com.getpy.express.ui.multistore

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.MultiStoreResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.data.repositories.UserRepository
import retrofit2.http.Field

class MultiStoreViewModel( val preference:PreferenceProvider, val repository: UserRepository): ViewModel() {

    suspend fun getStoreDetailsforMultiStore (
            @Field("access_key")access_key:String,
            @Field("phone_number")phoneNumer:String,
            @Field("merchant_id")merchantId:Int
    ) : MultiStoreResponse = repository.getStoreDetailsforMultiStore(
            access_key,phoneNumer,merchantId
    )


}