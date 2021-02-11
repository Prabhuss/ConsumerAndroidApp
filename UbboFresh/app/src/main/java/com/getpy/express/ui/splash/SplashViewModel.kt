package com.getpy.fresh.views.splash

import android.content.Context
import android.graphics.Typeface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.getpy.express.UbboFreshApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel() : ViewModel() {
    val liveData:LiveData<SplashedState>
        get()=mutableLiveData
    private var mutableLiveData=MutableLiveData<SplashedState>()
    init {
        GlobalScope.launch {
          delay(1500)
          mutableLiveData.postValue(SplashedState.MainActivity())
        }
    }
    
    fun assainCustomFonts(context: Context)
    {
        try{
            UbboFreshApp.instance?.latoblack = Typeface.createFromAsset(context.assets, "fonts/Lato-Black.ttf")
            UbboFreshApp.instance?.latoblackitalic = Typeface.createFromAsset(context.assets, "fonts/Lato-BlackItalic.ttf")
            UbboFreshApp.instance?.latobold = Typeface.createFromAsset(context.assets, "fonts/Lato-Bold.ttf")
            UbboFreshApp.instance?.latobolditalic = Typeface.createFromAsset(context.assets, "fonts/Lato-BoldItalic.ttf")
            UbboFreshApp.instance?.latoheavy = Typeface.createFromAsset(context.assets, "fonts/Lato-Heavy.ttf")
            UbboFreshApp.instance?.latoheavyitalic = Typeface.createFromAsset(context.assets, "fonts/Lato-HeavyItalic.ttf")
            UbboFreshApp.instance?.latoitalic = Typeface.createFromAsset(context.assets, "fonts/Lato-Italic.ttf")
            UbboFreshApp.instance?.latolight = Typeface.createFromAsset(context.assets, "fonts/Lato-Light.ttf")
            UbboFreshApp.instance?.latolightitalic = Typeface.createFromAsset(context.assets, "fonts/Lato-LightItalic.ttf")
            UbboFreshApp.instance?.latomedium = Typeface.createFromAsset(context.assets, "fonts/Lato-Medium.ttf")
            UbboFreshApp.instance?.latoregular = Typeface.createFromAsset(context.assets, "fonts/Lato-Regular.ttf")
            UbboFreshApp.instance?.latomediumitalic = Typeface.createFromAsset(context.assets, "fonts/Lato-MediumItalic.ttf")
            UbboFreshApp.instance?.latosemibold = Typeface.createFromAsset(context.assets, "fonts/Lato-Semibold.ttf")
            UbboFreshApp.instance?.latosemibolditalic = Typeface.createFromAsset(context.assets, "fonts/Lato-SemiboldItalic.ttf")
            UbboFreshApp.instance?.latothin = Typeface.createFromAsset(context.assets, "fonts/latothin.ttf")
            UbboFreshApp.instance?.latothinitalic = Typeface.createFromAsset(context.assets, "fonts/Lato-ThinItalic.ttf")

        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }
}
sealed class SplashedState
{
    class MainActivity:SplashedState()
}