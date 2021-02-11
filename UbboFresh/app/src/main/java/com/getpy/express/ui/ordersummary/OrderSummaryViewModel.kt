package com.getpy.express.ui.ordersummary

import androidx.lifecycle.ViewModel
import com.getpy.express.data.model.ReOrderInvoiceItems
import com.getpy.express.data.repositories.UserRepository

class OrderSummaryViewModel(val repository: UserRepository) : ViewModel()
{
    suspend fun reorderInvoiceItems(
        merchantbranchid:Int,
        phone_number:String,
        access_key:String,
        invoice_id:String): ReOrderInvoiceItems
    {
        return repository.reorderInvoiceItems(merchantbranchid, phone_number, access_key, invoice_id)
    }
}