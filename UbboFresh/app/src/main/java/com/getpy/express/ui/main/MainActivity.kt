package com.getpy.express.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.MainCategoryExpAdapter
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.model.CategoriesExpModel
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.model.MerAppSettingsDetailsResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.ActivityMainBinding
import com.getpy.express.ui.account.WebviewActivity
import com.getpy.express.ui.contactmerchant.ContactMerchantActivity
import com.getpy.express.ui.myorders.MyOrdersActivity
import com.getpy.express.ui.notifications.NotificationActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MainActivity : AppCompatActivity(),KodeinAware
{
    override val kodein by kodein()
    private val appDataBase: AppDataBase by instance()
    private val factory: MainViewModelFactory by instance()
    private val preference:PreferenceProvider by instance()
    var expAdapter:MainCategoryExpAdapter?=null
    var totCatList:ArrayList<CategoriesExpModel>?=null
    private var tcmodel: MerAppSettingsDetailsResponse?=null
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewmodel=ViewModelProviders.of(this,factory).get(MainViewModel::class.java)

        if(UbboFreshApp.instance?.isComingFromReOrder!!)
        {
            lifecycleScope.launch {
                try {
                    UbboFreshApp.instance?.carItemsList?.let { appDataBase.CustomerAddressDao().insertProductsData(it) }
                    setupBadge()
                    UbboFreshApp.instance?.isComingFromReOrder=false
                    binding.bottomNavigationView.selectedItemId=R.id.cartFragment
                }catch (e:CancellationException)
                {
                    Log.i("scope","job is canceled")
                }
                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        }else
        {
            lifecycleScope.launch {
                try {
                    UbboFreshApp.instance?.carItemsList= appDataBase.CustomerAddressDao().getCartData(
                            preference.getIntData(Constants.saveMerchantIdKey).toString(),
                            preference.getStringData(Constants.saveMobileNumkey))
                    setupBadge()
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
        AppCenter.start(
            application, "9e64f71e-a876-4d54-a2ce-3c4c1ea86334",
            Analytics::class.java, Crashes::class.java
        )


        init()
        initialize()
        initializeList()
        getTermsAndCndUrl()


        binding.fab.setOnClickListener(View.OnClickListener {
            binding.activityMainAppbarlayout.showView()
            binding.selectStore.hideView()
            binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
            binding.activityMainToolbarTitle.setText("Offers")
            navcontroller?.navigate(R.id.shoppingFragment)
        })

        binding.searchEdit.setOnClickListener(View.OnClickListener {
            UbboFreshApp.instance?.isSearchBoxclicked=true
            binding.activityMainAppbarlayout.hideView()
            navcontroller?.navigate(R.id.searchFragment)
        })
       //callMainCategories(preference.getIntData(Constants.saveMerchantIdKey))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.activityMainAppbarlayout.showView()
        UbboFreshApp.instance?.isSearchBoxclicked=false
    }

    fun init()
    {
        binding.selectStore.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
        binding.searchEdit.setTypeface(UbboFreshApp.instance?.latoregular)
        //binding.name.setTypeface(UbboFreshApp.instance?.latoregular)
        //binding.welcomeUser.setTypeface(UbboFreshApp.instance?.latoregular)

        binding.activityMainToolbarTitle.setText(getStringData(preference,Constants.saveStorename))
        //binding.navigationRv.setHasFixedSize(true)
        binding.activityMainAppbarlayout.showView()
    }
    fun initialize()
    {
        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false
        navHostfrag=supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        navcontroller=navHostfrag.findNavController()
        appBarConfig= AppBarConfiguration(
            setOf(R.id.homeFragment,R.id.categoriesFragment,
                R.id.shoppingFragment,R.id.cartFragment,R.id.accountFragment),binding.draweLayout)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navcontroller!!, appBarConfig!!)
        binding.bottomNavigationView.setupWithNavController(navcontroller!!)
        binding.navView.setupWithNavController(navcontroller!!)
        NavigationUI.setupWithNavController(binding.navView, navcontroller!!)

        binding.navView.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { menuItem ->
            val id = menuItem.itemId
            //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
            if (id == R.id.home_main) {
                binding.bottomNavigationView.selectedItemId=R.id.homeFragment
            }
            if (id == R.id.categories) {
                binding.bottomNavigationView.selectedItemId=R.id.categoriesFragment
            }
            if (id == R.id.offer_zone) {
                navcontroller?.navigate(R.id.shoppingFragment)
            }
            if (id == R.id.my_orders) {
                val intent= Intent(this, MyOrdersActivity::class.java)
                startActivity(intent)
            }
            if (id == R.id.my_cart) {
                binding.bottomNavigationView.selectedItemId=R.id.cartFragment
            }
            if (id == R.id.account) {
                binding.bottomNavigationView.selectedItemId=R.id.accountFragment
            }
            if (id == R.id.terms) {
                loadTCUrl()
            }
            if(id==R.id.cmerchant)
            {
                val intent= Intent(this, ContactMerchantActivity::class.java)
                startActivity(intent)
            }
            if(id==R.id.notification)
            {
                val intent= Intent(this, NotificationActivity::class.java)
                startActivity(intent)
            }
            //This is for maintaining the behavior of the Navigation view
            NavigationUI.onNavDestinationSelected(menuItem, navcontroller!!)
            //This is for closing the drawer after acting on it
            binding.draweLayout.closeDrawer(GravityCompat.START)
            true
        })
    }
    fun loadTCUrl()
    {
        val map= java.util.HashMap<String, String>()
        map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
        map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
        Analytics.trackEvent("Terms and Conditions clicked", map)

        val model=tcmodel?.data?.merchantdata
        val intent=Intent(this, WebviewActivity::class.java)
        intent.putExtra("url",model?.SettingMessage)
        startActivity(intent)
    }
    fun initializeList()
    {
        totCatList= ArrayList<CategoriesExpModel>()
        if(UbboFreshApp.instance?.carItemsList==null) {
            UbboFreshApp.instance?.carItemsList = ArrayList<ProductsDataModel>()
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        return navcontroller!!.navigateUp(appBarConfig!!) || super.onSupportNavigateUp()
        //return appBarConfig?.let { navcontroller!!.navigateUp(it) }!!
    }
    fun getTermsAndCndUrl()
    {
        lifecycleScope.launch {
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
                okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    fun callMainCategories(merchantid:Int)
    {
        lifecycleScope.launch {
            try {
                val mainAndSuncatModel= MainActivity.viewmodel.MainAndSubGategory(
                        preference.getStringData(Constants.saveaccesskey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        merchantid,
                        "NULL")
                if(mainAndSuncatModel.status?.toLowerCase().equals(Constants.status)) {
                    val list = mainAndSuncatModel.data
                    list?.let { callSubcategories(it, merchantid) }
                }else
                {
                    okDialogWithOneAct(Constants.appName,mainAndSuncatModel.message.toString())
                }
            }catch (e: NoInternetExcetion)
            {
                binding.coordinateLayout.snakBar("Please check network")
                //networkDialog()
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

    fun callSubcategories(list:ArrayList<MainAndSubCatDataModel>,merchantid: Int)
    {
        lifecycleScope.launch {
            try {
                for(i in 0 until list.size)
                {
                    val model=list.get(i)
                    val mainAndSuncatModel= MainActivity.viewmodel.MainAndSubGategory(
                            preference.getStringData(Constants.saveaccesskey),
                            preference.getStringData(Constants.saveMobileNumkey),
                            merchantid,
                            model.SubCategoryId!!
                    )
                    totCatList?.add(CategoriesExpModel(model,mainAndSuncatModel.data))
                }
                expAdapter= totCatList?.let { MainCategoryExpAdapter(preference,this@MainActivity, it) }
                //binding.navigationRv.adapter=expAdapter
            }catch (e: NoInternetExcetion)
            {
                binding.coordinateLayout.snakBar("Please check network")
                //networkDialog()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val menuItem: MenuItem = menu.findItem(R.id.action_cart)
        val actionView: View = menuItem.getActionView()
        menuItem.setVisible(false)
        textCartItemCount = actionView.findViewById<View>(R.id.cart_badge) as TextView
        setupBadge()
        actionView.setOnClickListener { onOptionsItemSelected(menuItem) }
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cart -> {
             binding.bottomNavigationView.selectedItemId=R.id.cartFragment
                // Do something
                return true
            }
            R.id.action_store->{
                if(preference.getIntData(Constants.saveNoOfStores)>1) {
                    okDialogWithNavigateToStore(this,preference,"Are you sure","do you want to switch store?")

                }else
                {
                    okDialogWithOneAct(Constants.appName,"This merchant dont have multiple stores")

                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }



    companion object{
        var navcontroller: NavController?=null
        var appBarConfig: AppBarConfiguration?=null
        lateinit var viewmodel : MainViewModel
        lateinit var binding:ActivityMainBinding
        lateinit var navHostfrag:NavHostFragment
        var textCartItemCount: TextView? = null
        fun setupBadge() {
            if (UbboFreshApp.instance?.carItemsList != null) {
                if (UbboFreshApp.instance?.carItemsList!!.size == 0) {
                    val badgeDrawable = binding.bottomNavigationView.getBadge(R.id.cartFragment)
                    if (badgeDrawable != null) {
                        badgeDrawable.isVisible = false
                        badgeDrawable.clearNumber()
                    }
                    if (textCartItemCount!!.visibility != View.GONE) {
                        textCartItemCount!!.visibility = View.GONE
                    }
                } else {
                    var itemcount=0
                    for(i in 0  until UbboFreshApp.instance?.carItemsList!!.size)
                    {
                        val model=UbboFreshApp.instance?.carItemsList?.get(i)
                        itemcount=itemcount+model!!.itemCount
                    }
                    var badge=binding.bottomNavigationView.getOrCreateBadge(R.id.cartFragment)
                    badge.isVisible=true
                    badge.number=itemcount
                    textCartItemCount!!.text = itemcount.toString()
                    if (textCartItemCount!!.visibility != View.VISIBLE) {
                        textCartItemCount!!.visibility = View.VISIBLE
                    }
                }
            }
        }

    }




}