package com.getpy.express.ui.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.getpy.fresh.views.home.OffersViewModel
import com.getpy.express.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class OffersViewModelFactory(
    private val repository: UserRepository):ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OffersViewModel(repository) as T
    }
}