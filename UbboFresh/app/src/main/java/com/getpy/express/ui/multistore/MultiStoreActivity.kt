package com.getpy.express.ui.multistore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.MultiStoreAdater
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.model.MultiStoreDataModel
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityStoreBinding
import com.getpy.express.ui.main.MainActivity
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MultiStoreActivity : AppCompatActivity(),KodeinAware  {
    override val kodein by kodein()
    private val databse: AppDataBase by instance()
    private val factory: MultiStoreViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var binding: ActivityStoreBinding
    lateinit var viewModel: MultiStoreViewModel
    var adapter:MultiStoreAdater?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_store)
        viewModel= ViewModelProviders.of(this,factory).get(MultiStoreViewModel::class.java)
        init()
        binding.recyclerView.setLayoutManager(LinearLayoutManager(this))
        binding.recyclerView.setHasFixedSize(true)
        binding.pbar.show()
        callMutistore()
    }
    fun init()
    {
        binding.selectStore.setTypeface(UbboFreshApp.instance?.latobold)
        binding.welcome.setTypeface(UbboFreshApp.instance?.latobold)
        binding.welcome1.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.pullDownTxt.setTypeface(UbboFreshApp.instance?.latoregular)
    }

    fun callCategories(model: MultiStoreDataModel)
    {
        preference.saveBoolData(Constants.saveMultistore,false)
        preference.saveBoolData(Constants.savelogin,true)
        preference.saveStringData(Constants.saveStorename,model.NameofStore.toString())
        model.mid?.let { preference.saveIntData(Constants.saveMerchantIdKey, it) }
        val map=HashMap<String,String>()
        map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
        map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
        map.put("storename", model.NameofStore.toString())
        Analytics.trackEvent("multistore clicked", map)
        lifecycleScope.launch {
            UbboFreshApp.instance?.carItemsList=databse.CustomerAddressDao().getCartData(preference.getIntData(Constants.saveMerchantIdKey).toString(),preference.getStringData(Constants.saveMobileNumkey))
            if(UbboFreshApp.instance?.carItemsList!!.size>0)
            {
                UbboFreshApp.instance?.hashMap?.clear()
                for(i in 0 until UbboFreshApp.instance?.carItemsList!!.size) {
                    val m = UbboFreshApp.instance?.carItemsList?.get(i)
                    m?.citrineProdId?.let { UbboFreshApp.instance?.hashMap?.put(it,m) }
                }
            }else
            {
                UbboFreshApp.instance?.hashMap?.clear()
            }
            val intent=Intent(this@MultiStoreActivity,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }


    fun callMutistore()
    {
        val jsonobject=JSONObject()
        jsonobject.put("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonobject.put("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonobject.put("merchant_id", Constants.merchantid)

        Log.i("getMultipleStoreDetails",jsonobject.toString())
         lifecycleScope.launchWhenCreated {
             try {


                 val multires=viewModel.getStoreDetailsforMultiStore(
                         preference.getStringData(Constants.saveaccesskey),
                         preference.getStringData(Constants.saveMobileNumkey),
                         Constants.merchantid)
                 binding.pbar.dismiss()
                 if(multires.status?.toLowerCase().equals(Constants.status))
                 {
                     adapter= multires.data?.let { MultiStoreAdater(this@MultiStoreActivity,it,object:MultiStoreAdater.OnItemClickListener{
                         override fun onItemClick(item: MultiStoreDataModel?) {
                             item?.let { it1 -> callCategories(it1) }
                         }
                     }) }
                     binding.recyclerView.adapter=adapter
                 }else
                 {
                     if(multires.message?.toLowerCase().equals(Constants.message))
                     {
                         okDialogWithNavigateToLogin(this@MultiStoreActivity,preference,Constants.appName,multires.message.toString())
                     }else
                     {
                         okDialogWithOneAct(Constants.appName,multires.message.toString())
                     }

                 }

             }catch (e: NoInternetExcetion)
             {
                 binding.pbar.dismiss()
                 networkDialog()
             }catch (e:Exception)
             {
                 binding.pbar.dismiss()
                 okDialogWithOneAct("Error",e.message.toString())
             }

         }
    }
}