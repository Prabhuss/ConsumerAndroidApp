package com.getpy.fresh.views.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.*
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.model.*
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentHomeBinding
import com.getpy.express.ui.home.HomeViewModelFactory
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.main.MainActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance


class HomeFragment() : InjectionFragment() {
    private val factory: HomeViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    private lateinit var viewmodel : HomeViewModel
    private lateinit var binding: FragmentHomeBinding
    private val appDataBase:AppDataBase by instance()
    private var list:ArrayList<MainAndSubCatDataModel>?=null
    private var spotlightlist:ArrayList<SpotLightModel>?=null
    private var getOfTailsRes:GetOfferTailsResponse?=null
    private var topProductsResponse:ProductsResponse?=null
    private var dealsProductsResponse:ProductsResponse?=null
    @SuppressLint("WrongConstant")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding =DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        viewmodel= ViewModelProviders.of(this,factory).get(HomeViewModel::class.java)

        MainActivity.binding.activityMainAppbarlayout.showView()
        MainActivity.binding.selectStore.showView()
        MainActivity.binding.selectStore.setTypeface(UbboFreshApp.instance?.latoregular)
        MainActivity.binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
        MainActivity.binding.activityMainToolbarTitle.setText(getStringData(preference,Constants.saveStorename))

        spotlightlist= ArrayList();
        val model1=SpotLightModel()
        model1.name="Top Selling"
        spotlightlist?.add(model1)
        val model2=SpotLightModel()
        model2.name="Deals for you"
        spotlightlist?.add(model2)
        binding.spootListRecyler.setHasFixedSize(true)
        binding.spootListRecyler.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        val adapter= activity?.let { SpotLightScrollAdapter(it, spotlightlist!!,object : ItemClickListener {
            override fun onItemClick(view: View?, position: Int) {
                if(position==0)
                {
                    UbboFreshApp.instance?.pordlist=topProductsResponse?.data
                }else
                {
                    UbboFreshApp.instance?.pordlist=dealsProductsResponse?.data
                }
                 UbboFreshApp.instance?.isCmgfromHomeItemClick=true
                 callSubCategories()
            }

        })}
        binding.spootListRecyler.adapter=adapter
        binding.offersRecyclerview.setHasFixedSize(true)
        binding.offersRecyclerview.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        binding.dealsForYou.itemAnimator=null
        binding.dealsForYou.setHasFixedSize(true)
        binding.dealsForYou.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
        binding.topRatedRecyclerview.itemAnimator=null
        binding.topRatedRecyclerview.setHasFixedSize(true)
        binding.topRatedRecyclerview.layoutManager=LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

        binding.catGrid.setOnItemClickListener { adapterView, view, i, l ->
            val model=list?.get(i)
            UbboFreshApp.instance?.mainAndSubCatDataModel=model
            val map=HashMap<String,String>()
            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
            map.put("ProductId", model?.SubCategoryId.toString())
            map.put("ProductName", model?.Name.toString())
            Analytics.trackEvent("Category clicked", map)
            model?.let { callSubCategories() }
        }
        runable = Runnable {
            MainActivity.binding.draweLayout.closeDrawer(Gravity.START)
            callSubCategories()
        }
        addRunnable= Runnable {
            if(activity!=null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        UbboFreshApp.instance?.carItemsList?.let { appDataBase.CustomerAddressDao().insertProductsData(it) }
                    } catch (e: CancellationException) {
                        Log.i("scope", "job is canceled")
                    } catch (e: Exception) {
                        activity?.okDialogWithOneAct("Error", e.message.toString())
                    }

                }
            }
        }
        removeRunnable= Runnable {
            viewLifecycleOwner.lifecycleScope.launch {
                UbboFreshApp.instance?.productsDataModel?.let { appDataBase.CustomerAddressDao().deleteProductsData(it) }
            }
        }
        binding.tailes1Iamge.setOnClickListener(View.OnClickListener {
            UbboFreshApp.instance?.isCmgfromHomeItemClick=true
            val model = getOfTailsRes?.data?.get(0)

            val map=HashMap<String,String>()
            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
            map.put("ProductId", model?.OfferCode.toString())
            map.put("ProductName", model?.OfferName.toString())
            Analytics.trackEvent("tails1 image clicked", map)

            model?.let { it1 -> getofferproducts(it1) }
        })
        binding.tailes2Image.setOnClickListener(View.OnClickListener {
            UbboFreshApp.instance?.isCmgfromHomeItemClick=true
            val model = getOfTailsRes?.data?.get(1)
            val map=HashMap<String,String>()
            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
            map.put("ProductId", model?.OfferCode.toString())
            map.put("ProductName", model?.OfferName.toString())
            Analytics.trackEvent("tails2 image clicked", map)

            model?.let { it1 -> getofferproducts(it1) }
        })
        binding.viewAll.setOnClickListener(View.OnClickListener {
            UbboFreshApp.instance?.isCmgfromHomeItemClick = true
            UbboFreshApp.instance?.pordlist = dealsProductsResponse?.data
            callSubCategories()
        })
        binding.viewallTopSelling.setOnClickListener(View.OnClickListener {
            UbboFreshApp.instance?.isCmgfromHomeItemClick = true
            UbboFreshApp.instance?.pordlist = topProductsResponse?.data
            callSubCategories()
        })
        init()
        binding.pbar.show()
        callSlidingImages()
        callMainCategories(preference.getIntData(Constants.saveMerchantIdKey))
        getOfferDetails()
        getOfferTails()
        getTopDetails()
        getDealsForyou()
        return binding.root;

    }
    fun init()
    {
        binding.categories.setTypeface(UbboFreshApp.instance?.latobold)
        binding.dealsFor.setTypeface(UbboFreshApp.instance?.latobold)
        binding.spotlight.setTypeface(UbboFreshApp.instance?.latobold)
        binding.topSelling.setTypeface(UbboFreshApp.instance?.latobold)
    }

    fun callSlidingImages()
    {
        viewLifecycleOwner.lifecycleScope.launch {

            try {
                binding.viewpager.showView()
                val response=viewmodel.callSlidingImages(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                if(response.status?.toLowerCase().equals(Constants.status)) {
                    binding.viewpager.showView()
                    val adapter = activity?.let { response.data?.let { it1 -> SlidingPagerAdapter(it, it1) } }
                    binding.viewpager.adapter = adapter
                }else
                {
                    binding.viewpager.hideView()
                }
            }catch (e: NoInternetExcetion)
            {
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.viewpager.hideView()
                e.printStackTrace()
                //activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }


    fun getOfferDetails()
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response=viewmodel.getOfferDetails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                if(response.status?.toLowerCase().equals(Constants.status)) {
                    binding.offersRecyclerview.showView()
                    val adapter = activity?.let {
                        response.data?.let { it1 ->
                            HorizontalScrollImagesAdapter(it, it1, object : ItemClickListener {
                                override fun onItemClick(view: View?, position: Int) {
                                    binding.pbar.show()
                                    UbboFreshApp.instance?.isCmgfromHomeItemClick = true
                                    val model = response.data!!.get(position)
                                    val map=HashMap<String,String>()
                                    map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                                    map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                                    map.put("ProductId", model.OfferCode.toString())
                                    map.put("ProductName", model.OfferName.toString())
                                    Analytics.trackEvent("offer details clicked", map)

                                    getofferproducts(model)
                                }

                            })
                        }
                    }
                    binding.offersRecyclerview.adapter = adapter
                }else
                {
                    binding.offersRecyclerview.hideView()
                }
            }catch (e: NoInternetExcetion)
            {
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.offersRecyclerview.showView()
                e.printStackTrace()
                //activity?.okDialogWithOneAct("Error",e.message.toString())
            }

        }
    }
    fun getofferproducts(model:GetOfferDetailsData)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response= viewmodel.getofferProduct(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey),
                        model.OfferCode!!)
                binding.pbar.dismiss()
                if(response.status.toLowerCase().equals(Constants.status)) {
                    UbboFreshApp.instance?.pordlist = response.data
                    callSubCategories()
                }else
                {
                    if(response.message.toLowerCase().equals(Constants.message))
                    {
                        activity?.okDialogWithNavigateToLogin(requireActivity(),preference,Constants.appName,response.message)
                    }else
                    {
                        activity?.okDialogWithOneAct("Error",response.message)
                    }
                }
            }catch (e: NoInternetExcetion)
            {
                binding.pbar.dismiss()
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                binding.pbar.dismiss()
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.pbar.dismiss()
                e.printStackTrace()
                activity?.okDialogWithOneAct("Error","There is no offer data")
            }

        }

    }
    @SuppressLint("UseRequireInsteadOfGet")
    fun getofferproducts(model:GetOfferTailsData)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response= viewmodel.getofferProduct(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey),
                        model.OfferCode!!)
                if(response.status.toLowerCase().equals(Constants.status)) {
                    UbboFreshApp.instance?.pordlist = response.data
                    callSubCategories()
                }else
                {
                    if(response.message.toLowerCase().equals(Constants.message))
                    {
                        activity?.okDialogWithNavigateToLogin(requireActivity(),preference,Constants.appName,response.message)
                    }else
                    {
                        activity?.okDialogWithOneAct(Constants.appName, response.message)
                    }
                }
            }catch (e: NoInternetExcetion)
            {
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                e.printStackTrace()
                //activity?.okDialogWithOneAct("Error",e.message.toString())
            }

        }

    }
    fun getOfferTails()
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                getOfTailsRes=viewmodel.getOfferTails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                if(getOfTailsRes?.status?.toLowerCase().equals(Constants.status)) {
                    for (i in 0 until getOfTailsRes?.data!!.size) {
                        val model = getOfTailsRes?.data!!.get(i)
                        if (i == 0) {
                            binding.tailes1Iamge.showView()
                            binding.tailes2Image.hideView()
                            binding.tailes1Iamge.load(model.OfferImgURL) {
                                placeholder(R.drawable.ic_no_image_found)
                                error(R.drawable.ic_no_image_found)
                            }
                        } else if (i == 1) {
                            binding.tailes2Image.showView()
                            binding.tailes2Image.load(model.OfferImgURL) {
                                placeholder(R.drawable.ic_no_image_found)
                                error(R.drawable.ic_no_image_found)
                            }
                        }
                    }
                }else
                {
                    binding.tailes1Iamge.hideView()
                    binding.tailes2Image.hideView()
                }
            }catch (e: NoInternetExcetion)
            {
                //MainActivity.binding.coordinateLayout.snakBar("Please check network")
               // activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.tailes1Iamge.hideView()
                binding.tailes2Image.hideView()
                e.printStackTrace()
                //activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    fun getTopDetails()
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                topProductsResponse=viewmodel.getTopDetails(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                if(topProductsResponse?.status?.toLowerCase().equals(Constants.status)) {
                    binding.topSelling.showView()
                    binding.topRatedRecyclerview.showView()
                    binding.spotLayout.showView()
                    binding.spootListRecyler.showView()
                    for(i in 0 until topProductsResponse?.data!!.size)
                    {
                        val model=topProductsResponse?.data?.get(i)
                        model?.mobileNumber=preference.getStringData(Constants.saveMobileNumkey)
                    }
                    if(topProductsResponse?.data!!.size==1)
                    {
                        topProductsResponse
                                ?.data?.add(ProductsDataModel())
                    }
                    tophadapter = activity?.let {
                        topProductsResponse?.data?.let { it1 ->
                            HorizontalProductItemsAdapter(preference,activity?.supportFragmentManager!!,it, it1, object : ItemClickListener {
                                override fun onItemClick(view: View?, position: Int) {
                                    if (position == (it1.size-1)) {
                                        UbboFreshApp.instance?.isCmgfromHomeItemClick = true
                                        if(topProductsResponse?.data?.size==2)
                                        {
                                            val model=topProductsResponse?.data?.get(1)
                                            if(model?.citrineProdId==null)
                                            {
                                                topProductsResponse?.data?.removeAt(1)
                                            }
                                        }
                                        UbboFreshApp.instance?.pordlist = topProductsResponse?.data
                                        callSubCategories()
                                    }

                                }
                            })
                        }
                    }
                    binding.topRatedRecyclerview.adapter = tophadapter
                }else
                {
                    binding.viewallTopSelling.hideView()
                    binding.topSelling.hideView()
                    binding.topRatedRecyclerview.hideView()
                }
            }catch (e: NoInternetExcetion)
            {
                //MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                if(topProductsResponse?.message?.toLowerCase().equals(Constants.message))
                {
                    binding.viewallTopSelling.hideView()
                    binding.topSelling.hideView()
                }else
                {
                    binding.spotLayout.hideView()
                    binding.spootListRecyler.hideView()
                    binding.viewallTopSelling.hideView()
                    binding.topSelling.hideView()
                    //activity?.okDialogWithOneAct("Error",e.message.toString())
                }
            }

        }
    }
    fun getDealsForyou()
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                dealsProductsResponse=viewmodel.getDealsForyou(
                        preference.getIntData(Constants.saveMerchantIdKey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        preference.getStringData(Constants.saveaccesskey))
                if(dealsProductsResponse?.status?.toLowerCase().equals(Constants.status)) {
                    binding.dealsFor.showView()
                    binding.dealsForYou.showView()
                    binding.spotLayout.showView()
                    binding.spootListRecyler.showView()
                    for(i in 0 until dealsProductsResponse?.data!!.size)
                    {
                        val model=dealsProductsResponse?.data?.get(i)
                        model?.mobileNumber=preference.getStringData(Constants.saveMobileNumkey)
                    }
                    if(dealsProductsResponse?.data!!.size==1)
                    {
                        dealsProductsResponse?.data?.add(ProductsDataModel())
                    }
                    dealshadapter= activity?.let {
                        dealsProductsResponse?.data?.let { it1 ->
                            HorizontalProductItemsAdapter(preference,activity?.supportFragmentManager!!,it, it1, object : ItemClickListener {
                                override fun onItemClick(view: View?, position: Int) {
                                    if (position == (it1.size-1)) {
                                        UbboFreshApp.instance?.isCmgfromHomeItemClick = true
                                        if(dealsProductsResponse?.data?.size==2)
                                        {
                                            val model=dealsProductsResponse?.data?.get(1)
                                            if(model?.citrineProdId==null)
                                            {
                                                dealsProductsResponse?.data?.removeAt(1)
                                            }
                                        }
                                        UbboFreshApp.instance?.pordlist = dealsProductsResponse?.data
                                        callSubCategories()
                                    }
                                }
                            })
                        }
                    }
                    binding.dealsForYou.adapter = dealshadapter
                }else
                {
                    binding.viewAll.hideView()
                    binding.dealsFor.hideView()
                    binding.dealsForYou.hideView()
                }
            }catch (e: NoInternetExcetion)
            {
                //MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                if(dealsProductsResponse?.message?.toLowerCase().equals(Constants.message))
                {
                    binding.viewAll.hideView()
                    binding.dealsFor.hideView()
                }else
                {
                    binding.spotLayout.hideView()
                    binding.spootListRecyler.hideView()
                    binding.viewAll.hideView()
                    binding.dealsFor.hideView()
                    //activity?.okDialogWithOneAct("Error",e.message.toString())
                }
            }

        }
    }

    fun callMainCategories(merchantid:Int)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val mainAndSuncatModel=viewmodel.MainAndSubGategory(
                        preference.getStringData(Constants.saveaccesskey),
                        preference.getStringData(Constants.saveMobileNumkey),
                        merchantid,
                        "NULL"
                )
                binding.pbar.dismiss()
                if(mainAndSuncatModel.status?.toLowerCase().equals(Constants.status)) {
                    if (mainAndSuncatModel?.data != null) {
                        list = mainAndSuncatModel?.data
                        val adater = activity?.let { mainAndSuncatModel?.data?.let { it1 -> HomeCategoriesGridAdapter(it, it1) } }
                        binding.catGrid.adapter = adater
                        setDynamicHeight(binding.catGrid)
                    } else {
                        mainAndSuncatModel.message?.let { activity?.okDialogWithOneAct("Error", it) }
                    }
                }else
                {
                    if(mainAndSuncatModel.message?.toLowerCase().equals(Constants.message))
                    {
                        mainAndSuncatModel.message?.let { activity?.okDialogWithNavigateToLogin(requireActivity(),preference,Constants.appName, it) }
                    }else
                    {
                        activity?.okDialogWithOneAct(Constants.appName,mainAndSuncatModel.message.toString())
                    }
                }
            }catch (e: NoInternetExcetion)
            {
                binding.pbar.dismiss()
                MainActivity.binding.coordinateLayout.snakBar("Please check network")
                //activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.pbar.dismiss()
                e.printStackTrace()
                //activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    private fun setDynamicHeight(gridView: GridView) {
        val gridViewAdapter = gridView.adapter
            ?: // pre-condition
            return
        var totalHeight = 0
        var items = gridViewAdapter.count

        if(items<=5)
        {
            items=5
        }else if(items<=10)
        {
            items=10
        }else if(items<15)
        {
            items=15
        }else if(items<=20)
        {
            items=20
        }else if(items<=25)
        {
            items=25
        }else if(items<=30)
        {
            items=30
        }
        val nocol=gridView.numColumns
        var rows = 0
        val listItem = gridViewAdapter.getView(0, null, gridView)
        listItem.measure(0, 0)
        totalHeight = listItem.measuredHeight
        var x = 1f
        if (items > nocol) {
            x = items / nocol.toFloat()
            rows = (x + 1).toInt()
            totalHeight *= rows
        }
        val params = gridView.layoutParams
        params.height = (totalHeight)
        gridView.layoutParams = params
    }
    fun callSubCategories()
    {
        if(MainActivity.navcontroller?.currentDestination?.id==R.id.homeFragment) {
            val action = HomeFragmentDirections.actionHomeFragmentToProductsFragment()
            findNavController().navigate(action)
        }
    }


    companion object{
        var runable:Runnable?=null
        var addRunnable:Runnable?=null
        var removeRunnable:Runnable?=null
        var tophadapter:HorizontalProductItemsAdapter?=null
        var dealshadapter:HorizontalProductItemsAdapter?=null

    }

}