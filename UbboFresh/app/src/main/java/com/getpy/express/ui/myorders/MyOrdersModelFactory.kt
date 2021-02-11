package com.getpy.express.ui.myorders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.getpy.express.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class MyOrdersModelFactory(
    private val repository: UserRepository):ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllOrdersViewModel(repository) as T
    }
}