package com.getpy.express.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.getpy.express.data.db.dao.CustomerAddressDao
import com.getpy.express.data.db.entities.CustomerAddressData
import com.getpy.express.data.db.entities.ProductsDataModel

@Database(entities = [CustomerAddressData::class,ProductsDataModel::class],version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun CustomerAddressDao():CustomerAddressDao

    companion object{
        @Volatile
        private  var instance : AppDataBase?=null
        private val LOCK = Any()
        operator fun invoke(context: Context)= instance?: synchronized(LOCK)
        {
            instance?:buildDataBase(context).also {
                instance=it
            }
        }
        private fun buildDataBase(context: Context)=
            Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java,
            "MyDataBase.db").build()

    }

}