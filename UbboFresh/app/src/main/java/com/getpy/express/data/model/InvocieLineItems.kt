package com.getpy.express.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InvocieLineItems (
    var Discount: String? = null,
    var CreatedBy: String? = null,
    var ProductName: String? = null,
    var CustomerInvoiceLineItemId: String? = null,
    var ProductImage: String? = null,
    var Quantity: String? = null,
    var ProductId: String? = null,
    var UnitPrice: String? = null,
    var CouponCode: String? = null,
    var ProductName2: String? = null,
    var InStock: String? = null,
    var TotalPrice: String? = null,
    var CreatedDate: String? = null,
    var UnitPriceAfterDiscount: String? = null,
    var CustomerInvoiceId: String? = null,
    var Category: String? = null,
    var MerchantBranchId: String? = null
): Parcelable