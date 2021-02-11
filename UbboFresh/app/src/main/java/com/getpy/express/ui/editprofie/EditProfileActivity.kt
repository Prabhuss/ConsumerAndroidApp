package com.getpy.express.ui.editprofie

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.data.model.GetCustInfoResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityEditProfileBinding
import com.getpy.express.ui.account.AccountVewModel
import com.getpy.express.ui.account.AccountViewModelFactory
import com.getpy.express.ui.main.MainActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class EditProfileActivity : AppCompatActivity(),KodeinAware {
    override val kodein by kodein()
    lateinit var binding:ActivityEditProfileBinding
    lateinit var viewmodel:AccountVewModel
    private val factory: AccountViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_edit_profile)
        viewmodel=ViewModelProviders.of(this,factory).get(AccountVewModel::class.java)
        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )
        binding.backImag.setOnClickListener(View.OnClickListener {
            finish()
        })
        val custinfo=intent.getParcelableExtra<GetCustInfoResponse>("custInfo")
        binding.fname.setText(custinfo?.FirstName?:"")
        binding.lname.setText(custinfo?.LastName?:"")
        binding.email.setText(custinfo?.Email?:"")
        binding.gstNum.setText(custinfo?.GSTNumber?:"")

        binding.userName.setText(binding.fname.text.toString()+" "+binding.lname.text.toString())

        binding.applyChanges.setOnClickListener(View.OnClickListener {
            setCustInfo(binding.fname.text.toString(),
            binding.lname.text.toString(),
            binding.email.text.toString(),
            binding.gstNum.text.toString())
        })
        getGstDetails()
    }
    fun init()
    {
        binding.fname.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.lname.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.email.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.gstNum.setTypeface(UbboFreshApp.instance?.latoregular)
    }
    fun setCustInfo(fname:String?,lname:String?,email:String?,gstno:String?)
    {
        if(checkStringIsEmpty(fname.toString()))
        {
           binding.fname.setError("first name feild should not empty")
           return
        }
        if(checkStringIsEmpty(lname.toString()))
        {
            binding.lname.setError("first name feild should not empty")
            return
        }
        if(checkStringIsEmpty(email.toString()))
        {
            binding.email.setError("first name feild should not empty")
            return
        }
        binding.pbar.show()
        lifecycleScope.launch {
            try {
                val response=viewmodel.setCustInfo(
                        preference.getStringData(Constants.saveMobileNumkey),preference.getStringData(Constants.saveMobileNumkey),
                        preference.getIntData(Constants.saveMerchantIdKey),fname, lname,null,
                        null,null,email, null,null,null,null,null,
                        gstno,preference.getStringData(Constants.saveaccesskey)
                )
                binding.pbar.dismiss()
                response.message?.let { toast(it) }
            }catch (e: NoInternetExcetion)
            {
                //MainActivity.binding.coordinateLayout.snakBar("Please check network")
                networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.pbar.dismiss()
                okDialogWithOneAct("Error",e.message.toString())
            }
        }



    }

    fun getGstDetails()
    {
        lifecycleScope.launch {
            try {
                val response=viewmodel.getGSTSettingDetails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                val data=response.data
                if(data?.gST_Active.equals("Yes"))
                {
                  binding.gstNum.showView()
                }else
                {
                  binding.gstNum.hideView()
                }
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
                okDialogWithOneAct("Error",e.message.toString())
            }



        }
    }
}