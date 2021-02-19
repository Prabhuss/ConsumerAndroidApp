package com.getpy.express.ui.cart.cartactivities

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.CustomerAddressAdapter
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.db.entities.CustomerAddressData
import com.getpy.express.data.model.GetDistanceData
import com.getpy.express.data.model.Merchantdata
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityCheckOutBinding
import com.getpy.express.databinding.OkCustomDialogBinding
import com.getpy.express.ui.cart.CartViewModel
import com.getpy.express.ui.cart.CartViewModelFactory
import com.getpy.express.ui.orderstatus.OrderStatusActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*

class CheckOutActivity : AppCompatActivity(), KodeinAware {
    override val kodein by kodein()
    private val database: AppDataBase by instance()
    private val factory: CartViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var binding: ActivityCheckOutBinding
    lateinit var viewModel: CartViewModel
    var adapter:CustomerAddressAdapter?=null
    var customerAddressData:CustomerAddressData?=null
    var merchantAppSetData:Merchantdata?=null
    var getDisanceData:GetDistanceData?=null
    var latitude: String? = null
    var longitude: String? = null
    var city: String = ""
    var state: String = ""
    var country: String = ""
    var postalCode: String = ""
    var address: String = ""
    var knownName: String = ""
    var totalPrice: String = ""
    var discount: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_check_out)
        viewModel = ViewModelProviders.of(this, factory).get(CartViewModel::class.java)
        binding.recyclerview.setHasFixedSize(true)

        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )

        binding.addNewAddr.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            intent.putExtra("lat", latitude)
            intent.putExtra("lng", longitude)
            intent.putExtra("city", city)
            intent.putExtra("state", state)
            intent.putExtra("country", country)
            intent.putExtra("postalCode", postalCode)
            intent.putExtra("address", address)
            intent.putExtra("discount",discount)
            startActivity(intent)
        })
        binding.icBack.setOnClickListener(View.OnClickListener {
            finish()
        })
        binding.placeOrder.setOnClickListener(View.OnClickListener {
            binding.placeOrder.isEnabled=false
            Handler().postDelayed({ binding.placeOrder.setEnabled(true) }, 5000)
            if(adapter?.getModel()!=null) {
                customerAddressData=adapter?.getModel()
                if (!adapter?.getModel()!!.ischecked) {
                    okDialogWithOneAct(Constants.AlertBoxHeader, "No delivery address selected")
                    return@OnClickListener
                }
            }else
            {
                okDialogWithOneAct(Constants.AlertBoxHeader, "No delivery address selected")
                return@OnClickListener
            }
            if(!binding.checkbox.isChecked)
            {
                okDialogWithOneAct(Constants.AlertBoxHeader,"Please select payment option")
                return@OnClickListener
            }

            if(merchantAppSetData?.SettingValue?.toDouble()!! >totalPrice.toDouble())
            {
                okDialogWithOneAct(Constants.AlertBoxHeader,merchantAppSetData?.SettingMessage.toString())
                return@OnClickListener
            }
            getDistance(customerAddressData?.Latitude, customerAddressData?.Longitude);
        })

        init()
    }

    fun init() {
        binding.checkout.setTypeface(UbboFreshApp.instance?.latobold)
        binding.deliverto.setTypeface(UbboFreshApp.instance?.latobold)
        binding.paymentOption.setTypeface(UbboFreshApp.instance?.latobold)
        binding.payOnDelivery.setTypeface(UbboFreshApp.instance?.latobold)
        binding.payondeltext.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.priceDetails.setTypeface(UbboFreshApp.instance?.latobold)
        binding.priceDetailsValue.setTypeface(UbboFreshApp.instance?.latobold)
        binding.total.setTypeface(UbboFreshApp.instance?.latobold)
        binding.totalPrice.setTypeface(UbboFreshApp.instance?.latobold)
        binding.placeOrder.setTypeface(UbboFreshApp.instance?.latoregular)

        binding.totMrp.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.totMrpValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.discount.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.discountValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.couponDiscount.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.couponDiscountValue.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.delCharges.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.delChargesValue.setTypeface(UbboFreshApp.instance?.latoregular)

        totalPrice = intent.getStringExtra("total") ?: "0.0"
        discount = intent.getStringExtra("discount") ?: "0.0"
        latitude = intent.getStringExtra("lat") ?: null
        longitude = intent.getStringExtra("lng") ?: null
        city = intent.getStringExtra("city") ?: ""
        state = intent.getStringExtra("state") ?: ""
        country = intent.getStringExtra("country") ?: ""
        postalCode = intent.getStringExtra("postalCode") ?: ""
        address = intent.getStringExtra("address") ?: ""

        binding.totMrpValue.setText(totalPrice)
        binding.totalPrice.setText(totalPrice)
        binding.discountValue.setText(discount)
        // getDistance()
        //getMerchantAppSettingDetails()

        getMerchantAppSettingDetails()



    }

    override fun onResume() {
        super.onResume()
        getCustomerAddress()
    }

    fun placeOrder() {
        val map=HashMap<String,String>()
        map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
        map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
        map.put("InvoiceType ","GetPY")
        map.put("PaymentMode", "COD")
        map.put("TotalAmount", totalPrice)
        Analytics.trackEvent("New Order clicked", map)
        val jsonObject=JsonObject()
        val order_details=JsonObject()
        val Invoice=JsonObject()
        val InvoiceItem=JsonArray()
        jsonObject.addProperty("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonObject.addProperty("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonObject.addProperty("merchant_id",preference.getIntData(Constants.saveMerchantIdKey))
        jsonObject.addProperty("orderpayment_id","")
        Invoice.addProperty("DiscountAmount",discount)
        Invoice.addProperty("TaxAmount","0.0")
        Invoice.addProperty("TotalInvoiceAmount",totalPrice)
        Invoice.addProperty("CouponCode","")
        Invoice.addProperty("PayableAmount",totalPrice)
        Invoice.addProperty("InvoiceType","GetPYApp")
        Invoice.addProperty("OrderStatus","New")
        Invoice.addProperty("PaymentMode","COD")
        Invoice.addProperty("DeliverAddressId",customerAddressData?.ID)
        Invoice.addProperty("PaymentOrderId","NULL")
        Invoice.addProperty("DeliveryInstruction",UbboFreshApp.instance?.instructionString)
        order_details.add("Invoice",Invoice)
        for(i in 0 until UbboFreshApp.instance?.carItemsList!!.size)
        {
            val InvoiceItemObj=JsonObject()
            val model=UbboFreshApp.instance?.carItemsList?.get(i)
            InvoiceItemObj.addProperty("ProductId",model?.citrineProdId)
            InvoiceItemObj.addProperty("quantity",model?.itemCount)
            InvoiceItemObj.addProperty("ProductName",model?.productName)
            if(model?.mrp!=null && model.sellingPrice!=null)
            {
                InvoiceItemObj.addProperty("UnitPrice",model.mrp)
                InvoiceItemObj.addProperty("Discount",(model.mrp?.toDouble()?.minus(model.sellingPrice.toDouble()))?.div(model?.mrp?.toDouble()!!))
                InvoiceItemObj.addProperty("UnitPriceAfterDiscount",model.sellingPrice)
                InvoiceItemObj.addProperty("TotalPrice",(model.itemCount.times(model.sellingPrice.toDouble())))
            }
            var url=""
            if(model?.imageLinkFlag!=null) {
                if (model.imageLinkFlag.equals("R")) {
                  url=UbboFreshApp.instance?.imageLoadUrl+model.productPicUrl
                } else {
                    url=model.productPicUrl
                }
            }else
            {
                if(model?.productPicUrl!=null)
                {
                    url=UbboFreshApp.instance?.imageLoadUrl+model.productPicUrl
                }else
                {
                    url= UbboFreshApp.instance?.imageLoadUrl.toString()
                }

            }
            InvoiceItemObj.addProperty("ProductImage",url)
            InvoiceItemObj.addProperty("Category",model?.category)
            InvoiceItem.add(InvoiceItemObj)
        }
        order_details.add("InvoiceItem",InvoiceItem)
        jsonObject.add("order_details",order_details)

        binding.pbar.show()

        lifecycleScope.launch {
          try {
                 val response=viewModel.createOrder(jsonObject)
                 binding.pbar.dismiss()
                 UbboFreshApp.instance?.instructionString=""
                 if(response.status.equals("Failure"))
                 {
                     val intent=Intent(this@CheckOutActivity,OrderStatusActivity::class.java)
                     intent.putExtra("message",response.data?.message)
                     intent.putExtra("status",response.status)
                     startActivity(intent)
                 }else
                 {
                    val intent=Intent(this@CheckOutActivity,OrderStatusActivity::class.java)
                    intent.putExtra("message",response.data?.message)
                    intent.putExtra("status",response.status)
                    startActivity(intent)
                 }
             }catch (e: NoInternetExcetion)
             {
                 binding.pbar.dismiss()
                 networkDialog()
             }catch (e:CancellationException)
             {
                 binding.pbar.dismiss()
                 Log.i("scope","job is canceled")
             }
             catch (e:Exception)
             {
                 binding.pbar.dismiss()
                 okDialogWithOneAct("Error",e.message.toString())
             }
         }
    }
    fun getCustomerAddress() {

        lifecycleScope.launch {
            try {
                val response = viewModel.getCustomerAddress(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                launch {
                    try {
                        response.data?.let { database.CustomerAddressDao().insertcustomerAddrData(it) }
                        if (response.data?.size == 0) {
                            binding.line.visibility = View.GONE
                        } else {
                            binding.line.visibility = View.VISIBLE
                        }
                        if (response.data?.size == 3) {
                            binding.addNewAddr.visibility = View.GONE
                        }
                        val list = database.CustomerAddressDao().getCustAddrData(
                                preference.getStringData(Constants.saveMobileNumkey),
                                preference.getIntData(Constants.saveMerchantIdKey))
                        //Baypass local database
                        adapter = list.let { CustomerAddressAdapter(this@CheckOutActivity, it) }
                        //comment above line and uncomment below line to use local database for customer address
                        //adapter = CustomerAddressAdapter(this@CheckOutActivity, list)
                        binding.recyclerview.adapter = adapter
                        adapter?.notifyDataSetChanged()
                    } catch (E: Exception) {
                        E.printStackTrace()
                    }
                }
            }catch (e: NoInternetExcetion) {
                networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e: Exception) {
                okDialogWithOneAct("Error", e.message.toString())
            }
        }
    }

    fun getDistance(lat: String?, long: String?) {
        lifecycleScope.launch {
            try {
               val getDisanceResponse = viewModel.getDistance(
               preference.getIntData(Constants.saveMerchantIdKey),
               latitude,
               longitude,
               preference.getStringData(Constants.saveaccesskey),
               preference.getStringData(Constants.saveMobileNumkey))
               getDisanceData=getDisanceResponse.data
                //Code for "Distance check" pop up
                if(getDisanceData?.active?.toLowerCase().equals("yes"))
                {
                    if(getDisanceData?.deliverable?.toLowerCase().equals("yes"))
                    {
                        getDisanceData?.message?.let { it1 -> okDialog(Constants.AlertBoxHeader, it1)}
                        return@launch
                    }
                }
                if(getDisanceData?.active?.toLowerCase().equals("yes"))
                {
                    if(getDisanceData?.deliverable?.toLowerCase().equals("no"))
                    {
                        getDisanceData?.message?.let { it1 -> okDialogWithOneAct(Constants.AlertBoxHeader, it1)}
                        return@launch
                    }
                }
                placeOrder()
            } catch (e: NoInternetExcetion) {
                networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e: Exception) {
                okDialogWithOneAct("Error", e.message.toString())
            }
        }
    }

    fun getMerchantAppSettingDetails() {

        val jsonobject=JSONObject()
        jsonobject.put("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonobject.put("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonobject.put("merchant_id", preference.getIntData(Constants.saveMerchantIdKey))
        Log.i("getMerAppSetDetails",jsonobject.toString())


            lifecycleScope.launch {
                try {
                   val mdata = viewModel.merchantAppSettingDetails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        "Amount",
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                   merchantAppSetData=mdata.data?.merchantdata
                   binding.priceDetailsValue.setText(merchantAppSetData?.SettingMessage)
                } catch (e: NoInternetExcetion) {
                    networkDialog()
                }catch (e:CancellationException)
                {
                    Log.i("scope","job is canceled")
                }
                catch (e: Exception) {
                    okDialogWithOneAct("Error", e.message.toString())
                }
            }


    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                        this,
                        LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
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
        fusedLocationProviderClient.requestLocationUpdates(
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
                                geocoder = Geocoder(this@CheckOutActivity, Locale.getDefault())
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
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                            this,
                            getString(R.string.location_permission_not_granted),
                            Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun okDialog(title:String, message:String)
    {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        val binding : OkCustomDialogBinding = DataBindingUtil.inflate(
            LayoutInflater.from(this), R.layout.ok_custom_dialog, null, false);
        dialog.setContentView(binding.root)
        binding.cancelText.hideView()
        binding.header.text = title
        binding.message.text=message
        binding.okText.text="Ok"
        binding.okText.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.header.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.message.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.okText.setOnClickListener {
            placeOrder()
            dialog.dismiss()
        }
        if(this!=null)
            dialog.show()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }
}