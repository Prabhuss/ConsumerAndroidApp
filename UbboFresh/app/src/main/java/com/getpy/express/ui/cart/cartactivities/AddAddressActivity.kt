package com.getpy.express.ui.cart.cartactivities


import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.room.PrimaryKey
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.AutoCompleteAdapter
import com.getpy.express.data.db.entities.CustomerAddressData
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityAddAddressBinding
import com.getpy.express.ui.cart.CartViewModel
import com.getpy.express.ui.cart.CartViewModelFactory
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.util.*


class AddAddressActivity : AppCompatActivity(),KodeinAware,OnMapReadyCallback {
    override val kodein by kodein()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var mapFragment: SupportMapFragment? = null
    private val factory: CartViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var binding:ActivityAddAddressBinding
    lateinit var viewModel: CartViewModel
    var adapter: AutoCompleteAdapter? = null
    var placesClient: PlacesClient? = null
    var latitude:String?=null
    var longitude:String?=null
    var city:String=""
    var state: String=""
    var country: String=""
    var postalCode: String=""
    var address:String=""
    var saveAs:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_add_address)
        viewModel=ViewModelProviders.of(this,factory).get(CartViewModel::class.java)

        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )

        binding.applyChanges.setOnClickListener(View.OnClickListener {
            setCustomerAddress()
        })
        binding.icBack.setOnClickListener(View.OnClickListener {
            finish()
        })
//        val apiKey = getString(R.string.google_maps_key)
//        if (!Places.isInitialized()) {
//            Places.initialize(getApplicationContext(), apiKey);
//        }

        //placesClient = Places.createClient(this);
        //initAutoCompleteTextView();
        init()
        if(intent.extras?.getSerializable("model")!=null) {
            val model = intent.extras?.getSerializable("model") as CustomerAddressData
            model.let { updateData(it) }
        }
        createMap()
    }
    fun init()
    {
        binding.editAddress.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.addrDetails.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.name.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.nameLayout.markRequiredInRed()
        binding.alternateNumber.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.landmark.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.landmarkLayout.markRequiredInRed()
        binding.flatno.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.address.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.addressLayout.markRequiredInRed()
        binding.city.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.cityLayout.markRequiredInRed()
        binding.state.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.stateLayout.markRequiredInRed()
        binding.country.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.countryLayout.markRequiredInRed()
        binding.postZipcode.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.postZipcodeLayout.markRequiredInRed()
        binding.useCurrLocation.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.locText.setTypeface(UbboFreshApp.instance?.latoheavy)
        binding.applyChanges.setTypeface(UbboFreshApp.instance?.latoheavy)

        val intent=intent
        latitude=intent.getStringExtra("lat")?:"0.0"
        longitude=intent.getStringExtra("lng")?:"0.0"
        city=intent.getStringExtra("city")?:""
        state=intent.getStringExtra("state")?:""
        country=intent.getStringExtra("country")?:""
        postalCode=intent.getStringExtra("postalCode")?:""
        address=intent.getStringExtra("address")?:""

        binding.address.setText(address)
        if(checkStringIsEmpty(city))
        {
            binding.city.setHint("")
        }else
        {
            binding.city.setText(city)
        }
        if(checkStringIsEmpty(state))
        {
            binding.state.setHint("")
        }else
        {
            binding.state.setText(state)
        }
        if(checkStringIsEmpty(country))
        {
            binding.country.setHint("")
        }else
        {
            binding.country.setText(country)
        }
        if(checkStringIsEmpty(postalCode))
        {
            binding.postZipcode.setHint("")
        }else
        {
            binding.postZipcode.setText(postalCode)
        }
    }
    fun updateData(model:CustomerAddressData)
    {
        binding.name.setText(model.FirstName)
        binding.alternateNumber.setText(model.SecondaryPhone)
        binding.address.setText(model.Address1)
        binding.landmark.setText(model.Address2)
        binding.flatno.setText(model.FlatNo_DoorNo)
        binding.city.setText(model.City)
        binding.state.setText(model.State)
        binding.country.setText(model.Country)
        if(model.PostalCodeZipCode!=null) {
            if (checkStringIsEmpty(model.PostalCodeZipCode!!)) {
                binding.postZipcode.setHint(postalCode)
            } else {
                binding.postZipcode.setText(model.PostalCodeZipCode)
            }
        }else
        {
            if(checkStringIsEmpty(postalCode))
            {
                binding.postZipcode.setHint("")
            }else
            {
                binding.postZipcode.setText(postalCode)

            }
        }

        if(model.TagName.equals(Constants.address1))
        {
            binding.otherButton.isEnabled=false
            binding.workButton.isEnabled=false
            binding.workButton.isClickable=false
            binding.otherButton.isClickable=false
            binding.homeButton.isChecked=true
        }
        if(model.TagName.equals(Constants.address2))
        {
            binding.otherButton.isEnabled=false
            binding.homeButton.isEnabled=false
            binding.homeButton.isClickable=false
            binding.otherButton.isClickable=false
            binding.workButton.isChecked=true
        }
        if(model.TagName.equals(Constants.address3))
        {
            binding.homeButton.isEnabled=false
            binding.workButton.isEnabled=false
            binding.homeButton.isClickable=false
            binding.workButton.isClickable=false
            binding.otherButton.isChecked=true
        }
    }
    fun setCustomerAddress()
    {
        if(checkStringIsEmpty(binding.name.text.toString()))
        {
            binding.nameLayout.error="name field shouldnot empty"
            return
        }
        if(checkStringIsEmpty(binding.landmark.text.toString()))
        {
            binding.landmarkLayout.error="landmark field shouldnot empty"
            return
        }
        if(checkStringIsEmpty(binding.address.text.toString()))
        {
            binding.addressLayout.error="address field shouldnot empty"
            return
        }
        if(checkStringIsEmpty(binding.city.text.toString()))
        {
            binding.addressLayout.error="city field shouldnot empty"
            return
        }
        if(checkStringIsEmpty(binding.state.text.toString()))
        {
            binding.addressLayout.error="state field shouldnot empty"
            return
        }
        if(checkStringIsEmpty(binding.country.text.toString()))
        {
            binding.addressLayout.error="country field shouldnot empty"
            return
        }
        if(checkStringIsEmpty(binding.postZipcode.text.toString()))
        {
            binding.postZipcodeLayout.error="postcode field shouldnot empty"
            return
        }
        val selectedId = binding.radioGroup.checkedRadioButtonId
        val button = findViewById(selectedId) as RadioButton
        if(button.text.equals("Home"))
        {
            saveAs=Constants.address1
        }
        if(button.text.equals("Work"))
        {
            saveAs=Constants.address2
        }
        if(button.text.equals("Other"))
        {
            saveAs=Constants.address3
        }
        if(checkStringIsEmpty(button.text.toString()))
        {
            okDialogWithOneAct(Constants.appName,"please choose any address type")
            return
        }
        lifecycleScope.launch {
            try {
                val response=viewModel.setCustomerAddress(
                        preference.getStringData(Constants.saveaccesskey),
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        binding.alternateNumber.text.toString(),
                        binding.address.text.toString(),
                        binding.landmark.text.toString(),
                        longitude,
                        latitude,
                        saveAs,
                        binding.name.text.toString(),
                        "",
                        binding.flatno.text.toString(),
                        binding.city.text.toString(),
                        binding.state.text.toString(),
                        binding.country.text.toString(),
                        "",
                        binding.postZipcode.text.toString()
                )
                val data=response.string()
                val obj=JSONObject(data)
                finish()
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
    private fun initAutoCompleteTextView() {
        binding.auto.setThreshold(1)
        binding.auto.setOnItemClickListener(autocompleteClickListener)
        adapter = placesClient?.let { AutoCompleteAdapter(this, it) }
        binding.auto.setAdapter(adapter)
    }
    fun createMap()
    {
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        if(latitude!=null && longitude!=null) {
            googleMap?.addMarker(
                MarkerOptions()
                    .position(LatLng(latitude!!.toDouble(), longitude!!.toDouble()))
                    .title(city)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude!!.toDouble(),longitude!!.toDouble()),
                    10f
                )
            )
        }

       /* googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(37.4629101, -122.2449094))
                .title("Facebook")
                .snippet("Facebook HQ: Menlo Park")
        )

        googleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(37.3092293, -122.1136845))
                .title("Apple")
        )*/


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
                    for (location in locationResult.locations) {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        val geocoder: Geocoder
                        val addresses: List<Address>
                        geocoder = Geocoder(this@AddAddressActivity, Locale.getDefault())
                        addresses = geocoder.getFromLocation(latitude!!.toDouble(), longitude!!.toDouble(), 1)
                        // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        if(addresses!=null) {
                            address = addresses[0].getAddressLine(0)
                            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            city = addresses[0].getLocality()
                            state = addresses[0].getAdminArea()
                            country = addresses[0].getCountryName()
                            postalCode = addresses[0].getPostalCode()
                            val knownName = addresses[0].getFeatureName()
                        }
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

    private val autocompleteClickListener: AdapterView.OnItemClickListener = object :
        AdapterView.OnItemClickListener {
            override fun onItemClick(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                try {
                    val item: AutocompletePrediction = adapter!!.getItem(i)
                    var placeID: String? = null
                    if (item != null) {
                        placeID = item.getPlaceId()
                    }

//                To specify which data types to return, pass an array of Place.Fields in your FetchPlaceRequest
//                Use only those fields which are required.
                    val placeFields: kotlin.collections.List<Place.Field> = Arrays.asList(
                        Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS
                        , Place.Field.LAT_LNG
                    )
                    var request: FetchPlaceRequest? = null
                    if (placeID != null) {
                        request = FetchPlaceRequest.builder(placeID, placeFields)
                            .build()
                    }
                    if (request != null) {
                        placesClient!!.fetchPlace(request).addOnSuccessListener(object :
                            OnSuccessListener<FetchPlaceResponse?> {


                            override fun onSuccess(task: FetchPlaceResponse?) {
                               toast(task?.getPlace()?.getName().toString() + "\n" + task?.getPlace()
                                   ?.getAddress());
                                /*responseView?.setText(
                                    task.getPlace().getName().toString() + "\n" + task.getPlace()
                                        .getAddress()
                                )*/
                            }
                        }).addOnFailureListener(object : OnFailureListener {
                            override fun onFailure(@NonNull e: Exception) {
                                e.printStackTrace()
                                toast(e.message.toString());
                                //responseView!!.text = e.message
                            }
                        })
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }


}