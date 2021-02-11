package com.getpy.express.ui.auth

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityLoginBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import kotlin.collections.HashMap


class LoginActivity : AppCompatActivity(),KodeinAware
{
    private var binding:ActivityLoginBinding?=null
    private var viewmodel:AuthViewModel?=null
    override val kodein by kodein()
    var token:String=""
    private val preference:PreferenceProvider by instance()
    private val factory: AuthViewModelFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewmodel= ViewModelProviders.of(this,factory).get(AuthViewModel::class.java)

        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )

        initFont()
        binding?.sendOtpBut?.setOnClickListener(View.OnClickListener {
            binding?.sendOtpBut?.setBackgroundResource(R.drawable.rounded_corner_selected)
            binding?.sendOtpBut?.setTextColor(Color.WHITE)

                 generateOtpCall()
        })
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result

        })
    }
    fun initFont()
    {
        binding?.loginTxt?.setTypeface(UbboFreshApp.instance?.latobold)
        binding?.entMonTxt?.setTypeface(UbboFreshApp.instance?.latoregular)
        binding?.mobileNumEdit?.setTypeface(UbboFreshApp.instance?.latoregular)
        binding?.sendOtpBut?.setTypeface(UbboFreshApp.instance?.latoregular)
    }
    fun generateOtpCall()
    {
        val mobleNum=binding?.mobileNumEdit?.text.toString()
        if(TextUtils.isEmpty(mobleNum))
        {
            okDialogWithOneAct(Constants.appName,"please enter mobiler number",binding!!.sendOtpBut)
            return
        }
        val isPhnNum=binding?.mobileNumEdit?.text.toString().isPhoneNumber()
        if(!isPhnNum) {
            binding?.mobileNumEdit?.setError("Please check mobile number")
            return
        }
        binding?.progressBar?.show()
        lifecycleScope.launch {
            try {
                val authres=viewmodel?.generateOtp(mobleNum,Constants.merchantid,Constants.hashkey,token)
                binding?.progressBar?.dismiss()
                if(authres?.status?.toLowerCase().equals(Constants.status)) {
                    authres?.accessKey?.let { preference.saveStringData(Constants.saveaccesskey, it) }
                    preference.saveStringData(Constants.saveMobileNumkey, mobleNum)
                    Intent(this@LoginActivity, OTPVerificationActivity::class.java).also {
                        startActivity(it)
                    }
                }else
                {
                    okDialogWithOneAct("Error",authres?.message.toString())
                }
            }catch (e:NoInternetExcetion)
            {
                binding?.progressBar?.dismiss()
                networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding?.progressBar?.dismiss()
                okDialogWithOneAct("Error",e.message.toString())
            }
        }
        val map=HashMap<String,String>()
        map.put("mobileNum",mobleNum)
        map.put("merchantid", Constants.merchantid.toString())
        Analytics.trackEvent("send OTP button clicked", map)
    }
}