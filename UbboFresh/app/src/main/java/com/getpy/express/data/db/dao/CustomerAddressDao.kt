package com.getpy.express.data.db.dao

import androidx.room.*
import com.getpy.express.data.db.entities.CustomerAddressData
import com.getpy.express.data.db.entities.ProductsDataModel

@Dao
interface CustomerAddressDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertcustomerAddrData(customeraddressdata:List<CustomerAddressData>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductsData(productsDataModel:MutableList<ProductsDataModel>)

    @Delete
    suspend fun deleteProductsData(productsDataModel:ProductsDataModel)

    @Delete
    suspend fun deleteListOfProductsData(productsDataModel:MutableList<ProductsDataModel>)

    @Query("SELECT *FROM customeraddressdata where  PrimaryPhone IN (:PrimaryPhone) AND MerchantBranchId IN (:MerchantBranchId)")
    suspend fun getCustAddrData(PrimaryPhone:String,MerchantBranchId:Int):List<CustomerAddressData>

    @Query("SELECT *FROM ProductsDataModel where MerchantBranchId IN (:merchantId) AND PrimaryPhone IN (:PrimaryPhone)")
    suspend fun getCartData(merchantId:String,PrimaryPhone:String):MutableList<ProductsDataModel>
}