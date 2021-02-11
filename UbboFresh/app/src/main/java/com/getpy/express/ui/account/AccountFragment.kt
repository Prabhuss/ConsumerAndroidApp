package com.getpy.express.ui.account

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.data.model.GetCustInfoResponse
import com.getpy.express.data.model.MerAppSettingsDetailsResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentAccountBinding
import com.getpy.express.ui.contactmerchant.ContactMerchantActivity
import com.getpy.express.ui.editprofie.EditProfileActivity
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.main.MainActivity
import com.getpy.express.ui.myorders.MyOrdersActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.generic.instance


class AccountFragment : InjectionFragment(),View.OnClickListener{
    companion object{
        var runnable:Runnable?=null
    }
    lateinit var binding: FragmentAccountBinding
    lateinit var viewmodel:AccountVewModel
    private val factory: AccountViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    private var custInfo:GetCustInfoResponse?=null
    private var tcmodel:MerAppSettingsDetailsResponse?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       binding = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.fragment_account, container, false)
       viewmodel= activity?.let { ViewModelProviders.of(it,factory).get(AccountVewModel::class.java) }!!
       val view=binding.root
        MainActivity.binding.activityMainAppbarlayout.showView()
       MainActivity.binding.selectStore.hideView()
       MainActivity.binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
       MainActivity.binding.activityMainToolbarTitle.setText("Accounts")

       binding.editLayout.setOnClickListener(this)
       binding.orderLayout.setOnClickListener(this)
       binding.switchStoreLayout.setOnClickListener(this)
       binding.contactMerchant.setOnClickListener(this)
       binding.logoutLayout.setOnClickListener(this)
       binding.termsPolicy.setOnClickListener(this)
       binding.privacyPolicy.setOnClickListener(this)
       binding.shippingPolicy.setOnClickListener(this)
       binding.returnPolicy.setOnClickListener(this)

        runnable = Runnable {
            if(MainActivity.navcontroller?.currentDestination?.id==R.id.accountFragment) {
                MainActivity.navcontroller?.navigate(R.id.action_accountFragment_to_productsFragment)
            }
        }

       init()
       getTermsAndCndUrl()
       return view
    }

    override fun onResume() {
        super.onResume()
        getCustInfo()
    }

    fun init()
    {
        binding.userName.setTypeface(UbboFreshApp.instance?.latobold)
        binding.mobileno.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.editText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.ordText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.switchStoreText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.logoutText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.contMerText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.returnPolicy.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.shippingPolicy.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.privacyPolicy.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.termsPolicy.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.version.setTypeface(UbboFreshApp.instance?.latoregular)
        try {
            val pInfo = activity?.packageName?.let { activity?.packageManager?.getPackageInfo(it, 0) }
            val version = pInfo?.versionName
            binding.version.text="Version: "+version
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
    fun getCustInfo()
    {
        val jsonobject= JSONObject()
        jsonobject.put("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonobject.put("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonobject.put("merchant_id", preference.getIntData(Constants.saveMerchantIdKey))
        Log.i("getMultipleStoreDetails",jsonobject.toString())

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val phnNum=preference.getStringData(Constants.saveMobileNumkey).toLong()
                custInfo=viewmodel.getCustInfo(phnNum,
                        preference.getStringData(Constants.saveaccesskey),
                        preference.getIntData(Constants.saveMerchantIdKey))
                binding.userName.setText(custInfo?.FirstName.toString()+" "+custInfo?.LastName.toString())
                binding.mobileno.text="+91"+custInfo?.PrimaryPhone
                binding.email.text=custInfo?.Email
            }catch (e: NoInternetExcetion)
            {
                MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }

        }
    }
    fun getTermsAndCndUrl()
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                tcmodel= viewmodel.merchantAppSettingDetails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        "TnCMessage",
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
            }catch (e: NoInternetExcetion)
            {
                MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    fun editProfile()
    {
        val map=HashMap<String,String>()
        map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
        map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
        Analytics.trackEvent("edit profile button clicked", map)

        val intent= Intent(activity,EditProfileActivity::class.java)
        intent.putExtra("custInfo",custInfo)
        startActivity(intent)
    }
    fun getOrder()
    {
        val intent= Intent(activity,MyOrdersActivity::class.java)
        startActivity(intent)
    }
    fun getContactMerchant()
    {
        val intent= Intent(activity,ContactMerchantActivity::class.java)
        startActivity(intent)
    }
    fun switchStore()
    {
        if(preference.getIntData(Constants.saveNoOfStores)>1) {
            activity?.okDialogWithNavigateToStore(requireActivity(),preference,"Are you sure","do you want to switch store?")

        }else
        {
            activity?.okDialogWithOneAct(Constants.appName,"This merchant dont have multiple stores")

        }
    }
    fun logout()
    {

        activity?.okLogOutDialog(requireActivity(),preference,"Are you sure","do you want to logout from app?")
    }
    fun loadTCUrl()
    {
        val map= java.util.HashMap<String, String>()
        map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
        map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
        Analytics.trackEvent("Terms and Conditions clicked", map)

        val model=tcmodel?.data?.merchantdata
        val intent=Intent(activity,WebviewActivity::class.java)
        intent.putExtra("url",model?.SettingMessage)
        startActivity(intent)
    }
    /*fun clearAllData()
    {
        preference.saveBoolData(Constants.saveMultistore,false)
        preference.saveBoolData(Constants.savelogin,false)
        val intent = Intent(activity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }*/
    override fun onClick(view: View?) {
        if(view==binding.editLayout)
        {
            editProfile()
        }else if(view==binding.orderLayout)
        {
            getOrder()
        }else if(view==binding.switchStoreLayout)
        {
           switchStore()
        }else if(view==binding.contactMerchant)
        {
            getContactMerchant()
        }else if(view==binding.logoutLayout)
        {
            logout()
        }
        else if(view==binding.termsPolicy)
        {
            loadTCUrl()
        }else if(view==binding.privacyPolicy)
        {
            loadTCUrl()
        }else if(view==binding.shippingPolicy)
        {
            loadTCUrl()
        }else if(view==binding.returnPolicy)
        {
            loadTCUrl()
        }
    }
}