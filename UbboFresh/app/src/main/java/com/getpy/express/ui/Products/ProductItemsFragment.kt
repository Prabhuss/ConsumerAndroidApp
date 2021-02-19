package com.getpy.express.ui.Products

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.getpy.express.R
import com.getpy.express.UbboFreshApp
import com.getpy.express.Utils.*
import com.getpy.express.adapter.PostRecyclerAdapter
import com.getpy.express.data.db.entities.ProductsDataModel
import com.getpy.express.data.model.MainAndSubCatDataModel
import com.getpy.express.data.model.ProductsResponse
import com.getpy.express.data.preferences.PreferenceProvider
import com.getpy.express.databinding.FragmentItemsProductBinding
import com.getpy.express.listeners.PaginationListener
import com.getpy.express.ui.home.InjectionFragment
import com.getpy.fresh.views.Products.ProductsFragment
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ShoppingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductItemsFragment() : InjectionFragment() ,SwipeRefreshLayout.OnRefreshListener{
    private var param1: String? = null
    private var param2: String? = null
    private val factory: ProductsViewModelFactory by instance()
    private val preference: PreferenceProvider by instance()
    lateinit var viewmodel:ProductsViewModel
    private var isLoadFirstTime:Boolean=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding =DataBindingUtil.inflate(inflater, R.layout.fragment_items_product, container, false)
        viewmodel= activity?.let { ViewModelProviders.of(it,factory).get(ProductsViewModel::class.java) }!!
        val view= binding.root
        init()
        if(ProductsFragment.currentPage==PaginationListener.PAGE_START) {
            adapter = activity?.let {
                activity?.supportFragmentManager?.let { it1 ->
                    PostRecyclerAdapter(preference,it1, it, ArrayList<ProductsDataModel>())
                }
            }
            adapter?.hashMap= LinkedHashSet()
            binding.recyclerview.setAdapter(adapter)
        }
        if (ProductsFragment.currentPage != PaginationListener.PAGE_START) adapter!!.removeLoading()
        //adapter?.addItems(UbboFreshApp.instance?.pordlist)
        for(i in 0 until UbboFreshApp.instance?.pordlist!!.size)
        {
            val model=UbboFreshApp.instance?.pordlist?.get(i)
            model?.let { adapter?.hashMap?.add(it) }
        }
        adapter?.mPostItems=ArrayList()
        adapter?.hashMap?.let { adapter?.mPostItems?.addAll(it) }
        binding.recyclerview.setAdapter(adapter)
        adapter?.notifyDataSetChanged()
        binding.swipeRefresh.isRefreshing = false
        if(adapter?.itemCount!! >0)
        {
            binding.noProdcuts.hideView()
        }else
        {
            binding.noProdcuts.showView()
        }
        if (UbboFreshApp.instance?.pordlist?.size!!>0) {
            if(UbboFreshApp.instance?.isCmgfromHomeItemClick!!)
            {
                UbboFreshApp.instance?.isCmgfromHomeItemClick=false
                ProductsFragment.isLastPage = true
            }else
            {
                if(UbboFreshApp.instance?.pordlist?.size!!>9)
                {
                    ProductsFragment.isLastPage = false
                    adapter!!.addLoading()
                }else
                {
                    ProductsFragment.isLastPage = false
                }
            }

        } else {
            ProductsFragment.isLastPage = true
        }
        ProductsFragment.isLoading = false

        return view
    }

    fun init()
    {
        val gridLayout = GridLayoutManager(activity?.applicationContext, 2)
        gridLayout.orientation= LinearLayoutManager.VERTICAL
        binding.recyclerview.setItemAnimator(null);
        binding.recyclerview.setLayoutManager(gridLayout)
        binding.recyclerview.setHasFixedSize(true)

        binding.swipeRefresh.setOnRefreshListener(this)
        binding.swipeRefresh.isEnabled=false

        binding.recyclerview.addOnScrollListener(object : PaginationListener(gridLayout) {
            override fun isLastPage(): Boolean {
                return ProductsFragment.isLastPage
            }

            override  protected fun loadMoreItems() {
                ProductsFragment.isLoading = true
                ProductsFragment.currentPage++
                Handler().postDelayed(ProductsFragment.loadRunnable!!,10)

            }

            override fun isLoading(): Boolean {
                return ProductsFragment.isLoading
            }

        })
    }

    fun getProducts(smodel: MainAndSubCatDataModel)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val prodResponse = smodel.Name?.let { callProducts(it) }
                for(i in 0 until prodResponse?.data!!.size)
                {
                    val model=prodResponse.data?.get(i)
                    model?.mobileNumber=preference.getStringData(Constants.saveMobileNumkey)
                }
                val data = prodResponse?.data
                if (ProductsFragment.currentPage != PaginationListener.PAGE_START) adapter!!.removeLoading()
                adapter?.addItems(data)
                binding.swipeRefresh.isRefreshing = false

                if(adapter?.itemCount!! >0)
                {
                    binding.noProdcuts.hideView()
                }else
                {
                    binding.noProdcuts.showView()
                }

                // check weather is last page or not

                // check weather is last page or not
                if (data?.size!!>0) {
                    adapter!!.addLoading()
                } else {
                    ProductsFragment.isLastPage = true
                }
                ProductsFragment.isLoading = false

            }catch (e: NoInternetExcetion)
            {
                ProductsFragment.binding.progressBar.dismiss()
                activity?.networkDialog()
            }catch (e:CancellationException)
            {
                Log.i("scope","job is canceled")
            }
            catch (e:Exception)
            {
                ProductsFragment.binding.progressBar.dismiss()
                activity?.okDialogWithOneAct("Error",e.message.toString())
            }
        }
    }
    suspend fun callProducts(categoryname:String): ProductsResponse
    {
        return viewmodel.getProducts(
                preference.getIntData(Constants.saveMerchantIdKey),
                preference.getStringData(Constants.saveMobileNumkey),
                preference.getStringData(Constants.saveaccesskey),
                categoryname,
                10,
                ProductsFragment.currentPage,
                ""
        )
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShoppingFragment.
         */
        lateinit var  binding: FragmentItemsProductBinding
        var adapter:PostRecyclerAdapter?=null
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onRefresh() {
        binding.swipeRefresh.isRefreshing=false
    }


}