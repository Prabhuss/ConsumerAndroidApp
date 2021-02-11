package com.getpy.express.data.model

import com.getpy.express.data.db.entities.ProductsDataModel
import java.util.ArrayList

class ReOrderInvoiceItems {
    var data: ArrayList<ProductsDataModel>? = null
    var status: String? = null
}