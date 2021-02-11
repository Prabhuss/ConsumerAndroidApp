package com.getpy.express.data.model

class GstData {
    var ceatedby: String? = null
    var gST_Active: String? = null
    var createdDate: String? = null
    var id: String? = null
    var modifiedDate: String? = null
    var merchantBranchId: String? = null

    override fun toString(): String {
        return "ClassPojo [Ceatedby = " + ceatedby + ", GST_Active = " + gST_Active + ", CreatedDate = " + createdDate + ", id = " + id + ", ModifiedDate = " + modifiedDate + ", MerchantBranchId = " + merchantBranchId + "]"
    }
}