package com.getpy.fresh.views.Products

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.microsoft.appcenter.analytics.Analytics
import com.getpy.express.listeners.ItemClickListener
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.HorizontalScrollAdapter
import com.getpy.express.data.db.AppDataBase
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.model.MainAndSubCatResponse
import com.getpy.express.data.model.ProductsResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentProductsBinding
import com.getpy.express.listeners.PaginationListener.PAGE_START
import com.getpy.express.ui.Products.ProductItemsFragment
import com.getpy.express.ui.Products.ProductsViewModel
import com.getpy.express.ui.Products.ProductsViewModelFactory
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.express.ui.main.MainActivity
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.kodein.di.generic.instance
import retrofit2.http.Field


class ProductsFragment : InjectionFragment() {
    private val factory: ProductsViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    private val database:AppDataBase by instance()
    lateinit var viewmodel:ProductsViewModel
    var spotlightadater:HorizontalScrollAdapter?=null
    var subCatlist= ArrayList<MainAndSubCatDataModel>()
    var subCatlist1= ArrayList<MainAndSubCatDataModel>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater, R.layout.fragment_products, container, false)
        viewmodel= activity?.let { ViewModelProviders.of(it,factory).get(ProductsViewModel::class.java) }!!
        val view=binding.root
        init()
        binding.viewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }
            override fun onPageSelected(position: Int) {
                binding.progressBar.show()
                itemCount = 0
                currentPage = PAGE_START
                isLastPage = false
                ProductItemsFragment.adapter?.clear()

                val model=subCatlist.get(position)

                val map= HashMap<String, String>()
                map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                map.put("subCtegoryId", model.SubCategoryId.toString())
                map.put("subCategoryName",model.Name.toString())
                Analytics.trackEvent("sub category1 clicked", map)

                callData2(subCatlist.get(position),true)
            }
        })

        if (UbboFreshApp.instance?.isCmgfromHomeItemClick!!) {
            itemCount = 0
            currentPage = PAGE_START
            isLastPage = false
            ProductItemsFragment.adapter?.clear()
            callProductsIfFromHome()
        } else {
            itemCount = 0
            currentPage = PAGE_START
            isLastPage = false
            ProductItemsFragment.adapter?.clear()
            UbboFreshApp.instance?.mainAndSubCatDataModel?.let { callData1(it, false) }
        }


        loadRunnable= Runnable {
            UbboFreshApp.instance?.mainAndSubCatDataModel?.let { reloadProducts(it) }
        }
        runnable= Runnable {
            lifecycleScope.launch {
                try {
                    UbboFreshApp.instance?.carItemsList?.let { database.CustomerAddressDao().insertProductsData(it) }
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
        removerunnable= Runnable {
            lifecycleScope.launch {
                try {
                    UbboFreshApp.instance?.productsDataModel?.let { database.CustomerAddressDao().deleteProductsData(it) }
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
        return view;
    }
    @SuppressLint("ClickableViewAccessibility")
    fun init()
    {
        binding.viewPager.setOnTouchListener(OnTouchListener { arg0, arg1 -> true })

        MainActivity.binding.selectStore.hideView()
        MainActivity.binding.activityMainToolbarTitle.setTypeface(UbboFreshApp.instance?.latoregular)
        MainActivity.binding.activityMainToolbarTitle.setText("Products")

        binding.recyclerview.setLayoutManager(
                LinearLayoutManager(activity,
                        RecyclerView.HORIZONTAL,false))
        binding.recyclerview.setHasFixedSize(true)
    }
    //calling subcategoires
    fun callData1(model:MainAndSubCatDataModel,isScrolling: Boolean)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.progressBar.show()
            try {
                val mainAndSuncatModel=callSubCategories(model.SubCategoryId!!)
                if(mainAndSuncatModel.data!=null)
                {
                    if(mainAndSuncatModel.data!!.size>0) {
                        subCatlist = mainAndSuncatModel?.data!!
                        mainAndSuncatModel.data?.get(0)?.let { callData2(it, isScrolling) }
                    }else
                    {
                       callData3(model,isScrolling)
                    }
                }else
                {
                    callData3(model,isScrolling)
                }

            }catch (e: NoInternetExcetion)
            {
                binding.progressBar.dismiss()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.progressBar.show()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }

    }
    //fetching subcategorie of subcategories data
    fun callData2(model:MainAndSubCatDataModel,isScrolling:Boolean)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val mainAndSuncatModel = model.SubCategoryId?.let { callSubCategories1(it) }
                if(mainAndSuncatModel?.data!=null)
                {
                    if(mainAndSuncatModel.data?.size!!>0)
                    {
                        subCatlist1= mainAndSuncatModel.data!!
                        mainAndSuncatModel.data?.get(0)?.let { callData3(it,isScrolling) }
                    }else
                    {
                        callData3(model,isScrolling)
                    }
                }else
                {
                  callData3(model,isScrolling)
                }
            }catch (e:NoInternetExcetion)
            {
                binding.progressBar.show()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.progressBar.show()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    fun callProductsIfFromHome()
    {
        if(subCatlist.size==0)
        {
            binding.recyclerTabLayout.visibility=View.GONE
            val model=MainAndSubCatDataModel()
            subCatlist.add(model)
        }
        val adater= MyPagerAdater(childFragmentManager, subCatlist)
        binding.viewPager.setAdapter(adater)
        binding.recyclerTabLayout.setTabsFromPagerAdapter(adater)
        binding.recyclerTabLayout.setupWithViewPager(binding.viewPager)
        binding.progressBar.dismiss()
    }
    //fetching products
    fun callData3(smodel:MainAndSubCatDataModel,isScrolling: Boolean)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prodResponse = smodel.Name?.let { callProducts(it) }
                for(i in 0 until prodResponse?.data!!.size)
                {
                    val model=prodResponse.data?.get(i)
                    model?.mobileNumber=preference.getStringData(Constants.saveMobileNumkey)
                }
                UbboFreshApp.instance?.pordlist = prodResponse?.data
                UbboFreshApp.instance?.mainAndSubCatDataModel=smodel
                if(subCatlist.size==0)
                {
                    binding.recyclerTabLayout.visibility=View.GONE
                    val model=MainAndSubCatDataModel()
                    subCatlist.add(model)
                }
                if(!isScrolling) {
                    val adater= MyPagerAdater(childFragmentManager, subCatlist)
                    binding.viewPager.setAdapter(adater)
                    adater.notifyDataSetChanged()
                    binding.recyclerTabLayout.setTabsFromPagerAdapter(adater)
                    binding.recyclerTabLayout.setupWithViewPager(binding.viewPager)
                    if(UbboFreshApp.instance?.isComingfromCatOrMen!!) {
                        UbboFreshApp.instance?.isComingfromCatOrMen=false
                        binding.viewPager.setCurrentItem(UbboFreshApp.instance?.isSubCatPos!!)
                    }
                }else
                {
                    binding.viewPager.adapter?.notifyDataSetChanged()
                }
                if(subCatlist1.size>0)
                {
                    smodel.isSelectedSubTab=true
                    spotlightadater= HorizontalScrollAdapter(subCatlist1,object : ItemClickListener {
                        override fun onItemClick(view: View?, position: Int) {
                            binding.progressBar.show()
                            val model=subCatlist1.get(position)
                            UbboFreshApp.instance?.mainAndSubCatDataModel=smodel
                            itemCount = 0
                            currentPage = PAGE_START
                            isLastPage = false
                            ProductItemsFragment.adapter?.clear()
                            for(i in 0 until subCatlist1.size)
                            {
                                val m=subCatlist1.get(i)
                                if(m.Name.equals(model.Name))
                                {
                                    model.isSelectedSubTab=true
                                }else
                                {
                                    m.isSelectedSubTab=false
                                }
                            }

                            val map= HashMap<String, String>()
                            map.put("mobileNum",preference.getStringData(Constants.saveMobileNumkey))
                            map.put("merchantid", preference.getIntData(Constants.saveMerchantIdKey).toString())
                            map.put("subctegoryId", model.SubCategoryId.toString())
                            map.put("subCategoryName",model.Name.toString())
                            Analytics.trackEvent("sub category2 clicked", map)

                            spotlightadater?.notifyDataSetChanged()
                            getProducts(model,true)
                        }
                    })
                    binding.recyclerview.setAdapter(spotlightadater)
                }

                binding.progressBar.dismiss()

            }catch (e:NoInternetExcetion)
            {
                binding.progressBar.show()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.progressBar.show()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    fun reloadProducts(smodel:MainAndSubCatDataModel)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prodResponse = smodel.Name?.let { callProducts(it) }
                for(i in 0 until prodResponse?.data!!.size)
                {
                    val model=prodResponse.data?.get(i)
                    model?.mobileNumber=preference.getStringData(Constants.saveMobileNumkey)
                }
                UbboFreshApp.instance?.pordlist = prodResponse.data
                UbboFreshApp.instance?.mainAndSubCatDataModel=smodel
                binding.viewPager.adapter?.notifyDataSetChanged()
            }catch (e:NoInternetExcetion)
            {
                binding.progressBar.show()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.progressBar.show()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    fun getProducts(smodel:MainAndSubCatDataModel,isScrolling: Boolean)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prodResponse = smodel.Name?.let { callProducts(it) }
                binding.progressBar.dismiss()
                for(i in 0 until prodResponse?.data!!.size)
                {
                   val model=prodResponse.data?.get(i)
                   model?.mobileNumber=preference.getStringData(Constants.saveMobileNumkey)
                }
                UbboFreshApp.instance?.pordlist = prodResponse.data
                UbboFreshApp.instance?.mainAndSubCatDataModel=smodel
                if(!isScrolling) {
                    val adater= MyPagerAdater(childFragmentManager, subCatlist)
                    binding.viewPager.setAdapter(adater)
                    binding.recyclerTabLayout.setTabsFromPagerAdapter(adater)
                    binding.recyclerTabLayout.setupWithViewPager(binding.viewPager)
                }else
                {
                    binding.viewPager.adapter?.notifyDataSetChanged()
                }
            }catch (e:NoInternetExcetion)
            {
                binding.progressBar.dismiss()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                binding.progressBar.dismiss()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    suspend fun callProducts(categoryname:String):ProductsResponse
    {

        val jsonobject=JSONObject()
        jsonobject.put("merchant_id",preference.getIntData(Constants.saveMerchantIdKey))
        jsonobject.put("phone_number",preference.getStringData(Constants.saveMobileNumkey))
        jsonobject.put("access_key",preference.getStringData(Constants.saveaccesskey))
        jsonobject.put("category_name",categoryname)
        jsonobject.put("page_size",10)
        jsonobject.put("page_number", currentPage)
        jsonobject.put("lastSyncDate","")


        return viewmodel.getProducts(
                    preference.getIntData(Constants.saveMerchantIdKey),
                    preference.getStringData(Constants.saveMobileNumkey),
                    preference.getStringData(Constants.saveaccesskey),
                    categoryname,
                    10,
                     currentPage,
                    ""
            )
    }
    suspend fun callSubCategories(categoryid:String):MainAndSubCatResponse
    {
            return viewmodel.MainAndSubGategory(
                    preference.getStringData(Constants.saveaccesskey),
                    preference.getStringData(Constants.saveMobileNumkey),
                    preference.getIntData(Constants.saveMerchantIdKey),
                    categoryid
            )

    }
    suspend fun callSubCategories1(categoryid: String):MainAndSubCatResponse
    {
            return viewmodel.MainAndSubGategory(
                    preference.getStringData(Constants.saveaccesskey),
                    preference.getStringData(Constants.saveMobileNumkey),
                    preference.getIntData(Constants.saveMerchantIdKey),
                    categoryid
            )

    }

    class MyPagerAdater(val fm:FragmentManager,val list:ArrayList<MainAndSubCatDataModel>) : FragmentStatePagerAdapter(fm)
    {
        private var baseId: Long = 0
        override fun getItem(position: Int): Fragment {
            val fragment= ProductItemsFragment.newInstance("","")
            return fragment
        }

        override fun getCount(): Int {
            return list.size

        }

        override fun getPageTitle(position: Int): CharSequence? {
            val model=list.get(position)
            return model.Name
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE;

        }


        fun getItemId(position: Int): Long {
            // give an ID different from position when position has been changed
            return baseId + position
        }

        /**
         * Notify that the position of a fragment has been changed.
         * Create a new ID for each position to force recreation of the fragment
         * @param n number of items which have been changed
         */
        fun notifyChangeInPosition(n: Int) {
            // shift the ID returned by getItemId outside the range of all previous fragments
            baseId += count + n.toLong()
        }

    }
    companion object
    {
        var runnable:Runnable?=null
        var removerunnable:Runnable?=null
        var loadRunnable:Runnable?=null
        lateinit var binding: FragmentProductsBinding

        var currentPage: Int = PAGE_START
        var isLastPage = false
        var isLoading = false
        var itemCount = 0
    }

}

