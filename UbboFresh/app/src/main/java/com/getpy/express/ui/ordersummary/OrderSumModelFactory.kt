package com.getpy.express.ui.ordersummary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.getpy.express.data.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class OrderSumModelFactory(
    private val repository: UserRepository):ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OrderSummaryViewModel(repository) as T
    }
}