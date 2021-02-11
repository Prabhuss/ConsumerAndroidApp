package com.getpy.express.ui.cart

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.CartItmesAdapter
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentCartBinding
import com.getpy.express.ui.cart.cartactivities.CheckOutActivity
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.main.MainActivity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance
import java.util.*
import kotlin.math.roundToInt

class CartFragment : InjectionFragment() {
    private val appDataBase: AppDataBase by instance()
    private val preference:PreferenceProvider by instance()
    var latitude:String?=null
    var longitude:String?=null
    var city:String=""
    var state: String=""
    var country: String=""
    var postalCode: String=""
    var address:String=""
    var knownName: String=""
    var totalPrice:String=""
    var discount:String=""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_cart, container, false)
        binding.recyclerview.setHasFixedSize(true)
        MainActivity.binding.activityMainAppbarlayout.showView()
        MainActivity.binding.selectStore.hideView()
        MainActivity.binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
        MainActivity.binding.layout.gravity=Gravity.CENTER
        MainActivity.binding.activityMainToolbarTitle.setText("My Cart")
        binding.procedBut.setOnClickListener(View.OnClickListener {
            if(UbboFreshApp.instance?.carItemsList!!.size>0) {
                val intent = Intent(activity, CheckOutActivity::class.java)
                intent.putExtra("total", binding.totalPrice.text.toString())
                intent.putExtra("discount", binding.discountValue.text.toString())
                intent.putExtra("lat",latitude)
                intent.putExtra("lng",longitude)
                intent.putExtra("city",city)
                intent.putExtra("state",state)
                intent.putExtra("country",country)
                intent.putExtra("postalCode",postalCode)
                intent.putExtra("address",address)
                startActivity(intent)
            }else
            {
                activity?.okDialogWithOneAct(Constants.appName,"Hello sir, your cart is empty")
            }
        })
        runnable= Runnable {
            caleculateTotal()
        }
        addRunnable = Runnable {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    UbboFreshApp.instance?.carItemsList?.let { appDataBase.CustomerAddressDao().insertProductsData(it) }
                }catch (e:CancellationException)
                {
                    Log.i("scope","job is canceled")
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        }
        removeRunnable = Runnable {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    UbboFreshApp.instance?.productsDataModel?.let { appDataBase.CustomerAddressDao().deleteProductsData(it) }
                }catch (e:CancellationException)
                {
                    Log.i("scope","job is canceled")
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        }

        naviagateRunnable = Runnable {
            if(MainActivity.navcontroller?.currentDestination?.id==R.id.cartFragment) {
                MainActivity.navcontroller?.navigate(R.id.action_cartFragment_to_productsFragment)
            }
        }

        init()
        cartItems()
        caleculateTotal()
        return binding.root
    }

    fun cartItems()
    {
        if(UbboFreshApp.instance?.carItemsList!=null) {
            if (UbboFreshApp.instance?.carItemsList!!.size > 0) {
                binding.cartEmptyLayout.visibility = View.GONE
                binding.carItemsLayout.visibility = View.VISIBLE
                adapter = UbboFreshApp.instance?.carItemsList?.let {
                    activity?.let { it1 -> CartItmesAdapter(preference,it1.supportFragmentManager,it1, it) }
                }
                binding.recyclerview.adapter = adapter
            } else {
                binding.cartEmptyLayout.visibility = View.VISIBLE
                binding.carItemsLayout.visibility = View.GONE
            }
        }else
        {
            binding.cartEmptyLayout.visibility = View.VISIBLE
            binding.carItemsLayout.visibility = View.GONE
        }
    }
    fun caleculateTotal()
    {
        var totalPrice=0.0
        var discount=0.0
        for(i in 0 until UbboFreshApp.instance?.carItemsList!!.size)
        {
            val model=UbboFreshApp.instance?.carItemsList?.get(i)
            totalPrice=totalPrice+(model?.itemCount!!.times(model.sellingPrice.toDouble()))
            if(model.discount!=null) {
                discount = discount + model.discount.toDouble()
            }
        }
        var roundedTotalPrice = String.format("%.2f", totalPrice)
        binding.totMrpValue.setText(roundedTotalPrice)
        binding.totalPrice.setText(roundedTotalPrice)
        binding.discountValue.setText(discount.toString())
    }
    fun init()
    {
        binding.totMrp.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.totMrpValue.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.discount.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.discountValue.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.couponDiscount.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.couponDiscountValue.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.delCharges.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.delChargesValue.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.total.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.totalPrice.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.procedBut.setTypeface(UbboFreshApp.instance?.latoheavy)
    }
    override fun onStart() {
        super.onStart()
        when {
            activity?.let { PermissionUtils.isAccessFineLocationGranted(it) }!! -> {
                when {
                    PermissionUtils.isLocationEnabled(requireActivity()) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(requireActivity())
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                        activity as AppCompatActivity,
                        LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }
    private fun setUpLocationListener() {
        val fusedLocationProviderClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (activity?.let {
                    ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED && activity?.let {
                    ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient?.requestLocationUpdates(
                locationRequest,
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        try {
                            for (location in locationResult.locations) {
                                latitude = location.latitude.toString()
                                longitude = location.longitude.toString()
                                val geocoder: Geocoder
                                val addresses: List<Address>
                                if(activity!=null) {
                                    geocoder = Geocoder(activity, Locale.getDefault())
                                    addresses = geocoder.getFromLocation(latitude!!.toDouble(), longitude!!.toDouble(), 1)
                                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                    if(addresses!=null) {
                                        address = addresses[0].getAddressLine(0)
                                        // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        if(addresses[0].getLocality()!=null)
                                        {
                                            city =addresses[0].getLocality()
                                        }else
                                        {
                                            city =""
                                        }
                                        if(addresses[0].getAdminArea()!=null)
                                        {
                                            state =addresses[0].getAdminArea()
                                        }else
                                        {
                                            state =""
                                        }
                                        if(addresses[0].getCountryName()!=null)
                                        {
                                            country =addresses[0].getCountryName()
                                        }else
                                        {
                                            country =""
                                        }
                                        if(addresses[0].getPostalCode()!=null)
                                        {
                                            postalCode = addresses[0].getPostalCode()
                                        }else
                                        {
                                            postalCode = ""
                                        }

                                        if(addresses[0].getFeatureName()!=null)
                                        {
                                            knownName = addresses[0].getFeatureName()
                                        }else
                                        {
                                            knownName =""
                                        }


                                    }
                                }

                            }
                        }catch (e:Exception)
                        {
                            e.printStackTrace()
                        }

                        // Few more things we can do here:
                        // For example: Update the location of user on server
                    }
                },
                Looper.myLooper()
        )
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        activity?.let { PermissionUtils.isLocationEnabled(it) }!! -> {
                            setUpLocationListener()
                        }
                        else -> {
                            activity?.let { PermissionUtils.showGPSNotEnabledDialog(it) }
                        }
                    }
                } else {
                    Toast.makeText(
                            activity,
                            getString(R.string.location_permission_not_granted),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
        lateinit var binding:FragmentCartBinding
        var runnable:Runnable?=null
        var addRunnable:Runnable?=null
        var removeRunnable:Runnable?=null
        var naviagateRunnable:Runnable?=null
        var adapter:CartItmesAdapter?=null
    }

}