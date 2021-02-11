package com.getpy.fresh.views.home

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.*
import com.getpy.express.data.repositories.UserRepository

class OffersViewModel(val repository: UserRepository) : ViewModel() {

    suspend fun getOfferDetails(mid:Int,pnum:String,ackey:String): GetOfferDetailsResponse
    {
        return repository.getOfferDetails(mid,pnum,ackey)
    }
    suspend fun getofferProduct(mid:Int,pnum:String,ackey:String,offercode:String): ProductsResponse
    {
        return repository.getofferProduct(mid,pnum,ackey,offercode)
    }

}