package com.getpy.express.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CustomerInvoiceData(
    var TotalInvoiceAmount: String? = null,
    var CustomerVehicleId: String? = null,
    var LabourAmount: String? = null,
    var InvoiceId: String? = null,
    var ModifiedDate: String? = null,
    var ModifiedBy: String? = null,
    var PayableAmount: String? = null,
    var TypeOfRoom: String? = null,
    var RedeemedPoints: String? = null,
    var TaxAmount: String? = null,
    var OrderStatus: String? = null,
    var TotalDays: String? = null,
    var Deliveryboy_id: String? = null,
    var InvoiceDate: String? = null,
    var CustomerInvoiceId: String? = null,
    var StoreCustomerId: String? = null,
    var PartsAmount: String? = null,
    var CreatedBy: String? = null,
    var EarnedPoints: String? = null,
    var DeliverAddressId: String? = null,
    var BranchId: String? = null,
    var PDFPath: String? = null,
    var DiscountAmount: String? = null,
    var InvoiceType: String? = null,
    var CouponCode: String? = null,
    var PosId: String? = null,
    var CreatedDate: String? = null,
    var MerchantBranchId: String? = null,
    var PaymentMode: String? = null
): Parcelable