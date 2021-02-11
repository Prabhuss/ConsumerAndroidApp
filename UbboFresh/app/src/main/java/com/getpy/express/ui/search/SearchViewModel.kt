package com.getpy.express.ui.search

import androidx.lifecycle.ViewModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.data.repositories.UserRepository

class SearchViewModel(val preference: PreferenceProvider, val respository:UserRepository) : ViewModel()
{

    suspend fun getSearchProducts(phnNum:String,
    accesskey:String,merchantId:Int,catgoryname:String,pageSize:Int,pageNum:Int)=respository.getProductBySearch(
            phnNum,accesskey,merchantId,catgoryname,pageSize,pageNum)
}