package com.getpy.express.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.getpy.express.Utils.NoInternetExcetion
import okhttp3.Interceptor
import okhttp3.Response


class NetworkConnectionInterceptor(val context: Context):Interceptor {
    private val applicationContext=context.applicationContext
    override fun intercept(chain: Interceptor.Chain): Response {
        if(!isInternetAvailable())
            throw NoInternetExcetion("maky sure you have an active data connection")
        return chain.proceed(chain.request())
    }
    private fun isInternetAvailable():Boolean
    {
        var result =false
        val connectivityManager= applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager.let {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        it?.getNetworkCapabilities(connectivityManager?.activeNetwork)?.apply {
                            result=when{
                                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
                                else ->false
                            }
                        }
                    } else {
                        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val activeNetwork = cm.activeNetworkInfo
                        if (activeNetwork != null) {
                            // connected to the internet
                            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                                return true
                                // connected to wifi
                            } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                                // connected to mobile data
                                return true
                            }else
                            {
                                return false
                            }
                        } else {
                            // not connected to the internet
                            return false
                        }
                    }
        }
       return result
    }
}