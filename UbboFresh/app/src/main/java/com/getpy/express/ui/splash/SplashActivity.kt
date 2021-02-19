package com.getpy.fresh.views.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.Constants
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.ui.auth.LoginActivity
import com.getpy.express.ui.main.MainActivity
import com.getpy.express.ui.multistore.MultiStoreActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SplashActivity : AppCompatActivity(),KodeinAware {
    override val kodein by kodein()
    private lateinit var splashViewModel:SplashViewModel
    private val databse:AppDataBase by instance()
    private val preference: PreferenceProvider by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val stb = AnimationUtils.loadAnimation(this, R.anim.stb)
        val btt = AnimationUtils.loadAnimation(this, R.anim.btt)
        var iconImage = findViewById(R.id.imageView) as ImageView
        var getPYImage = findViewById(R.id.biz_text) as ImageView
        iconImage.startAnimation(stb)
        getPYImage.startAnimation(btt)

        splashViewModel=ViewModelProviders.of(this).get(SplashViewModel::class.java)
        splashViewModel.assainCustomFonts(this)
        lifecycleScope.launch {
            try {
                UbboFreshApp.instance?.carItemsList=databse.CustomerAddressDao().getCartData(
                    preference.getIntData(Constants.saveMerchantIdKey).toString(),preference.getStringData(Constants.saveMobileNumkey))
                if(UbboFreshApp.instance?.carItemsList!!.size>0)
                {
                    UbboFreshApp.instance?.hashMap?.clear()
                    for(i in 0 until UbboFreshApp.instance?.carItemsList!!.size) {
                        val model = UbboFreshApp.instance?.carItemsList?.get(i)
                        model?.citrineProdId?.let { UbboFreshApp.instance?.hashMap?.put(it,model) }
                    }
                }
                callAfterLaunch()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                callAfterLaunch()
                e.printStackTrace()
            }

        }

    }
    fun callAfterLaunch()
    {
        splashViewModel.liveData.observe(this, Observer {
            when(it)
            {
                is SplashedState.MainActivity->{
                    goToMainActivity()
                }
            }
        })
    }
    private fun goToMainActivity()
    {
        UbboFreshApp.instance?.imageLoadUrl=preference.getStringData(Constants.saveBaseUrl)
        if(preference.getBoolData(Constants.savelogin))
        {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else if(preference.getBoolData(Constants.saveMultistore))
        {
            startActivity(Intent(this,MultiStoreActivity::class.java))
            finish()
        }else
        {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }


}