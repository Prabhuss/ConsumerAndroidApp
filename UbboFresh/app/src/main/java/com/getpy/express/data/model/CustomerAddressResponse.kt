package com.getpy.express.data.model

import com.getpy.express.data.db.entities.CustomerAddressData
import java.util.*

data class CustomerAddressResponse (
        var data: ArrayList<CustomerAddressData>?,
        var status: String?
)