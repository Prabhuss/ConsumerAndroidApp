package com.getpy.express.data.model

class GetGSTSettingDetails {
    var data: GstData? = null
    var status: String? = null

    override fun toString(): String {
        return "ClassPojo [data = $data, status = $status]"
    }
}